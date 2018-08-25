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

package diskong.app.tagger;

import diskong.api.ApiConfigurationException;
import diskong.core.AlbumVo;
import diskong.core.IAlbumVo;
import diskong.gui.GenericForm;

import diskong.gui.RetagSelectionDialog;
import diskong.gui.TrackTagModel;
import diskong.parser.MetaUtils;
import diskong.services.AlbumService;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;
import java.util.Map;

public class TaggerForm extends JDialog {

    private final static Logger LOG = LoggerFactory.getLogger(TaggerForm.class);
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane scrollPane1;
    private TrackTagModel model;
    private JTable table1;
    private JButton analyseButton;
    private JButton retagButton;
    private JLabel pageTitle;
    private JLabel styles;
    private JLabel genres;

    AlbumVo albumOri;
    IAlbumVo albumNew;

    public TaggerForm(AlbumVo albumOri) {
        this.albumOri = albumOri;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        analyseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                AlbumService albumService = new AlbumService();
                IAlbumVo a2 = null;
                try {
                    a2 = albumService.searchAlbum(albumOri);
                } catch (ApiConfigurationException e) {
                    LOG.error("oauth error", e);
                    JOptionPane.showMessageDialog(null, "Oauth authentication failed. Please check your credentials", "error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (a2 != null)
                    //found: ok
                    JOptionPane.showMessageDialog(null, albumOri.getTitle() + " found using API: " + albumService.getSearchAPI());
                else {
                    GenericForm gf = new GenericForm();
                    a2 = gf.manualSearch(albumOri);

                }
                if (a2 != null) {
                    albumNew = a2;
                    ((TrackTagModel) table1.getModel()).setNewInfo(a2);
                    styles.setText(String.join(", ", a2.getStyles()));

                    genres.setText(String.join(", ", a2.getGenres()));
                }

            }
        });

        retagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                AlbumService albumService = new AlbumService();

                RetagSelectionDialog dialog = new RetagSelectionDialog();
                dialog.pack();
                dialog.setVisible(true);
                Map<String, Boolean> map = dialog.getretagElements();


                //Metadata data = checkRetagNeeded(originalInfo, correctedInfo);
                albumService.setSimulate(false);
                if (map.get("style") != null) {

                    try {
                        Metadata data = new Metadata();

                            MetaUtils.setStyle(albumNew, data);
                            MetaUtils.setGenre(albumNew, data);
                        albumService.retagAlbum(data, albumOri);


                    } catch (TaggerException e) {
                        LOG.error("error", e);
                        JOptionPane.showMessageDialog(null,
                                e.getMessage(), "information", JOptionPane.ERROR_MESSAGE);

                    }

                }
                if (map.get("trackname") != null) {

                    try {

                        albumService.retagAlbum(albumOri, albumNew);
                    } catch (TaggerException e) {
                        LOG.error("error", e);
                        JOptionPane.showMessageDialog(null,
                                e.getMessage(), "information", JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
            });

        pageTitle.setText(albumOri.getArtist()+" - "+albumOri.getTitle());
        styles.setText(String.join(", ", albumOri.getStyles()));

        genres.setText(String.join(", ", albumOri.getGenres()));

    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        TaggerForm dialog = new TaggerForm(new AlbumVo());
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        model = new TrackTagModel(albumOri.getTracks(), null);
        table1 = new JTable(model);
        table1.getColumnModel().getColumn(0).setPreferredWidth(10);
        table1.getColumnModel().getColumn(1).setPreferredWidth(150);
        table1.getColumnModel().getColumn(2).setPreferredWidth(80);
    }
}
