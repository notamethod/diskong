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

import diskong.api.ListAlbumListener;
import diskong.api.TrackList;
import diskong.api.TrackListListener;
import diskong.app.common.SimpleStatObject;
import diskong.app.data.track.TrackEntity;
import diskong.app.services.DataServiceImpl;
import diskong.core.bean.AlbumVo;
import diskong.gui.AlbumModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

@Component
@Configurable
public class MainSelectForm  implements TrackListListener {

    DefaultListModel model;
    DefaultListModel albumModel;
    DefaultListModel genreModel;
    //AlbumModel model = new AlbumModel();

    @Autowired
    private DataServiceImpl trackService;

    @Autowired
    PlayerForm playerForm;



    public MainSelectForm() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        artistList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList theList = (JList) e.getSource();
                    int index = theList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        SimpleStatObject o = (SimpleStatObject) theList.getModel().getElementAt(index);
                        List<TrackEntity> list = trackService.findTrackByArtist(o.getLabel());
                        TrackList tl = new TrackList(list, "toto");
                        actionRequested(tl);
                    }

            }
        });
        albumList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList theList = (JList) e.getSource();
                int index = theList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    SimpleStatObject o = (SimpleStatObject) theList.getModel().getElementAt(index);
                    List<TrackEntity> list = trackService.findTrackByAlbum(o.getLabel());
                    TrackList tl = new TrackList(list, "toto");
                    actionRequested(tl);
                }

            }
        });
        genreList.addComponentListener(new ComponentAdapter() {
        });
        genreList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList theList = (JList) e.getSource();
                int index = theList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    SimpleStatObject o = (SimpleStatObject) theList.getModel().getElementAt(index);
                    List<TrackEntity> list = trackService.findTrackByGenre(o.getLabel());
                    TrackList tl = new TrackList(list, "toto");
                    actionRequested(tl);
                }
            }
        });
    }


    private void refresh() {
        List<SimpleStatObject> artists = trackService.findArtistCount();
        System.out.println(artists.size());
        for(SimpleStatObject artist: artists){
            model.addElement(artist);
        }
        List<SimpleStatObject> albums = trackService.findAlbumCount();
        System.out.println(albums.size());
        for(SimpleStatObject album: albums){
            albumModel.addElement(album);
        }
        List<SimpleStatObject> genres = trackService.findGenreCount();
        System.out.println(albums.size());
        for(SimpleStatObject genre: genres){
            genreModel.addElement(genre);
        }

    }

    public JPanel getMainPanel1() {
        return mainPanel1;
    }

    @PostConstruct
    public void init(){
        trackService.findAll();
    }

    private JPanel mainPanel1;
    private JPanel topPanel;
    private JPanel playerPanel;
    private JList artistList;
    private JButton refreshButton;
    private JList albumList;
    private JList genreList;
    private JPanel filterPanel;

    private void createUIComponents() {
        model = new DefaultListModel();
        artistList = new JList(model);
        albumModel = new DefaultListModel();
        albumList = new JList(albumModel);
        genreModel = new DefaultListModel();
        genreList = new JList(genreModel);
    }

    @Override
    public void actionRequested(TrackList trackList) {
        playerForm.init(trackList);
        Dimension dim = playerForm.getMainPanel1().getSize();
        playerPanel.setPreferredSize(dim);

        //Get the components in the panel
        java.awt.Component[] componentList = playerPanel.getComponents();

//Loop through the components
        for (java.awt.Component c : componentList) {
            //Remove it
            playerPanel.remove(c);
        }

        playerPanel.revalidate();
        playerPanel.repaint();


        final CardLayout cl = (CardLayout) playerPanel.getLayout();
        playerPanel.add(playerForm.getMainPanel1(), BorderLayout.SOUTH);
        playerForm.getMainPanel1().setVisible(true);


        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this.mainPanel1);

        if (topFrame != null) {
            System.out.println("pack");
            topFrame.pack();
        }

    }
}
