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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import diskong.rip.AbcdeHandler;
import diskong.rip.ArgAction;
import diskong.rip.RipperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RipForm {

    final String[] formats = new String[]{"flac", "m4a", "mp3", "mpc", "ogg", "opus", "spx", "vorbis", "wav", "wv", "ape"};
    final Logger LOG = LoggerFactory.getLogger(RipForm.class);
    AbcdeHandler ah;
    private JPanel JPanel1;
    private JButton button1;
    private JTextArea textArea1;

    private JButton ripButton;
    private JComboBox comboBox2;

    private JTextField outputFormat;
    private JCheckBox albumArtCheckBox;
    private JLabel jCoverImage;
    private JButton button2;
    private JLabel jlTitle;
    private JLabel jlArtist;


    public RipForm() {




        try {
            ah = new AbcdeHandler();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                searchCD();
            }
        });

        ripButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                File f = new File(ah.getRipProperties().getProperty(ah.OUTPUT_DIR));
                System.out.println(f.getAbsolutePath());
                if (!f.exists()) {
                    boolean isCreated = f.mkdirs();
                    JOptionPane.showMessageDialog(null,
                            "error creating " + f.getAbsolutePath(), "information", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                List<String> liste = new ArrayList<>();

                liste.add(ah.FORMAT_PREFIX + comboBox2.getSelectedItem());
                if (albumArtCheckBox.isSelected()) {
                    liste.add(ArgAction.DEFAULT.getString());
                }

                try {

                    System.out.println(ah.process(liste));
                } catch (RipperException e) {
                    JOptionPane.showMessageDialog(null,
                            e.getMessage(), "information", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }

            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("RipForm");
        RipForm rf = new RipForm();
        frame.setContentPane(rf.JPanel1);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                /* code run when component hidden*/
            }

            public void componentShown(ComponentEvent e) {
                rf.searchCD();
            }
        });
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public JPanel getJPanel1() {
        return JPanel1;
    }

    private void searchCD() {


        List<String> liste = new ArrayList<>();

        liste.add("clean,cddb");
        if (albumArtCheckBox.isSelected()) {
            liste.add(ArgAction.GETIMAGE.getString());
        }
        try {

            textArea1.setText(ah.process(liste));
            ImageIcon imageIcon = new ImageIcon(ah.getCoverImage()); // load the image to a imageIcon

            Image newimg = imageIcon.getImage();
            jlArtist.setText(ah.getArtist());
            jlTitle.setText(ah.getAlbum());
            newimg = newimg.getScaledInstance(120, 120, Image.SCALE_SMOOTH); // scale it the smooth way
            imageIcon = new ImageIcon(newimg);  // transform it back
            jCoverImage.setIcon(imageIcon);
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
    }

}
