package diskong.services;

import com.googlecode.mp4parser.authoring.Track;
import diskong.*;
import diskong.gui.AlbumModel;
import diskong.parser.AudioParser;
import diskong.parser.CallTrackInfo;
import diskong.parser.MetaUtils;
import diskong.parser.fileutils.FilePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class AudioService {

    final static Logger LOG = LoggerFactory.getLogger(AudioService.class);
    static int NBCHECK = 200;
    AlbumService albumService = new AlbumService();

    public List<AlbumVo> traiterDir(Map<Path, List<FilePath>> map, AlbumModel model) {
        List<AlbumVo> albums = new ArrayList<>();
        int checkTagged = 0;
        int taggedTrack = 0;
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
            diskong.parser.AudioParser ap = new AudioParser();
            AlbumVo album = AlbumFactory.getAlbum();
            albums.add(album);
            try {
                // parsedir
                LOG.debug("iteration");

                album.setState(TagState.TOTAG);
                LOG.debug("**************START******************************");
                ExecutorService executor = Executors.newFixedThreadPool(10);
                List<Future<TrackInfo>> list = new ArrayList<>();
                for (FilePath fPath : entry.getValue()) {
                    LOG.debug(fPath.getFile().getAbsolutePath());
                    Callable<TrackInfo> worker = new CallTrackInfo(fPath);
                    Future<TrackInfo> submit = executor.submit(worker);
                    list.add(submit);
                }

                for (Future<TrackInfo> future : list) {
                    try {
                        TrackInfo tinf = future.get();
                        album.add(tinf); // (metafile)
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } catch (WrongTrackAlbumException e) {
                        // put track in right album
                        AlbumFactory.orderingTrack(e.getTrack());
                    }

                }
                executor.shutdown();

                if (album.getTracks().isEmpty())
                    album.setState(TagState.NOTRACKS);
                else
                    LOG.info("Album parsed:" + album.toString() + album.getTracks().size());
                model.setAlbums(albums);
                Statistics.getInstance().addStats(album);
//                if (checkAction(album)) {
//                    IAlbumVo alInfos = albumService.searchAlbum(album);
//                    checkTagged += albumService.actionOnAlbum(album, alInfos);
//                    taggedTrack += checkTagged;
//                }

//				1 <
//						{"pagination": {"per_page": 50, "items": 1, "page": 1, "urls": {}, "pages": 1}, "results": [{"style": ["Alternative Rock", "Power Pop"], "thumb": "https://img.discogs.com/h--kMjRx67LHlr9H2DCiaGhQ6zc=/fit-in/150x150/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/R-6167017-1412736726-5831.jpeg.jpg", "format": ["CD", "Album"], "country": "US", "barcode": ["602537990726"], "uri": "/Weezer-Everything-Will-Be-Alright-In-The-End/master/741903", "community": {"have": 2744, "want": 712}, "label": ["Republic Records", "Republic Records", "Republic Records", "The Village Recorder", "South Beach Studios", "Sterling Sound"], "cover_image": "https://img.discogs.com/gpekxrOlT4AVW9-ykuOKsNQwpEQ=/fit-in/500x500/filters:strip_icc():format(jpeg):mode_rgb():quality(90)/discogs-images/R-6167017-1412736726-5831.jpeg.jpg", "catno": "B0021619-02", "year": "2014", "genre": ["Rock"], "title": "Weezer - Everything Will Be Alright In The End", "resource_url": "https://api.discogs.com/masters/741903", "type": "master", "id": 741903}]}
//				22:58:47.641 [main] DEBUG diskong.api.discogs.DiscogSearch - {"pagination":{"per_page":50,"items":1,"page":1,"urls":{},"pages":1},"results":[{"style":["Alternative Rock","Power Pop"],"thumb":"https:\/\/img.discogs.com\/h--kMjRx67LHlr9H2DCiaGhQ6zc=\/fit-in\/150x150\/filters:strip_icc():format(jpeg):mode_rgb():quality(40)\/discogs-images\/R-6167017-1412736726-5831.jpeg.jpg","format":["CD","Album"],"country":"US","barcode":["602537990726"],"uri":"\/Weezer-Everything-Will-Be-Alright-In-The-End\/master\/741903","community":{"have":2744,"want":712},"label":["Republic Records","Republic Records","Republic Records","The Village Recorder","South Beach Studios","Sterling Sound"],"cover_image":"https:\/\/img.discogs.com\/gpekxrOlT4AVW9-ykuOKsNQwpEQ=\/fit-in\/500x500\/filters:strip_icc():format(jpeg):mode_rgb():quality(90)\/discogs-images\/R-6167017-1412736726-5831.jpeg.jpg","catno":"B0021619-02","year":"2014","genre":["Rock"],"title":"Weezer - Everything Will Be Alright In The End","resource_url":"https:\/\/api.discogs.com\/masters\/741903","type":"master","id":741903}]}
//				22:58:47.649 [main] DEBUG diskong.MassFlac - Alternative Rock Rock


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            long endTime = System.currentTimeMillis();
            LOG.debug(" files metaparsed in " + (endTime - startTime) + " ms");

        }
        return albums;
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
        if (album.getState().equals(TagState.TOTAG) && !forceRetag && isOK) {
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