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

package diskong.app.services;

import diskong.AlbumFactory;
import diskong.Statistics;
import diskong.app.track.TrackEntity;
import diskong.app.track.TrackServiceImpl;
import diskong.core.*;
import diskong.core.bean.AlbumVo;
import diskong.core.bean.TrackInfo;
import diskong.parser.AudioParser;
import diskong.parser.CallTrackInfo;
import diskong.parser.MetaUtils;
import diskong.parser.TikaAudioParser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static diskong.core.bean.AlbumVo.TAG_ALBUM_ARTIST;

@Service
public class AudioService {

    private final static Logger LOG = LoggerFactory.getLogger(AudioService.class);
    private static int NBCHECK = 200;
    AlbumService albumService = new AlbumService();
    AudioParser autoParser=new TikaAudioParser();

    @Autowired
    private TrackServiceImpl trackService;

    public AudioService() throws Exception {
        System.out.println("creation audioservice");
    }


    /**
     * Parse files with audio content in directory
     * @param entry
     * @return
     */
    public AlbumVo parseDirectoryForAlbum(Map.Entry<Path, List<FilePath>> entry)  {
        //FIXME multi albums in one directory
        AlbumVo album = AlbumFactory.getAlbum();

            // parsedir
            LOG.debug("iteration");

            album.setState(diskong.core.TagState.TOTAG);
            LOG.debug("**************START******************************");
            ExecutorService executor = Executors.newFixedThreadPool(10);
            List<Future<TrackInfo>> list = new ArrayList<>();
            for (FilePath fPath : entry.getValue()) {
                LOG.debug(fPath.getFile().getAbsolutePath());
                Callable<TrackInfo> worker = new CallTrackInfo(fPath, autoParser);
                Future<TrackInfo> submit = executor.submit(worker);
                list.add(submit);
            }

            for (Future<TrackInfo> future : list) {
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

    /**
     * Parse files with audio content in directory
     * @param entry
     * @return
     */
    public List<AlbumVo> parseDirectory(Map.Entry<Path, List<FilePath>> entry)  {
        //FIXME multi albums in one directory
        //AlbumVo album = AlbumFactory.getAlbum();
        Map<String, AlbumVo> albums = new HashMap<>();
        // parsedir
        LOG.debug("iteration");

        //album.setState(diskong.core.TagState.TOTAG);
        LOG.debug("**************START******************************");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<TrackInfo>> list = new ArrayList<>();
        for (FilePath fPath : entry.getValue()) {
            LOG.debug(fPath.getFile().getAbsolutePath());
            Callable<TrackInfo> worker = new CallTrackInfo(fPath, autoParser);
            Future<TrackInfo> submit = executor.submit(worker);
            list.add(submit);
        }

        for (Future<TrackInfo> future : list) {
            try {
                TrackInfo tinf = future.get();
                addTrack(albums, tinf); // (metafile)
            } catch (InterruptedException | ExecutionException e) {
                LOG.error("fail to add track",e);
            } catch (WrongTrackAlbumException e) {
                // put track in right album
                AlbumFactory.orderingTrack(e.getTrack());
            }

        }
        executor.shutdown();

        return new ArrayList<>(albums.values());
        //Statistics.getInstance().addStats(album);

    }

    public void addTrack(AlbumVo album, TrackInfo trackInfo) throws WrongTrackAlbumException {
        addTrack(album, trackInfo.getfPath(), trackInfo.getMetadata());

    }
    public void addTrack(Map<String, AlbumVo> albums, TrackInfo trackInfo) throws WrongTrackAlbumException {
        FilePath fPath = trackInfo.getfPath();
        Metadata metadata = trackInfo.getMetadata();

        // check if all tracks in folder belong to same album
        if (metadata.get(Metadata.CONTENT_TYPE).contains("flac") || metadata.get(Metadata.CONTENT_TYPE).contains("vorbis")) {
            AlbumVo album = initAlbum(albums, metadata);
            if (album.getTitle() == null) {
                album.setTitle(metadata.get(XMPDM.ALBUM));
            }
            if (album.getArtist() == null) {
                album.setArtist(metadata.get(TAG_ALBUM_ARTIST));
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
            TrackInfo createdTrack = new TrackInfo(fPath, metadata);
            album.getTracks().add(createdTrack);
            trackService.create(new TrackEntity(createdTrack));

            //FIXME
//        } else if (metadata.get(Metadata.CONTENT_TYPE).contains("image") ){
//            if (fPath.getFile().getName().toLowerCase().contains("folder") || fPath.getFile().getName().toLowerCase().contains("cover"))
//            {
//                LOG.debug("cover image found " + fPath.getFile().getName());
//                album.setFolderImagePath(fPath.getFile().getAbsolutePath());
//            }
//            else{
//                album.getArts().add(fPath);
//            }
        } else {
            LOG.debug("type de fichier non géré:" + metadata.get(Metadata.CONTENT_TYPE));
        }

    }

    private AlbumVo initAlbum(Map<String, AlbumVo> albums, Metadata metadata) {
        String title = metadata.get(XMPDM.ALBUM);
        if (title == null)
            title = AlbumVo.UNKNOWN;
        String artist = metadata.get(TAG_ALBUM_ARTIST);
        System.out.println(artist);
        if (artist == null)
            artist = metadata.get(XMPDM.ARTIST);
        System.out.println(artist);
        if (artist == null)
            artist = AlbumVo.UNKNOWN;
        System.out.println(artist);
        String key = DigestUtils.sha1Hex(artist+"---"+title);
        if (albums.get(key)!=null){
            return albums.get(key);
        }else{
            AlbumVo album = AlbumFactory.getAlbum();
            album.setArtist(artist);
            album.setTitle(title);
            albums.put(key, album);
            return album;
        }
    }
    //AlbumVo album = AlbumFactory.getAlbum();

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
