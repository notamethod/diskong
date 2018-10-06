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


import diskong.api.ListAlbumListener;
import diskong.app.cdrip.GuiPreferences;
import diskong.app.cdrip.RipForm;
import diskong.app.detail.PlayerForm;
import diskong.core.AlbumVo;
import diskong.gui.FileExplorer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;

import static javax.swing.UIManager.setLookAndFeel;

public class DkMainWindow implements ListAlbumListener {
    private JPanel mainPanel;
    private JTree tree1;
    private FileExplorer fileExplorerPane;
    private JButton ripButton;
    private JButton settingsButton;
    private PlayerForm playerForm;
    private JPanel playerPanel;
    private JSplitPane verticalSplit;
    private JSplitPane hzSplit;
    private JFrame frame;


    public static void main(String[] args) {
        DkMainWindow dkmw = new DkMainWindow();
        dkmw.init();
    }

    public DkMainWindow() {
        Dimension minimumSize = new Dimension(800, 100);
        fileExplorerPane.getMainPanel().setMinimumSize(minimumSize);
        fileExplorerPane.addListener(this);
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
        System.out.println("xxxxxxxxxxxxxxxxxxxxx");
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
        frame = new JFrame("Diskong");
        System.out.println("frame " + frame);
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

    private void createUIComponents() {
        ResourceBundle rb = ResourceBundle.getBundle("Messages");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rb.getString("explorer.root"));
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(rb.getString("explorer.local"));
        root.add(node);


        //create the tree by passing in the root node
        tree1 = new JTree(root);

    }

    @Override
    public void actionRequested(AlbumVo album) {
        System.out.println("frame2 " + frame);
        try {
            playerForm = new PlayerForm(album);
            Dimension dim = playerForm.getMainPanel().getSize();
            playerPanel.setPreferredSize(dim);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Get the components in the panel
        Component[] componentList = playerPanel.getComponents();

//Loop through the components
        for (Component c : componentList) {

            //Remove it
            playerPanel.remove(c);
        }


        playerPanel.revalidate();
        playerPanel.repaint();


        final CardLayout cl = (CardLayout) playerPanel.getLayout();
        playerPanel.add(playerForm.getMainPanel(), BorderLayout.SOUTH);
        playerForm.getMainPanel().setVisible(true);


        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this.mainPanel);
        if (frame != null) {
            System.out.println("pack");
            frame.pack();
        }
        if (topFrame != null) {
            System.out.println("pack");
            topFrame.pack();
        }

    }

}
