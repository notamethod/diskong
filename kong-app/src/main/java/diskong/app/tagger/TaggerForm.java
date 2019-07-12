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

import diskong.Utils;
import diskong.core.bean.AlbumVo;
import diskong.core.bean.IAlbumVo;
import diskong.gui.RetagSelectionDialog;
import diskong.gui.TrackTagModel;
import diskong.parser.MetaUtils;
import diskong.app.services.AlbumService;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private JLabel jlImg;

    AlbumVo albumOri;
    IAlbumVo albumNew;

    public TaggerForm(AlbumVo albumOri) {

        this(albumOri, null);
    }

    public TaggerForm(AlbumVo albumOri, IAlbumVo albumNew) {
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

        if (albumNew != null) {

            ((TrackTagModel) table1.getModel()).setNewInfo(albumNew);
        }
        analyseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {


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
                        int taggedTracks = albumService.retagAlbum(data, albumOri);
                        JOptionPane.showMessageDialog(null,
                                taggedTracks + " tracks tagged", "information", JOptionPane.ERROR_MESSAGE);


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

                if (map.get("albumart") != null) {

                    if (albumNew.getCoverImageUrl() != null) {
                        Path target = albumOri.getTracks().get(0).getfPath().getPath().getParent();

                        try {
                            Utils.downloadFile(albumNew.getCoverImageUrl(), target.resolve("folder.jpg"));
                            JOptionPane.showMessageDialog(null, "image recovered");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        pageTitle.setText(albumOri.getArtist() + " - " + albumNew.getTitle());
        styles.setText(String.join(", ", albumNew.getStyles()));

        genres.setText(String.join(", ", albumNew.getGenres()));


        if (albumNew.getCoverImageUrl() != null) {
            Path target = Paths.get(System.getProperty("java.io.tmpdir"));

            Path fullTarget = target.resolve(albumNew.getId() + ".jpg");
            try {
                Utils.downloadFile(albumNew.getCoverImageUrl(), fullTarget);
                albumNew.setFolderImagePath(fullTarget.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (albumNew.getFolderImagePath() != null) {
            ImageIcon imgi;
            try {
                imgi = new ImageIcon(new File(albumNew.getFolderImagePath()).toURI().toURL());
                jlImg.setIcon(new ImageIcon(imgi.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

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
