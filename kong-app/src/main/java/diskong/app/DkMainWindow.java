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

package diskong.app;



import diskong.app.cdrip.GuiPreferences;
import diskong.app.cdrip.RipForm;
import diskong.app.detail.DetailForm;
import diskong.gui.AlbumModel;
import diskong.gui.FileExplorer;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static javax.swing.UIManager.setLookAndFeel;

public class DkMainWindow {
    private JPanel mainPanel;
    private JTree tree1;
    private FileExplorer fileExplorerPane;
    private JButton ripButton;
    private JButton settingsButton;

    public static void main(String[] args) {
        DkMainWindow dkmw= new DkMainWindow();
        dkmw.init();
    }

    public DkMainWindow() {
        Dimension minimumSize = new Dimension(800, 100);
        fileExplorerPane.getMainPanel().setMinimumSize(minimumSize);
        ripButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame frame = new JFrame("RipForm");
                frame.setContentPane(new RipForm().getJPanelOne());
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                try {
//                    frame.setIconImage(ImageIO.read(new FileInputStream("images/dk114.png")));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                frame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/icon110.png"));

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
        JFrame frame = new JFrame("Diskong");
        frame.setContentPane(new DkMainWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Image img = Toolkit.getDefaultToolkit().getImage("images/icon110.png");

        try {
            img = ImageIO.read(getClass().getResource("/images/icon110.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.setIconImage(img);

        frame.pack();
        frame.setVisible(true);

    }
}
