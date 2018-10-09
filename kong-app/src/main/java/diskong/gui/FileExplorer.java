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

import diskong.api.ListAlbumListener;
import diskong.core.AlbumVo;
import diskong.core.FilePath;
import diskong.core.TagState;
import diskong.parser.AudioParser;
import diskong.parser.DirectoryParser;
import diskong.parser.NioDirectoryParser;
import diskong.services.AlbumService;
import diskong.services.AudioService;
import org.dpr.swingtools.TextEventListener;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.swing.UIManager.setLookAndFeel;

public class FileExplorer implements TextEventListener {
    private final static Logger LOG = LoggerFactory.getLogger(MainForm.class);
    protected AlbumService albumService = new AlbumService();
    private TableModelListener tListener;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private AlbumModel model;
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
    private JPanel albumPanel;
    private List<ListAlbumListener> listenToTableAlbum;


    @Override
    public void textChanged(String text) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                /* Démarrage de l'interface graphique et du SwingWorker. */

                worker = new RetrieveAlbumsTasks(model, 2);
                worker.execute();
            }
        });
    }

    public FileExplorer()

    {
        listenToTableAlbum = new ArrayList<>();
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                worker.cancel(true);
            }
        });
        pathField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //dunno
            }
        });
        table1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    for (ListAlbumListener listener : listenToTableAlbum) {
                        listener.actionRequested(((AlbumModel)table.getModel()).getRow(table.getSelectedRow()));
                    }
                }
            }
        });
        pathField.addListener(this);
//        table1.setModel(model);
//                table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//
//        table1.getColumnModel().getColumn(0).setMinWidth(300);
//        table1.getColumnModel().getColumn(0).setPreferredWidth(500);
//        table1.getColumnModel().getColumn(0).setMaxWidth(500);
//        table1.getColumnModel().getColumn(1).setMinWidth(200);
//        table1.getColumnModel().getColumn(1).setPreferredWidth(300);
//        table1.getColumnModel().getColumn(1).setMaxWidth(300);
//        table1.getColumnModel().getColumn(2).setMinWidth(150);
//        table1.getColumnModel().getColumn(2).setPreferredWidth(300);
//        table1.getColumnModel().getColumn(2).setMaxWidth(300);
//        //year
//        table1.getColumnModel().getColumn(3).setMinWidth(50);
//        table1.getColumnModel().getColumn(3).setPreferredWidth(50);
//        table1.getColumnModel().getColumn(3).setMaxWidth(50);
//        table1.getColumnModel().getColumn(4).setMinWidth(50);
//        table1.getColumnModel().getColumn(4).setPreferredWidth(150);
//        table1.getColumnModel().getColumn(4).setMaxWidth(150);
        albumPanel.add(scrollPane1);
     // table1.setPreferredScrollableViewportSize(table1.getPreferredSize());
        scrollPane1.setPreferredSize(new Dimension(780,300));
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
        FileExplorer fe = new FileExplorer();
        JFrame frame = new JFrame("FileExplorer");
        frame.setContentPane(fe.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        initializeAlbumTable();
   //     scrollPane1 = new JScrollPane(table1);
       // scrollPane1.setOpaque(false);
      //  scrollPane1.getViewport().setOpaque(false);
//        scrollPane1.setMinimumSize(new Dimension(-1,250));
//        scrollPane1.setPreferredSize(new Dimension(-1,250));

        analyzeDirButton  = new JButton();
        pathField = new JDropText();
        pathField.setEditable(true);
        pathField.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }

    private void initializeAlbumTable(){
        model = new AlbumModel();
        table1 = new JTable(model);


        scrollPane1 = new JScrollPane(table1);

        Font topTopicsFont = new Font("Verdana",Font.PLAIN,12);
        table1.setFont(topTopicsFont);
        table1.setRowHeight(48);
        table1.setOpaque(false);




    }
    private void parseDir(File file) {
        progressBar1.setVisible(true);
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

    public void addListener(ListAlbumListener dkMainWindow) {
        listenToTableAlbum.add(dkMainWindow);
    }


    class RetrieveAlbumsTasks extends
            SwingWorker<List<AlbumVo>, AlbumVo> {
        RetrieveAlbumsTasks(TableModel model, int numbersToFind) {
            //initialize
        }

        @Override
        protected void done() {
            super.done();
                progressBar1.setValue(progressBar1.getMaximum());


        }

        @Override
        public List<AlbumVo> doInBackground() {
            albums.clear();
            model.clear();
            File f = new File(pathField.getText());
            parseDir(f);
//            if (tListener != null)
//                table1.getModel().removeTableModelListener(tListener);
            //table1.setModel(model);
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
                    LOG.info("PUBLISH"+avo.getTitle());
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
            LOG.info("PROCESS" + chunks);
            albums.addAll(chunks);
            LOG.info("PROCESS" + albums);
            model.setAlbums(albums);
//            for (AlbumVo avo : chunks) {
//                textArea.append(number + "\n");
//            }
        }
    }
}
