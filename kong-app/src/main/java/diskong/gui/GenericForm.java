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

package diskong.gui;

import diskong.api.ApiConfigurationException;
import diskong.core.bean.AlbumVo;
import diskong.core.bean.IAlbumVo;
import diskong.app.services.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class GenericForm {

    private final static Logger LOG = LoggerFactory.getLogger(GenericForm.class);
    protected AlbumService albumService = new AlbumService();

    /**
     * Do a manual with title and artist/
     *
     * @param album contains artist and title previously not found
     */
    public IAlbumVo manualSearch(IAlbumVo album) {
        IAlbumVo a2 = null;
        //manual search
        ManualSearchDialog ms = new ManualSearchDialog(album.getArtist(), album.getTitle());
        ms.setVisible(true);
        AlbumVo albumToSearch = ms.getAlbumInfos();
        if (albumToSearch != null) {
            try {
                a2 = albumService.searchAlbum(albumToSearch);
            } catch (ApiConfigurationException e) {
                LOG.error("oauth error", e);

                JOptionPane.showMessageDialog(null, "Oauth authentication failed. Please check your credentials", "error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (a2 != null) {
                //found: ok
                JOptionPane.showMessageDialog(null, album.getTitle() + " found using API: " + albumService.getSearchAPI());
                System.out.println(a2.toString());
                a2.setArtist(albumToSearch.getArtist());
                a2.setTitle(albumToSearch.getTitle());
                return a2;
            } else
                return manualSearch(album);


        }
        return null;

    }
}
