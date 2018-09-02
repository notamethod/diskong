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

package diskong.app.detail;

import diskong.api.ApiConfigurationException;
import diskong.api.EventListener;
import diskong.api.GuiListener;
import diskong.app.FlacPlayer;
import diskong.app.tagger.TaggerForm;
import diskong.core.AlbumVo;

import diskong.core.IAlbumVo;
import diskong.gui.GenericForm;
import diskong.gui.TrackModel;
import diskong.gui.TrackTagModel;
import diskong.services.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.metal.MetalSliderUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class DetailForm extends JDialog implements EventListener {

    private final static Logger LOG = LoggerFactory.getLogger(DetailForm.class);
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane scrollPane1;
    private TrackModel model;
    private JTable table1;
    private JButton analyseButton;
    private JLabel pageTitle;
    private JLabel styles;
    private JLabel genres;
    private JLabel jlImg;
    private JButton playButton;
    private JSlider musicSlider;
    private JToggleButton togglePlayButton;
    private BasicSliderUI sliderUi;
    private MySwingWorker worker;
    private List<GuiListener> listeners;

    AlbumVo albumOri;

    public DetailForm(AlbumVo albumOri) {
        this.albumOri = albumOri;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        sliderUi = new MetalSliderUI();
        listeners = new ArrayList<GuiListener>();
        // musicSlider.setUI(sliderUi);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        analyseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                AlbumService albumService = new AlbumService();
                IAlbumVo a2 = null;
                try {
                    a2 = albumService.searchAlbum(albumOri);
                } catch (ApiConfigurationException e) {
                    LOG.error("oauth error", e);
                    JOptionPane.showMessageDialog(null, "Oauth authentication failed. Please check your credentials", "error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (a2 != null)
                    //found: ok
                    LOG.info(albumOri.getTitle() + " found using API: " + albumService.getSearchAPI());
                    //JOptionPane.showMessageDialog(null, albumOri.getTitle() + " found using API: " + albumService.getSearchAPI());
                else {
                    GenericForm gf = new GenericForm();
                    a2 = gf.manualSearch(albumOri);

                }
                if (a2 != null) {
                    IAlbumVo albumNew = a2;
                    if (a2.getTracks().size() == albumOri.getTracks().size()) {
                        TaggerForm tf = new TaggerForm(albumOri, albumNew);
                        tf.pack();

                        tf.setVisible(true);
                    }
                    else
                        JOptionPane.showMessageDialog(null, "nombre de pistes ne correspond pas " + albumService.getSearchAPI());
                    styles.setText(String.join(", ", a2.getStyles()));

                    genres.setText(String.join(", ", a2.getGenres()));
                }

            }
        });


        pageTitle.setText(albumOri.getArtist() + " - " + albumOri.getTitle());
        styles.setText(String.join(", ", albumOri.getStyles()));
        genres.setText(String.join(", ", albumOri.getGenres()));

        // setting (if any) image from folder
        if (albumOri.getFolderImagePath() != null) {
            ImageIcon imgi;
            try {
                imgi = new ImageIcon(new File(albumOri.getFolderImagePath()).toURI().toURL());
                jlImg.setIcon(new ImageIcon(imgi.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        worker = new MySwingWorker(albumOri, albumOri);
                        worker.execute();

                    }
                });

            }
        });
        musicSlider.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent ev) {
                moveSlider(ev);
            }
        });
        musicSlider.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent ev) {
                moveSlider(ev);
            }

            public void mouseReleased(MouseEvent ev) {
                moveSlider(ev);
                for (GuiListener listener : listeners) {
                    listener.seekRequested((double) musicSlider.getValue() / musicSlider.getMaximum());
                }

            }
        });

        togglePlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                boolean selected = abstractButton.getModel().isSelected();
                if (selected && worker==null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            worker = new MySwingWorker(albumOri, albumOri);
                            worker.execute();

                        }
                    });
                }
                if (!selected && worker!=null) {
                    for (GuiListener listener : listeners) {
                        listener.pauseRequested();
                    }

                }
                if (selected && worker!=null) {
                    for (GuiListener listener : listeners) {
                        listener.resumeRequested();
                    }

                }
            }
        });
    }

    /**
     * update slider position
     *
     * @param t new position
     */
    public void setPosition(double t) {
        if (Double.isNaN(t))
            return;
        final double val = Math.max(Math.min(t, 1), 0);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (!musicSlider.getValueIsAdjusting())
                    musicSlider.setValue((int) Math.round(val * musicSlider.getMaximum()));
            }
        });
    }

    private void moveSlider(MouseEvent ev) {
//        musicSlider.setValue(sliderUi.valueForXPosition(ev.getX()));
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        DetailForm dialog = new DetailForm(new AlbumVo());
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        model = new TrackModel(albumOri.getTracks());
        table1 = new JTable(model);
        table1.getColumnModel().getColumn(0).setPreferredWidth(10);
        table1.getColumnModel().getColumn(1).setPreferredWidth(150);

    }

    @Override
    public void componentUpdateRequested(double v) {
        System.out.println("event !!");
        setPosition(v);
    }


    public void addListener(GuiListener listener) {
        listeners.add(listener);
    }


    private class MySwingWorker extends
            SwingWorker<AlbumVo, AlbumVo> {
        public MySwingWorker(AlbumVo albumOri, AlbumVo albumOri2) {
        }

        @Override
        protected AlbumVo doInBackground() throws Exception {
            FlacPlayer player = new FlacPlayer(albumOri);
            addListener(player.getListener());
            player.addListener(DetailForm.this);
            player.playAlbum();
            return null;
        }


    }
}
