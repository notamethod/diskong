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


import diskong.app.cdrip.RipForm;
import diskong.app.detail.MainSelectForm;
import diskong.app.services.DataServiceImpl;
import diskong.app.detail.FileExplorer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ResourceBundle;

import static javax.swing.UIManager.setLookAndFeel;

@SpringBootApplication
@Slf4j
public class DkMainApp {
    private final static Logger LOG = LoggerFactory.getLogger(DkMainApp.class);
    final static String EXPLORER_PANEL = "explorerPanel";
    public final static String TOP_PANEL = "topPanel";


    @Autowired
    private FileExplorer fileExplorerForm;

    @Autowired
    private MainSelectForm mainSelectForm;

    @Autowired
    private DataServiceImpl trackService;


    private JPanel mainPanel1;
    private JTree tree1;


    private JButton ripButton;
    private JButton settingsButton;
    private JPanel playerPanel;
    private JSplitPane hzSplit;
    private JPanel leftPanel;
    private JPanel topPanel;
    private JButton addButton;
    private JFrame frame;


    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(
                DkMainApp.class).headless(false).run(args);

        EventQueue.invokeLater(() -> {
            DkMainApp ex = context.getBean(DkMainApp.class);
            ex.init();
        });

    }

    public DkMainApp() {
        log.info("create APP");
        log.info("button"+ripButton);
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
               //do nothing yet
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
              CardLayout cl = (CardLayout) topPanel.getLayout();
              cl.show(topPanel, EXPLORER_PANEL);

            }
        });
    }

    public void init() {
        LOG.info("starting dk app...");

        frame = new JFrame("Diskong");
        frame.setContentPane(this.mainPanel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Image img = Toolkit.getDefaultToolkit().getImage("images/icon110.png");

        try {
            img = ImageIO.read(getClass().getResource("/images/icon110.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setIconImage(img);
        final CardLayout cl = (CardLayout) topPanel.getLayout();
        Dimension minimumSize = new Dimension(800, 100);
        fileExplorerForm.getMainPanel1().setMinimumSize(minimumSize);
        topPanel.add(mainSelectForm.getMainPanel1(), TOP_PANEL);
        topPanel.add(fileExplorerForm.getMainPanel1(), EXPLORER_PANEL);
        fileExplorerForm.addListener(mainSelectForm);
        if (trackService.countTrack()>0) {
            mainSelectForm.refreshFilters();
            cl.show(topPanel, TOP_PANEL);
        }
        else
            cl.show(topPanel, EXPLORER_PANEL);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
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
        ResourceBundle rb = ResourceBundle.getBundle("Messages");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rb.getString("explorer.root"));
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(rb.getString("explorer.local"));
        root.add(node);

        //create the tree by passing in the root node
        tree1 = new JTree(root);

        topPanel = new JPanel(new CardLayout());
    }



}
