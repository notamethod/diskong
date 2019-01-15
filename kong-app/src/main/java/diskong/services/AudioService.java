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
import diskong.gui.AlbumModel;
import diskong.parser.AudioParser;
import diskong.parser.CallTrackInfo;
import diskong.parser.MetaUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.AutoDetectParser;
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
    AudioParser autoParser=new AudioParser();

    public AudioService() throws Exception {
    }


    public List<diskong.core.AlbumVo> traiterDir(Map<Path, List<FilePath>> map, AlbumModel model) throws Exception {
        List<diskong.core.AlbumVo> albums = new ArrayList<>();
        int checkTagged = 0;
        int taggedTrack = 0;
        try {
            AudioParser ap = new AudioParser();
        } catch (Exception e) {
            //FIXME
            e.printStackTrace();
        }
        for (Map.Entry<Path, List<FilePath>> entry : map.entrySet()) {
            long startTime = System.currentTimeMillis();

            //continue or stop asked every NBCHECK parsed files
            if (checkTagged >= NBCHECK) {
                if (contineParse()) {
                    checkTagged = 0;
                } else {
                    return albums;
                }
            }
            //FIXME:check parser creation
            diskong.core.AlbumVo avo =  parseDirectory(entry);
            if (!avo.getState().equals(TagState.NOTRACKS)) {
                albums.add(avo);
                model.setAlbums(albums);
            }


            long endTime = System.currentTimeMillis();
            LOG.debug(" files metaparsed in " + (endTime - startTime) + " ms");
            if (Thread.interrupted()) {
                System.out.println("xxxxxxxxxxxxxxxxxx");
            }

        }
        return albums;
    }

    public diskong.core.AlbumVo parseDirectory(Map.Entry<Path, List<FilePath>> entry) throws Exception {

        diskong.core.AlbumVo album = AlbumFactory.getAlbum();


        try {
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
                    e.printStackTrace();
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
//                if (checkAction(album)) {
//                    IAlbumVo alInfos = albumService.searchAlbum(album);
//                    checkTagged += albumService.actionOnAlbum(album, alInfos);
//                    taggedTrack += checkTagged;
//                }

//				1 <
//						{"pagination": {"per_page": 50, "items": 1, "page": 1, "urls": {}, "pages": 1}, "results": [{"style": ["Alternative Rock", "Power Pop"], "thumb": "https://img.discogs.com/h--kMjRx67LHlr9H2DCiaGhQ6zc=/fit-in/150x150/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/R-6167017-1412736726-5831.jpeg.jpg", "format": ["CD", "Album"], "country": "US", "barcode": ["602537990726"], "uri": "/Weezer-Everything-Will-Be-Alright-In-The-End/master/741903", "community": {"have": 2744, "want": 712}, "label": ["Republic Records", "Republic Records", "Republic Records", "The Village Recorder", "South Beach Studios", "Sterling Sound"], "cover_image": "https://img.discogs.com/gpekxrOlT4AVW9-ykuOKsNQwpEQ=/fit-in/500x500/filters:strip_icc():format(jpeg):mode_rgb():quality(90)/discogs-images/R-6167017-1412736726-5831.jpeg.jpg", "catno": "B0021619-02", "year": "2014", "genre": ["Rock"], "title": "Weezer - Everything Will Be Alright In The End", "resource_url": "https://api.discogs.com/masters/741903", "type": "master", "id": 741903}]}
//				22:58:47.641 [main] DEBUG discogs.DiscogSearch - {"pagination":{"per_page":50,"items":1,"page":1,"urls":{},"pages":1},"results":[{"style":["Alternative Rock","Power Pop"],"thumb":"https:\/\/img.discogs.com\/h--kMjRx67LHlr9H2DCiaGhQ6zc=\/fit-in\/150x150\/filters:strip_icc():format(jpeg):mode_rgb():quality(40)\/discogs-images\/R-6167017-1412736726-5831.jpeg.jpg","format":["CD","Album"],"country":"US","barcode":["602537990726"],"uri":"\/Weezer-Everything-Will-Be-Alright-In-The-End\/master\/741903","community":{"have":2744,"want":712},"label":["Republic Records","Republic Records","Republic Records","The Village Recorder","South Beach Studios","Sterling Sound"],"cover_image":"https:\/\/img.discogs.com\/gpekxrOlT4AVW9-ykuOKsNQwpEQ=\/fit-in\/500x500\/filters:strip_icc():format(jpeg):mode_rgb():quality(90)\/discogs-images\/R-6167017-1412736726-5831.jpeg.jpg","catno":"B0021619-02","year":"2014","genre":["Rock"],"title":"Weezer - Everything Will Be Alright In The End","resource_url":"https:\/\/diskong.api.discogs.com\/masters\/741903","type":"master","id":741903}]}
//				22:58:47.649 [main] DEBUG diskong.MassFlac - Alternative Rock Rock


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
