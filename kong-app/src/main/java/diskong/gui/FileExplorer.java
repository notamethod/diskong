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

import diskong.app.detail.DetailForm;
import diskong.core.AlbumVo;
import diskong.core.FilePath;
import diskong.core.TagState;
import diskong.parser.AudioParser;
import diskong.parser.DirectoryParser;
import diskong.parser.NioDirectoryParser;
import diskong.services.AlbumService;
import diskong.services.AudioService;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.swing.UIManager.setLookAndFeel;

public class FileExplorer {
    private final static Logger LOG = LoggerFactory.getLogger(MainForm.class);
    protected AlbumService albumService = new AlbumService();
    private TableModelListener tListener;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private AlbumModel model = new AlbumModel();
    private AudioService service = new AudioService();
    private Map<Path, List<FilePath>> map;
    private List<AlbumVo> albums = new ArrayList<>();

    private RetrieveAlbumsTasks worker;
    private JDropText pathField;
    private JButton analyzeDirButton;
    private JButton stopButton;
    private JLabel nbFiles;
    private JScrollPane scrollPane1;
    private JPanel mainPanel;
    private JTable table1;
    private JProgressBar progressBar1;


    public FileExplorer() {

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

            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                worker.cancel(true);
            }
        });
        pathField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("xxx");
            }
        });
        table1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    DetailForm gui = null;
                    try {
                        gui = new DetailForm(((AlbumModel)table.getModel()).getRow(table.getSelectedRow()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    gui.pack();
                    gui.setVisible(true);
                }
            }
        });
    }

    public static void main(String[] args) {
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
        JFrame frame = new JFrame("FileExplorer");
        frame.setContentPane(new FileExplorer().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        table1 = new JTable(null);
        scrollPane1 = new JScrollPane(table1);
        scrollPane1.setOpaque(false);
        scrollPane1.getViewport().setOpaque(false);
        table1.setRowHeight(48);
        table1.setOpaque(false);
        analyzeDirButton  = new JButton();
        pathField = new JDropText();
        pathField.setEditable(true);
        pathField.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }
    private void parseDir(File file) {
        DirectoryParser dirParser = new NioDirectoryParser();
        try {
            map = dirParser.parse(file);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found: " + e.getLocalizedMessage(), "error", JOptionPane.ERROR_MESSAGE);

        }
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
                if (!avo.getState().equals(TagState.NOTRACKS)) {
                    publish(avo);
                }



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
