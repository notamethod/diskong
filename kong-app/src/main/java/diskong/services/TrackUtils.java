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

import diskong.core.IAlbumVo;
import diskong.core.TrackInfo;
import diskong.parser.MetaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TrackUtils {
    private final static Logger LOG = LoggerFactory.getLogger(TrackUtils.class);

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
