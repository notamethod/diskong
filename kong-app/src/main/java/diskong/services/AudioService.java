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

package diskong.services;

import diskong.AlbumFactory;
import diskong.Statistics;
import diskong.core.*;
import diskong.parser.AudioParser;
import diskong.parser.CallTrackInfo;
import diskong.parser.MetaUtils;
import diskong.parser.TikaAudioParser;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class AudioService {

    private final static Logger LOG = LoggerFactory.getLogger(AudioService.class);
    private static int NBCHECK = 200;
    AlbumService albumService = new AlbumService();
    AudioParser autoParser=new TikaAudioParser();

    public AudioService() throws Exception {
    }


    /**
     * Parse files with audio content in directory
     * @param entry
     * @return
     */
    public diskong.core.AlbumVo parseDirectory(Map.Entry<Path, List<FilePath>> entry)  {

        diskong.core.AlbumVo album = AlbumFactory.getAlbum();

            // parsedir
            LOG.debug("iteration");

            album.setState(diskong.core.TagState.TOTAG);
            LOG.debug("**************START******************************");
            ExecutorService executor = Executors.newFixedThreadPool(10);
            List<Future<diskong.core.TrackInfo>> list = new ArrayList<>();
            for (FilePath fPath : entry.getValue()) {
                LOG.debug(fPath.getFile().getAbsolutePath());
                Callable<diskong.core.TrackInfo> worker = new CallTrackInfo(fPath, autoParser);
                Future<diskong.core.TrackInfo> submit = executor.submit(worker);
                list.add(submit);
            }

            for (Future<diskong.core.TrackInfo> future : list) {
                try {
                    TrackInfo tinf = future.get();
                    addTrack(album,tinf); // (metafile)
                } catch (InterruptedException | ExecutionException e) {
                    LOG.error("fail to add track",e);
                } catch (WrongTrackAlbumException e) {
                    // put track in right album
                    AlbumFactory.orderingTrack(e.getTrack());
                }

            }
            executor.shutdown();

            if (album.getTracks().isEmpty()) {
                LOG.info("Album or folder without tracks:" + album.toString() + album.getTracks().size());
                album.setState(diskong.core.TagState.NOTRACKS);
            }
            else
                LOG.info("Album parsed:" + album.toString() + album.getTracks().size());
            //model.setAlbums(albums);
            Statistics.getInstance().addStats(album);

        return album;
    }

    public void addTrack(AlbumVo album, TrackInfo trackInfo) throws WrongTrackAlbumException {
        addTrack(album, trackInfo.getfPath(), trackInfo.getMetadata());

    }

    private void addTrack(AlbumVo album, FilePath fPath, Metadata metadata) throws WrongTrackAlbumException {
        // check if all tracks in folder belong to same album
        if (metadata.get(Metadata.CONTENT_TYPE).contains("flac") || metadata.get(Metadata.CONTENT_TYPE).contains("vorbis")) {
            if (album.getTitle() == null) {
                album.setTitle(metadata.get(XMPDM.ALBUM));
            } else if (!album.getTitle().equals(metadata.get(XMPDM.ALBUM))) {
                // wrong album ?
                throw new WrongTrackAlbumException(metadata);
            }
            if (album.getArtist() == null) {
                album.setArtist(metadata.get(XMPDM.ARTIST));
            } else if (!album.getArtist().equals(metadata.get(XMPDM.ARTIST))) {
                // wrong artist or various ?
                album.setArtist(AlbumVo.VARIOUS);
                //throw new WrongTrackArtistException(metadata);
            }
            if (album.getGenres() == null) {
                album.setGenres(MetaUtils.getGenre(metadata));
            }
            if (album.getGenres().isEmpty()){
                album.getGenres().addAll(MetaUtils.getGenre(metadata));
            }

            if (album.getStyles() == null) {
                album.setStyles(MetaUtils.getStyle(metadata));
            }

            if (album.getStyles().isEmpty()){
                album.getStyles().addAll(MetaUtils.getStyle(metadata));
            }

            if (album.getReleaseDate() == null) {
                //may be a date or a year (afaik)
                String dateInString = metadata.get(XMPDM.RELEASE_DATE);
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (dateInString != null) {

                    try {
                        album.setReleaseDate(LocalDate.parse(dateInString, dtf));
                    } catch (DateTimeParseException e) {
                        try {
                            int a = Integer.parseInt(dateInString);
                            album.setReleaseDate(LocalDate.parse(dateInString + "-01-01", dtf));
                        } catch (DateTimeParseException | NumberFormatException e1) {
                            System.out.println("date error " + dateInString);
                        }
                    }
                }
            }

            album.getTracks().add(new TrackInfo(fPath, metadata));

        } else if (metadata.get(Metadata.CONTENT_TYPE).contains("image") ){
            if (fPath.getFile().getName().toLowerCase().contains("folder") || fPath.getFile().getName().toLowerCase().contains("cover"))
            {
                LOG.debug("cover image found " + fPath.getFile().getName());
                album.setFolderImagePath(fPath.getFile().getAbsolutePath());
            }
            else{
                album.getArts().add(fPath);
            }
        } else {
            LOG.debug("type de fichier non géré:" + metadata.get(Metadata.CONTENT_TYPE));
        }
    }

    private boolean contineParse() {
        return true;
    }

    /**
     * @param album
     */
    private boolean checkAction(AlbumVo album) {

        boolean forceRetag = false;
        // if (forceRetag){
        // return ;
        // }
        boolean isOK = TrackUtils.isAllOK(album);
        if (album.getState().equals(diskong.core.TagState.TOTAG) && !forceRetag && isOK) {
            LOG.debug(
                    "album " + album.getTitle() + " not parsed: state:" + album.getState() + " all tags found:" + isOK);

            return false;
        }
        if (!album.getState().equals(TagState.TOTAG) || (!forceRetag && isOK)) {
            LOG.warn(
                    "album " + album.getTitle() + " not parsed: state:" + album.getState() + " all tags found:" + isOK);

            return false;
        }

        return true;
    }


}
