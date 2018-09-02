/*
 * Copyright 2018 org.dpr & croger
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
import diskong.core.IAlbumVo;
import diskong.core.TrackInfo;
import io.nayuki.flac.common.StreamInfo;
import io.nayuki.flac.decode.FlacDecoder;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FlacPlayer  implements Player, GuiListener {
    private List<EventListener> listeners = new ArrayList<EventListener>();
    private IAlbumVo album;
    public Listener listener;
    final double[] seekRequest = {-1};

    public FlacPlayer(IAlbumVo album) {
        this.album = album;
    }

    public void playAlbum() {
        for (TrackInfo track : album.getTracks()) {
            try {
                playTrack(track);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }

    }

    private void playTrack(@NotNull TrackInfo track) throws InterruptedException, IOException, LineUnavailableException {
        play(track.getfPath().getFile());
    }

    private void playFile(Path path) {

    }


    public void playTrack(int numTrack) {

    }

    private void play(File inFile) throws
            LineUnavailableException, IOException, InterruptedException {


        // Process header metadata blocks
        FlacDecoder decoder = new FlacDecoder(inFile);
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





        listener = new Listener() {
            public void seekRequested(double t) {
                synchronized (seekRequest) {
                    seekRequest[0] = t;
                    seekRequest.notify();
                }
            }

            public void windowClosing() {
                System.exit(0);
            }

            @Override
            public void pauseRequested() {
                seekRequest[0] = -2;
                seekRequest.notify();
            }

            @Override
            public void setPosition(double v) {
                notifySomethingHappened(v);
            }
        };

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
                if (seekReq != -2)
                    seekRequest[0] = -1;
            }

            // Decode next audio block, or seek and decode
            int blockSamples;
            if (seekReq == -2)
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
                synchronized (seekRequest) {
                    while (seekRequest[0] == -1)
                        seekRequest.wait();
                }
                continue;
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

    @Override
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    @Override
    public GuiListener getListener() {
        return new GuiListenerImpl();
    }

    void notifySomethingHappened(double v){
        for(EventListener listener : listeners){
            listener.somethingHappened(v);
        }
    }


    @Override
    public void seekRequested(double t) {

    }

    @Override
    public void pauseRequested() {

    }

    public interface Listener {

        public void seekRequested(double t);  // 0.0 <= t <= 1.0

        public void windowClosing();

        public void pauseRequested();


        public void setPosition(double v);
    }

    public class GuiListenerImpl implements  GuiListener{

        @Override
        public void seekRequested(double t) {
            synchronized (seekRequest) {
                seekRequest[0] = t;
                seekRequest.notify();
            }
        }

        @Override
        public void pauseRequested() {

        }
    }


}

