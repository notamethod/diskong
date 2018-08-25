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

import diskong.app.tagger.TaggerException;
import diskong.app.tagger.TaggerForm;
import diskong.core.AlbumVo;
import diskong.core.FilePath;
import diskong.core.IAlbumVo;
import diskong.Utils;
import diskong.api.ApiConfigurationException;
import diskong.app.cdrip.GuiPreferences;
import diskong.app.cdrip.RipForm;
import diskong.parser.AudioParser;
import diskong.parser.DirectoryParser;
import diskong.parser.MetaUtils;
import diskong.parser.NioDirectoryParser;
import diskong.services.AlbumService;
import diskong.services.AudioService;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.dpr.swingtools.components.JDropText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static javax.swing.UIManager.setLookAndFeel;

class MainForm {

    private final static Logger LOG = LoggerFactory.getLogger(MainForm.class);
    protected AlbumService albumService = new AlbumService();
    private TableModelListener tListener;
    private AlbumModel model = new AlbumModel();
    private AudioService service = new AudioService();
    private Map<Path, List<FilePath>> map;
    private List<AlbumVo> albums = new ArrayList<>();
    //MonSwingWorker worker;
    private RetrieveAlbumsTasks worker;
    private JButton analyseAlbumButton;
    private JPanel Panel1;
    private JTable table1;
    private JButton analyzeDirButton;
    private JButton getImageButton;
    private JLabel nbFiles;
    private JProgressBar progressBar1;
    private JScrollPane scrollPane1;
    private JDropText pathField;
    private JButton stopButton;
    private JButton retagButton;
    private JButton ripButton;
    private JButton settingsButton;
    private IAlbumVo originalInfo;
    private IAlbumVo correctedInfo;


    public MainForm() {

        
        analyzeDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        /* Démarrage de l'interface graphique et du SwingWorker. */

                        worker = new RetrieveAlbumsTasks(model, 2);
                        worker.execute();
                    }
                });


                //  albums=service.traiterDir(map);
                //  model.setAlbums(albums);
                //table1.setModel(model);
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                worker.cancel(true);
            }
        });

        analyseAlbumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (table1.getSelectedRow() > -1) {
                    IAlbumVo album = model.getRow(table1.getSelectedRow());
                    IAlbumVo a2 = null;
                    try {
                        a2 = albumService.searchAlbum(album);
                    } catch (ApiConfigurationException e) {
                        LOG.error("oauth error", e);
                        JOptionPane.showMessageDialog(null, "Oauth authentication failed. Please check your credentials", "error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (a2 != null)
                        //found: ok
                        JOptionPane.showMessageDialog(null, album.getTitle() + " found using API: " + albumService.getSearchAPI());
                    else {
                        GenericForm gf = new GenericForm();
                        a2 = gf.manualSearch(album);

                    }
                    originalInfo = album;
                    correctedInfo = a2;
                }

            }
        });
        getImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (table1.getSelectedRow() > -1) {
                    AlbumVo album = model.getRow(table1.getSelectedRow());
                    if (album.getFolderImagePath() == null || album.getFolderImagePath().isEmpty()) {
                        IAlbumVo a2 = null;
                        try {
                            a2 = albumService.searchAlbum(album);
                        } catch (ApiConfigurationException e) {
                            LOG.error("oauth error", e);
                            JOptionPane.showMessageDialog(null, "Oauth authentication failed. Please check your credentials", "error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (a2.getCoverImageUrl() != null) {
                            Path target = album.getTracks().get(0).getfPath().getPath().getParent();

                            try {
                                Utils.downloadFile(a2.getCoverImageUrl(), target.resolve("folder.jpg"));
                                JOptionPane.showMessageDialog(null, "image recovered");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        retagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                RetagSelectionDialog dialog = new RetagSelectionDialog();
                dialog.pack();
                dialog.setVisible(true);
               Map<String, Boolean> map =  dialog.getretagElements();
                Metadata data = checkRetagNeeded(originalInfo, correctedInfo);
                if (map.get("style")!=null) {
                    MetaUtils.setStyle(correctedInfo, data);
                    MetaUtils.setGenre(correctedInfo, data);
                }

                albumService.setSimulate(false);
                try {
                    try {
                        albumService.searchAlbumByID(correctedInfo);
                    } catch (ApiConfigurationException e) {
                        e.printStackTrace();
                    }
                    albumService.retagAlbum(data, originalInfo);
                } catch (TaggerException e) {
                    LOG.error("error", e);
                    JOptionPane.showMessageDialog(null,
                            e.getMessage(), "information", JOptionPane.ERROR_MESSAGE);

                }

            }
        });
        ripButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame frame = new JFrame("RipForm");
                frame.setContentPane(new RipForm().getJPanelOne());
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                GuiPreferences gui = new GuiPreferences();
                gui.pack();
                gui.setVisible(true);
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    TaggerForm gui = new TaggerForm(((AlbumModel)table.getModel()).getRow(table.getSelectedRow()));
                    gui.pack();
                    gui.setVisible(true);
                }
            }
        });

    }


    public static void main(String[] args) {
        MainForm mf = new MainForm();
        mf.init();
    }

    private Metadata checkRetagNeeded(IAlbumVo source, IAlbumVo dest) {
        Metadata data = null;
        if (dest != null) {
            data = new Metadata();
            if ((dest.getTitle() != null) && !Objects.equals(dest.getTitle(), source.getTitle())) {
                data.set(XMPDM.ALBUM, dest.getTitle());
            }
            if ((dest.getArtist() != null) && !Objects.equals(dest.getArtist(), source.getArtist())) {
                data.set(XMPDM.ARTIST, dest.getArtist());
            }

//TODO /masters/{master_id}
        }
        //TODO must return null if metadata empty
        return data;
    }



    public void init() {

        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                try {
                    setLookAndFeel(info.getClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().Panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pathField.setEditable(false);
        frame.pack();
        frame.setVisible(true);

    }

    private void createUIComponents() {
        table1 = new JTable(model);
        table1.setRowHeight(48);
        pathField = new JDropText();
        pathField.setEditable(true);
        pathField.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }

    private void parseDir(File file) {
        DirectoryParser dirParser = new NioDirectoryParser();
        map = dirParser.parse(file.getAbsolutePath());
        if (!map.isEmpty())
            analyzeDirButton.setEnabled(true);
        nbFiles.setText(String.valueOf(map.size()));
        progressBar1.setMaximum(map.size());
    }

    private class MonSwingWorker extends SwingWorker {

        public MonSwingWorker() {

        }

        public MonSwingWorker(AlbumModel model, int i) {
        }

        @Override
        public Integer doInBackground() {

            File f = new File(pathField.getText());
            parseDir(f);
            if (tListener != null)
                table1.getModel().removeTableModelListener(tListener);
            table1.setModel(model);
            tListener = e -> progressBar1.setValue(table1.getModel().getRowCount());
            table1.getModel().addTableModelListener(tListener);

            albums = service.traiterDir(map, model);

            //model.setAlbums(albums);

            return 0;
        }


        @Override
        protected void done() {

        }
    }

    class RetrieveAlbumsTasks extends
            SwingWorker<List<AlbumVo>, AlbumVo> {
        RetrieveAlbumsTasks(TableModel model, int numbersToFind) {
            //initialize
        }

        @Override
        public List<AlbumVo> doInBackground() {
            albums.clear();
            File f = new File(pathField.getText());
            parseDir(f);
            if (tListener != null)
                table1.getModel().removeTableModelListener(tListener);
            table1.setModel(model);
            tListener = e -> progressBar1.setValue(table1.getModel().getRowCount());
            table1.getModel().addTableModelListener(tListener);

            int checkTagged = 0;
            int taggedTrack = 0;
            AudioParser ap = new AudioParser();
            for (Map.Entry<Path, List<FilePath>> entry : map.entrySet()) {
                if (this.isCancelled()) {
                    progressBar1.setMaximum(map.size());
                    JOptionPane.showMessageDialog(null, "Analyse stopped");
                    return albums;
                }
                long startTime = System.currentTimeMillis();

                //FIXME:check parser creation
                AlbumVo avo = service.parseDirectory(entry);
                //albums.add(avo);
                //model.setAlbums(albums);
                publish(avo);


//                while (!enough && !isCancelled()) {
//                    number = nextPrimeNumber();
//                    publish(number);
//                    setProgress(100 * numbers.size() / numbersToFind);
//                }
            }
            return albums;

        }

        @Override
        protected void process(List<AlbumVo> chunks) {
            albums.addAll(chunks);
            model.setAlbums(albums);
//            for (AlbumVo avo : chunks) {
//                textArea.append(number + "\n");
//            }
        }
    }
}
