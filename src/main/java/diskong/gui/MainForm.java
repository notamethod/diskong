package diskong.gui;

import diskong.AlbumVo;
import diskong.parser.DirectoryParser;
import diskong.parser.NioDirectoryParser;
import diskong.parser.fileutils.FilePath;
import diskong.services.AudioService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.swing.UIManager.setLookAndFeel;

public class MainForm {

    private JButton button1;
    private JTextField textField1;
    private JPanel Panel1;
    private JTable table1;
    private JButton analyzeButton;
    private JButton button3;
    private JLabel nbFiles;
    AlbumModel model = new AlbumModel();
    AudioService service = new AudioService();
    Map<Path, List<FilePath>> map;
    List<AlbumVo> albums = new ArrayList<>();


    public MainForm() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File f = new File(textField1.getText());
                parseDir(f);
            }
        });
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        /* Démarrage de l'interface graphique et du SwingWorker. */

                        MonSwingWorker swingWorker = new MonSwingWorker();
                        swingWorker.execute();
                    }
                });



              //  albums=service.traiterDir(map);
                model.setAlbums(albums);
                table1.setModel(model);
            }
        });
    }



    public static void main(String[] args) {
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
    }

    private void parseDir(File file) {
        DirectoryParser dirParser = new NioDirectoryParser();
       map = dirParser.parse(file.getAbsolutePath());
        if (!map.isEmpty())
            analyzeButton.setEnabled(true);
        nbFiles.setText(String.valueOf(map.size()));
    }

    class MonSwingWorker extends SwingWorker {

        public MonSwingWorker() {
            /* On ajoute un écouteur de barre de progression. */
//            addPropertyChangeListener(new PropertyChangeListener() {
//
//                public void propertyChange(PropertyChangeEvent evt) {
//                    if ("progress".equals(evt.getPropertyName())) {
//                        progressBar.setValue((Integer) evt.getNewValue());
//                    }
//                }
//            });
        }

        @Override
        public Integer doInBackground() {
            table1.setModel(model);
            albums=service.traiterDir(map, model);

            //model.setAlbums(albums);

            return 0;
        }



        @Override
        protected void done() {

    }
    }
}
