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

package diskong.app.cdrip;

import diskong.app.JOptionScrollPane;
import diskong.core.bean.TrackInfo;
import diskong.gui.TrackRipModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RipForm {

    private final String[] formats = new String[]{"flac", "m4a", "mp3", "mpc", "ogg", "opus", "spx", "vorbis", "wav", "wv", "ape"};
    private final Logger LOG = LoggerFactory.getLogger(RipForm.class);
    TableModelListener tListener;
    List<TrackInfo> tracks = new ArrayList<>();
    private AbcdeHandler ah;
    private TrackRipModel model;
    private JPanel JPanelOne;
    private JButton analyzeButton;
    private JTextArea textArea1;
    private JButton ripButton;
    private JComboBox comboBox2;
    private JTextField outputFormat;
    private JLabel jCoverImage;
    private JButton button2;
    private JLabel jlTitle;
    private JLabel jlArtist;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JLabel jYear;
    private JLabel jOutput;
    private JButton stopButton;
    private JButton jSettings;
    private JLabel coverImg2;
    private RipTasks worker;
    private boolean albumArt = true;

    //TODO: use swingworker while ripping
    //TODO: show progress while ripping
    //TODO: add cancel button to stop ripping
    public RipForm() {


        try {
            ah = new AbcdeHandler();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                searchCD();
            }
        });

        ripButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                File f = new File(ah.getRipProperties().getProperty(ah.OUTPUT_DIR));
                if (!f.exists()) {
                    boolean isCreated = f.mkdirs();
                    JOptionPane.showMessageDialog(null,
                            "error creating " + f.getAbsolutePath(), "information", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                List<String> liste = new ArrayList<>();

                liste.add(ah.FORMAT_PREFIX + comboBox2.getSelectedItem());
                // no more an option: if (albumArtCheckBox.isSelected()) {
                liste.add(ArgAction.DEFAULT.getString());

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        /* Démarrage de l'interface graphique et du SwingWorker. */

                        worker = new RipTasks(liste, 2);
                        worker.execute();
                    }
                });


            }
        });
        jOutput.setText(ah.getRipProperties().getProperty(ah.OUTPUT_DIR));

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                 ah.stop();

                worker.cancel(true);
            }
        });
        jSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiPreferences gui = new GuiPreferences();
                gui.pack();
                gui.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("RipForm");
        RipForm rf = new RipForm();
        frame.setContentPane(rf.JPanelOne);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                /* code run when component hidden*/
            }

            public void componentShown(ComponentEvent e) {
                //   rf.searchCD();
            }
        });
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public JPanel getJPanelOne() {
        return JPanelOne;
    }

    private void searchCDnew() {


        List<String> liste = new ArrayList<>();

        StringBuilder action = new StringBuilder("-a clean,cddb");
        if (albumArt) {
            action.append(",").append(ArgAction.GETIMAGE.getString());
        }
        liste.add(action.toString());
        try {
            ProcessBuilder pb = ah.processCb(liste);
            pb.redirectErrorStream(true);
            Process process;
            process = pb.start();
            InputStream processStdOutput = process.getInputStream();
            Reader r = new InputStreamReader(processStdOutput);
            BufferedReader br = new BufferedReader(r);
            String line;
            while ((line = br.readLine()) != null) {
                LOG.info("xx" + line);
            }

        } catch (RipperException e) {
            LOG.error("process error", e);
            JOptionPane.showMessageDialog(null,
                    e.getMessageCode(), "information", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOG.error("process error", e);
            JOptionPane.showMessageDialog(null,
                    e.getMessage(), "information", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCD() {


        List<String> liste = new ArrayList<>();

        //clean
        liste.add("-a clean");
        try {
            ah.process(liste);
        } catch (RipperException e) {
            e.printStackTrace();
        }
        liste.clear();
        StringBuilder action = new StringBuilder("-a clean,cddb");
        if (albumArt) {
            action.append(",").append(ArgAction.GETIMAGE.getString());
        }
        liste.add(action.toString());
        try {

            List l = ah.parseTrack(ah.process(liste));
            model.setElements(l);
            jlArtist.setVisible(true);
            jlTitle.setVisible(true);
            jlArtist.setText(ah.getArtist());
            jlTitle.setText(ah.getAlbum());
//            jYear.setText(ah.get);
            String cover = ah.getCoverImage();
            if (cover == null) {
                coverImg2.setText("No cover image found");
            } else {
                File f = new File(cover);
                if (f.exists()) {
                    System.out.println("file found");
                } else
                    System.out.println("file not found " + cover);
                ImageIcon imageIcon = new ImageIcon(cover); // load the image to a imageIcon
                Image newimg = imageIcon.getImage();
                newimg = newimg.getScaledInstance(120, 120, Image.SCALE_SMOOTH); // scale it the smooth way
                imageIcon = new ImageIcon(newimg);  // transform it back
                jCoverImage.setIcon(imageIcon);
            }
        } catch (RipperException e) {
            LOG.error("process error", e);
            JOptionPane.showMessageDialog(null,
                    e.getMessageCode(), "information", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOG.error("process error", e);
            JOptionPane.showMessageDialog(null,
                    e.getMessage(), "information", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createUIComponents() {

        comboBox2 = new JComboBox(formats);
        model = new TrackRipModel();
        table1 = new JTable(model);
        for (int i = 0; i < model.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(model.getPrefSize(i));
        }
    }

    public class RipTasks extends
            SwingWorker<List<String>, String> {
        private List<String> liste;
        private Process process;

        RipTasks(List<String> liste, int numbersToFind) {
            this.liste = liste;
            //initialize
        }

        @Override
        protected void done() {
            super.done();
            textArea1.append("DONE");
            // progressBar1.setValue(progressBar1.getMaximum());
        }

        @Override
        public List<String> doInBackground() {
            //    albums.clear();

            //  table1.getModel().addTableModelListener(tListener);

            int checkTagged = 0;
            int taggedTrack = 0;

            int exitCode;
            try {


                ProcessBuilder pb = ah.processCb(liste);
                pb.redirectErrorStream(true);


                process = pb.start();


//                    if (!p.waitFor(5, TimeUnit.SECONDS)) {
//                        exitCode = 88;
//                    } else {
//                        exitCode = p.exitValue();
//                    }

                InputStream processStdOutput = process.getInputStream();
                Reader r = new InputStreamReader(processStdOutput);
                BufferedReader br = new BufferedReader(r);
                String line;
                while ((line = br.readLine()) != null) {
                    publish(line);
                    LOG.info("xx" + line);
                }

            }
//                catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    exitCode = 98;
//                    throw new RipperException("exit code is " + exitCode);
//                }

            catch (
                    RipperException | IOException e) {

                JOptionScrollPane.showMessageDialog(
                        e.getMessage(),
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);

                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void process(List<String> chunks) {
            //FIXME: does not work
            if (this.isCancelled()) {
                if (process != null && process.isAlive()) {
                    try {
                        System.out.println("yyy");
//                    process.destroy();                     // tell the process to stop
//                    process.waitFor(5, TimeUnit.SECONDS); // give it a chance to stop
                        process.destroyForcibly();             // tell the OS to kill the process
//                    process.waitFor();
                        Thread.sleep(1500);
                        this.done();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            try {
                List<String> infos = new ArrayList<>();
                ah.parseState(chunks, model.getState(), infos, 1);
                if (!infos.isEmpty()){
                    infos.forEach(value -> textArea1.append(value+"\n"));
                }
                model.fireTableDataChanged();
            } catch (analyseException e) {
                e.printStackTrace();
            }


        }
//                      try {
//
//            System.out.println(ah.process(liste));
//        } catch (RipperException e) {
//
//            JOptionScrollPane.showMessageDialog(
//                    e.getMessage(),
//                    "Information",
//                    JOptionPane.INFORMATION_MESSAGE);
//
//            e.printStackTrace();
//        }
    }
}
// - xx
//         12:07:26.412 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:07:26.416 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxGrabbing track 07: The World's First Ever Monster Truck Front Flip...
//         12:07:26.417 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxcdparanoia III release 10.2 (September 11, 2008)
//         12:07:26.417 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:07:26.608 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxRipping from sector  106454 (track  7 [0:00.00])
//         12:07:26.608 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx	  to sector  119954 (track  7 [3:00.00])
//         12:07:26.608 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:07:26.608 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxoutputting to /tmp//abcde.9109990b/track07.wav
//         12:07:26.608 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:07:53.926 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:07:53.926 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:07:53.927 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxDone.
//         12:07:53.927 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:07:53.927 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:07:53.939 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxGrabbing track 08: Science Fiction...
//         12:07:53.940 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxcdparanoia III release 10.2 (September 11, 2008)
//         12:07:53.940 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:07:54.156 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxRipping from sector  119955 (track  8 [0:00.00])
//         12:07:54.156 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx	  to sector  133897 (track  8 [3:05.67])
//         12:07:54.156 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:07:54.156 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxoutputting to /tmp//abcde.9109990b/track08.wav
//         12:07:54.156 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:20.402 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:20.403 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:20.404 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxDone.
//         12:08:20.404 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:20.404 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:20.413 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxGrabbing track 09: She Looks Like Fun...
//         12:08:20.414 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxcdparanoia III release 10.2 (September 11, 2008)
//         12:08:20.414 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:20.627 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxRipping from sector  133898 (track  9 [0:00.00])
//         12:08:20.627 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx	  to sector  147589 (track  9 [3:02.41])
//         12:08:20.627 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:20.627 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxoutputting to /tmp//abcde.9109990b/track09.wav
//         12:08:20.627 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:46.030 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:46.031 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:46.031 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxDone.
//         12:08:46.031 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:46.031 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:46.038 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxGrabbing track 10: Batphone...
//         12:08:46.040 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxcdparanoia III release 10.2 (September 11, 2008)
//         12:08:46.040 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:46.264 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxRipping from sector  147590 (track 10 [0:00.00])
//         12:08:46.264 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx	  to sector  167960 (track 10 [4:31.45])
//         12:08:46.264 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:08:46.264 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxoutputting to /tmp//abcde.9109990b/track10.wav
//         12:08:46.264 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:22.792 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:22.793 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:22.795 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxDone.
//         12:09:22.795 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:22.795 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:22.811 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxGrabbing track 11: The Ultracheese...
//         12:09:22.815 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxcdparanoia III release 10.2 (September 11, 2008)
//         12:09:22.815 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:23.054 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxRipping from sector  167961 (track 11 [0:00.00])
//         12:09:23.054 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx	  to sector  184289 (track 11 [3:37.53])
//         12:09:23.054 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:23.054 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxoutputting to /tmp//abcde.9109990b/track11.wav
//         12:09:23.054 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:50.056 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:50.057 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:50.057 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxDone.
//         12:09:50.057 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:50.057 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xx
//         12:09:50.080 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxEncoding track 11 of 11: The Ultracheese...
//         12:09:50.540 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxTagging track 11 of 11: The Ultracheese...
//         12:10:00.130 [SwingWorker-pool-1-thread-1] INFO  diskong.app.cdrip.RipForm - xxFinished.
