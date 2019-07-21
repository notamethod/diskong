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

package diskong.app.detail;

import diskong.app.common.SimpleStatObject;
import diskong.app.track.TrackEntity;
import diskong.app.services.DataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

@Component
@Configurable
public class MainSelectForm {

    DefaultListModel model;
    DefaultListModel albumModel;

    @Autowired
    private DataServiceImpl trackService;

    public MainSelectForm() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
    }

    private void refresh() {
        List<SimpleStatObject> artists = trackService.findArtistCount();
        System.out.println(artists.size());
        for(SimpleStatObject artist: artists){
            model.addElement(artist.getLabel()+" "+artist.getCount());
        }
        List<SimpleStatObject> albums = trackService.findAlbumCount();
        System.out.println(albums.size());
        for(SimpleStatObject album: albums){
            albumModel.addElement(album.getLabel()+" "+album.getCount());
        }

    }

    public JPanel getMainPanel1() {
        return mainPanel1;
    }

    @PostConstruct
    public void init(){
        System.out.println("POSST" + trackService);
        System.out.println("POSST" + trackService);
        trackService.findAll();
    }
    private JPanel mainPanel1;
    private JPanel topPanel;
    private JPanel playerPanel;
    private JList artistList;
    private JButton refreshButton;
    private JList albumList;

    private void createUIComponents() {
        model = new DefaultListModel();
        artistList = new JList(model);
        albumModel = new DefaultListModel();
        albumList = new JList(albumModel);
    }
}
