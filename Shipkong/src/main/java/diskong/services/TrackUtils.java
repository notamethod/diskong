package diskong.services;

import diskong.IAlbumVo;
import diskong.TrackInfo;
import diskong.parser.MetaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackUtils {
    final static Logger LOG = LoggerFactory.getLogger(TrackUtils.class);

    static boolean isAllOK(IAlbumVo iAlbum) {
        String preferences = "genre_exists+style_exists";
        LOG.debug("iteration");
        for (TrackInfo track : iAlbum.getTracks()) {
            if (MetaUtils.getGenre(track.getMetadata()).isEmpty()) {
                return false;
            }
            if (MetaUtils.getStyle(track.getMetadata()).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    static String regexx(String string, String regex) {
        if (string==null)
            return null;
        return string.replaceAll(regex, "").trim();
    }
}
