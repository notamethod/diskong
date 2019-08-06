/*
 * Copyright 2019 org.dpr & croger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package diskong.app;


import diskong.api.EventListener;
import diskong.api.GuiListener;
import diskong.app.Player;
import diskong.core.bean.IAlbumVo;
import diskong.core.bean.TrackInfo;
import io.nayuki.flac.common.StreamInfo;
import io.nayuki.flac.decode.FlacDecoder;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AlbumFlacPlayer implements Player {
    private List<EventListener> listeners = new ArrayList<>();
    private IAlbumVo album;
    public Listener listener;
    final double[] seekRequest = {-1};
    private boolean hasNext;
    private boolean hasPrevious;

    public final int RESUME = -1;
    public final int PAUSE = -2;
    public final int NEXT = -3;
    public final int PREVIOUS = -4;

    public AlbumFlacPlayer(IAlbumVo album) {
        this.album = album;
    }

    public void playAlbum() {
        playAlbum(0);
    }
    public void playAlbum(int firstToPlay) {
        int playedTrack = firstToPlay;
        List<TrackInfo> tracks = album.getTracks();
        int numTracks = tracks.size();
        try {
       while (playedTrack>=0){
           int returnedPlayInfo = play(tracks.get(playedTrack).getfPath().getFile(), playedTrack<=numTracks, playedTrack);
           if (returnedPlayInfo == NEXT) {
               System.out.println("next req");
               playedTrack++;
           }
           if (returnedPlayInfo == PREVIOUS && playedTrack>0) {
               System.out.println("prev req");
               playedTrack--;
           }
           if (returnedPlayInfo >=0) {
               System.out.println("play track req");
               playedTrack = returnedPlayInfo;
           }
       }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }

//    private int playTrack(@NotNull TrackInfo track) throws InterruptedException, IOException, LineUnavailableException {
//
//        return play(track.getfPath().getFile(), false);
//    }

    private void playFile(Path path) {

    }


    public void playTrack(int numTrack) {

    }

    private int play(File inFile, boolean hasNext, int track) throws
            LineUnavailableException, IOException, InterruptedException {

        listener = new Listener() {

            @Override
            public void setPosition(double v) {
                notifyComponentToUpdate(v);
            }

            @Override
            public void selectRow(int row) {
                notifyTableToUpdate(row);
            }
        };
        if (track>=0)
            listener.selectRow(track);
        // Process header metadata blocks
        try (FlacDecoder decoder = new FlacDecoder(inFile)) {
            while (decoder.readAndHandleMetadataBlock() != null) ;
            StreamInfo streamInfo = decoder.streamInfo;
            if (streamInfo.numSamples == 0)
                throw new IllegalArgumentException("Unknown audio length");

            // Start Java sound output API
            AudioFormat format = new AudioFormat(streamInfo.sampleRate,
                    streamInfo.sampleDepth, streamInfo.numChannels, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            // Create GUI object, event handler, communication object


            /*-- Audio player loop --*/

            // Decode and write audio data, handle seek requests, wait for seek when end of stream reached
            int bytesPerSample = streamInfo.sampleDepth / 8;
            long startTime = line.getMicrosecondPosition();

            // Buffers for data created and discarded within each loop iteration, but allocated outside the loop
            int[][] samples = new int[streamInfo.numChannels][65536];
            byte[] sampleBytes = new byte[65536 * streamInfo.numChannels * bytesPerSample];
            while (true) {

                // Get and clear seek request, if any
                double seekReq;
                synchronized (seekRequest) {
                    seekReq = seekRequest[0];
                    if (seekReq > -2)
                        seekRequest[0] = -1;
                }

                // Decode next audio block, or seek and decode
                int blockSamples;
                if (seekReq == NEXT && hasNext) {
                    line.stop();
                    synchronized (seekRequest) {
                            seekRequest[0] = -1;
                    }
                    return NEXT;
                } else if (seekReq == PREVIOUS) {
                    line.stop();
                    synchronized (seekRequest) {
                        seekRequest[0] = -1;
                    }
                    return PREVIOUS;
                } else if (seekReq <= -900) {
                    line.stop();
                    synchronized (seekRequest) {
                        seekRequest[0] = -1;
                    }
                    return (int) (seekReq + 1000);
                } else if (seekReq == PAUSE)
                    continue;
                else if (seekReq == -1)
                    blockSamples = decoder.readAudioBlock(samples, 0);
                else {
                    long samplePos = Math.round(seekReq * streamInfo.numSamples);
                    seekReq = -1;
                    blockSamples = decoder.seekAndReadAudioBlock(samplePos, samples, 0);
                    line.flush();
                    startTime = line.getMicrosecondPosition() - Math.round(samplePos * 1e6 / streamInfo.sampleRate);
                }

                // Set display position
                double timePos = (line.getMicrosecondPosition() - startTime) / 1e6;
                listener.setPosition(timePos * streamInfo.sampleRate / streamInfo.numSamples);

                // Wait when end of stream reached
                if (blockSamples == 0) {
                    if (hasNext) {
                        return NEXT;
                    } else {
                        synchronized (seekRequest) {
                            while (seekRequest[0] == -1) {
                                seekRequest.wait();
                            }
                        }
                        continue;
                    }


                }

                // Convert samples to channel-interleaved bytes in little endian
                int sampleBytesLen = 0;
                for (int i = 0; i < blockSamples; i++) {
                    for (int ch = 0; ch < streamInfo.numChannels; ch++) {
                        int val = samples[ch][i];
                        for (int j = 0; j < bytesPerSample; j++, sampleBytesLen++)
                            sampleBytes[sampleBytesLen] = (byte) (val >>> (j << 3));
                    }
                }
                line.write(sampleBytes, 0, sampleBytesLen);
            }
        }

    }

    @Override
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    @Override
    public GuiListener getPlayerListener() {
        return new GuiListenerImpl();
    }

    void notifyComponentToUpdate(double v) {
        for (EventListener listener : listeners) {
            listener.componentUpdateRequested(v);
        }
    }
    void notifyTableToUpdate(int row) {
        for (EventListener listener : listeners) {
            listener.TableUpdateRequested(row);
        }
    }

    public interface Listener {

        void setPosition(double v);
        void selectRow(int row);
    }

    public class GuiListenerImpl implements GuiListener {

        @Override
        public void seekRequested(double t) {
            synchronized (seekRequest) {
                seekRequest[0] = t;
                seekRequest.notify();
            }
        }

        @Override
        public void pauseRequested() {
            synchronized (seekRequest) {
                seekRequest[0] = PAUSE;
                seekRequest.notify();
            }
        }

        @Override
        public void resumeRequested() {
            synchronized (seekRequest) {
                seekRequest[0] = -1;
                seekRequest.notify();
            }
        }

        @Override
        public void nextRequested() {
            synchronized (seekRequest) {
                seekRequest[0] = NEXT;
                seekRequest.notify();
            }
        }

        @Override
        public void previousRequested() {
            synchronized (seekRequest) {
                seekRequest[0] = PREVIOUS;
                seekRequest.notify();
            }

        }

        @Override
        public void selectTrackRequested(int row) {
            synchronized (seekRequest) {
                seekRequest[0] = -1000+row;
                seekRequest.notify();
            }

        }

        @Override
        public void stopRequested() {
            //does nothing !
        }
    }

    //TODO volume control
    //FloatControl volume= (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);

}

