package diskong.gui;

import diskong.AlbumVo;
import diskong.IAlbumVo;
import diskong.Utils;
import diskong.parser.AudioParser;
import diskong.parser.DirectoryParser;
import diskong.parser.NioDirectoryParser;
import diskong.parser.fileutils.FilePath;
import diskong.services.AlbumService;
import diskong.services.AudioService;
import org.dpr.swingtools.components.JDropText;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.swing.UIManager.setLookAndFeel;

public class MainForm {

    TableModelListener tListener;
    AlbumModel model = new AlbumModel();
    AudioService service = new AudioService();
    AlbumService albumService = new AlbumService();
    Map<Path, List<FilePath>> map;
    List<AlbumVo> albums = new ArrayList<>();
    //MonSwingWorker worker;
    RetrieveAlbumsTasks worker;
    private JButton analyseAlbumButton;
    private JPanel Panel1;
    private JTable table1;
    private JButton analyzeDirButton;
    private JButton button3;
    private JLabel nbFiles;
    private JProgressBar progressBar1;
    private JScrollPane scrollPane1;
    private JDropText pathField;
    private JButton stopButton;


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
                if (table1.getSelectedRow()>-1){
                    AlbumVo aa =  model.getRow(table1.getSelectedRow());
                    IAlbumVo a2 = albumService.searchAlbum(aa);
                    JOptionPane.showMessageDialog(null,aa.getTitle() +" found using API: "+albumService.getSearchAPI());
                    System.out.println(a2.getCoverImageUrl());
                }

            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (table1.getSelectedRow()>-1){
                    AlbumVo album =  model.getRow(table1.getSelectedRow());
                    if (album.getFolderImagePath()==null || album.getFolderImagePath().isEmpty()) {
                        IAlbumVo a2 = albumService.searchAlbum(album);
                        if (a2.getCoverImageUrl() != null){
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
    }


    public static void main(String[] args) {
        MainForm mf = new MainForm();
        mf.init();
    }

    public void init(){

        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
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

        frame.pack();
        frame.setVisible(true);

    }

    private void createUIComponents() {
        table1 = new JTable(model);
        table1.setRowHeight(48);
    }

    private void parseDir(File file) {
        DirectoryParser dirParser = new NioDirectoryParser();
        map = dirParser.parse(file.getAbsolutePath());
        if (!map.isEmpty())
            analyzeDirButton.setEnabled(true);
        nbFiles.setText(String.valueOf(map.size()));
        progressBar1.setMaximum(map.size());
    }

    class MonSwingWorker extends SwingWorker {

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
            diskong.parser.AudioParser ap = new AudioParser();
            for (Map.Entry<Path, List<FilePath>> entry : map.entrySet()) {
                if(this.isCancelled()){
                    progressBar1.setMaximum(map.size());
                    JOptionPane.showMessageDialog(null, "Analyse stopped");
                    return albums;
                }
                long startTime = System.currentTimeMillis();

                //continue or stop asked every NBCHECK parsed files
//                if (checkTagged >= NBCHECK) {
//                    if (contineParse()) {
//                        checkTagged = 0;
//                    } else {
//                        return albums;
//                    }
//                }
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
