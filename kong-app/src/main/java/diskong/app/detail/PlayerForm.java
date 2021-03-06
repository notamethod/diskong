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
import diskong.core.TrackInfo;
import diskong.gui.GenericForm;
import diskong.gui.TableColumnAdjuster;
import diskong.gui.TrackModel;
import diskong.services.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.metal.MetalSliderUI;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.UIManager.setLookAndFeel;

public class PlayerForm implements EventListener {
    public JPanel getMainPanel() {
        return mainPanel;
    }

    private final static Logger LOG = LoggerFactory.getLogger(PlayerForm.class);
    private JPanel mainPanel;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton prevBtn;
    private JToggleButton togglePlayButton;
    private JButton button1;
    private JLabel styles;
    private JLabel genres;
    private JLabel jlImg;
    private JLabel pageTitle;
    private JLabel year;
    private JButton analyseButton;
    private JSlider musicSlider;
    private TrackModel model;
    private BasicSliderUI sliderUi;
    private PlayerForm.MySwingWorker worker;
    private List<GuiListener> listeners;
    AlbumVo albumOri;


    public PlayerForm(AlbumVo albumOri) throws IOException {
        //Test txo test 3
        this.albumOri = albumOri;

        sliderUi = new MetalSliderUI();
        listeners = new ArrayList<>();
        // musicSlider.setUI(sliderUi);

        musicSlider.setPaintTrack(true);
        musicSlider.setUI(new PlayerForm.ColoredThumbSliderUI(musicSlider, Color.red));
        //setPaintTrack(

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
                    } else
                        JOptionPane.showMessageDialog(null, "nombre de pistes ne correspond pas " + albumService.getSearchAPI());
                    styles.setText(String.join(", ", a2.getStyles()));

                    genres.setText(String.join(", ", a2.getGenres()));
                    year.setText(a2.getYear());
                }

            }
        });


        pageTitle.setText(albumOri.getArtist() + " - " + albumOri.getTitle());
        styles.setText(String.join(", ", albumOri.getStyles()));
        genres.setText(String.join(", ", albumOri.getGenres()));
        year.setText(albumOri.getYear());

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
                //request seek to player
                for (GuiListener listener : listeners) {
                    listener.seekRequested((double) musicSlider.getValue() / musicSlider.getMaximum());
                }

            }
        });
        togglePlayButton.setDisabledIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/20px-OOjs_UI_icon_play-ltr.svg.png"))));
        togglePlayButton.setDisabledSelectedIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/20px-OOjs_UI_icon_play-ltr.svg.png"))));
        togglePlayButton.setPressedIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/20px-OOjs_UI_icon_play-ltr.svg.png"))));
        togglePlayButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/20px-OOjs_UI_icon_play-ltr.svg.png"))));
        togglePlayButton.setSelectedIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/20px-OOjs_UI_icon_pause.svg.png"))));
        togglePlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                boolean selected = abstractButton.getModel().isSelected();
                if (selected && worker == null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            worker = new PlayerForm.MySwingWorker(albumOri, 0);
                            worker.execute();

                        }
                    });
                }
                if (!selected && worker != null) {
                    for (GuiListener listener : listeners) {
                        listener.pauseRequested();
                    }

                }
                if (selected && worker != null) {
                    for (GuiListener listener : listeners) {
                        listener.resumeRequested();
                    }

                }
            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (worker != null) {
                    for (GuiListener listener : listeners) {
                        listener.nextRequested();
                    }

                }
            }
        });
        prevBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (worker != null) {
                    for (GuiListener listener : listeners) {
                        listener.previousRequested();
                    }

                }
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                final int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    togglePlayButton.setSelected(true);
                    //worker not null: a track is playing
                    if (worker != null) {
                        TrackInfo tm = ((TrackModel) table.getModel()).getRow(table.getSelectedRow());
                        for (GuiListener listener : listeners) {
                            listener.selectTrackRequested(row);
                        }
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                worker = new PlayerForm.MySwingWorker(albumOri, row);
                                worker.execute();

                            }
                        });

                    }
                }
            }
        });

        TableColumnAdjuster tca = new TableColumnAdjuster(table1);
        tca.setColumnHeaderIncluded(false);
        tca.adjustColumns();

        jlImg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ImageOnlyDialog dialog = new ImageOnlyDialog(albumOri);
                dialog.pack();
                dialog.setVisible(true);
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

    //select a row in the table (the row being payed)
    public void selectRow(int t) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                table1.setRowSelectionInterval(t, t);

            }
        });
    }

    private void moveSlider(MouseEvent ev) {
//        musicSlider.setValue(sliderUi.valueForXPosition(ev.getX()));
    }

    public static void main(String[] args) throws IOException {
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
        frame.setContentPane(new PlayerForm(null).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        model = new TrackModel(albumOri.getTracks());
        table1 = new JTable(model);
        //make transparent background
        scrollPane1 = new JScrollPane(table1);
        scrollPane1.setOpaque(false);
        scrollPane1.setViewportBorder(null);
        scrollPane1.setBorder(BorderFactory.createEmptyBorder());
        table1.setBorder(BorderFactory.createEmptyBorder());
        table1.setOpaque(false);
        //table1.setTableHeader(null);
        //don't show header
        table1.getTableHeader().setUI(null);
        ((DefaultTableCellRenderer) table1.getDefaultRenderer(Object.class)).setOpaque(false);
        //autoresize columns
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // this is obvius part


    }

    @Override
    public void componentUpdateRequested(double v) {
        setPosition(v);
    }

    @Override
    public void TableUpdateRequested(int row) {
        selectRow(row);

    }

    //FIXME: needed ?
    public void addListener(GuiListener listener) {
        listeners.add(listener);
    }


    private class MySwingWorker extends
            SwingWorker<AlbumVo, Integer> {
        Integer row;

        public MySwingWorker(AlbumVo albumOri, Integer intValue) {
            row = intValue;
        }

        @Override
        protected AlbumVo doInBackground() {
            FlacPlayer player = new FlacPlayer(albumOri);
            addListener(player.getListener());
            player.addListener(PlayerForm.this);
            player.playAlbum(row);
            return null;
        }


    }

    /**
     * Change slider style (and thumb color), thanks to Gregg Bolinger
     * <p>
     * color properties from UIManager
     * UIManager.put("Slider.foreground", Color.red);
     * UIManager.put("Slider.focus", Color.red);
     * UIManager.put("Slider.highlight", Color.red);
     * UIManager.put("Slider.shadow", Color.red);
     * UIManager.put("Slider.background", Color.red);
     */
    class ColoredThumbSliderUI extends BasicSliderUI {

        Color thumbColor;

        ColoredThumbSliderUI(JSlider s, Color tColor) {
            super(s);
            thumbColor = tColor;
        }

        public void paint(Graphics g, JComponent c) {
            recalculateIfInsetsChanged();
            recalculateIfOrientationChanged();
            Rectangle clip = g.getClipBounds();

            if (slider.getPaintTrack() && clip.intersects(trackRect)) {
                paintTrack(g);
            }
            if (slider.getPaintTicks() && clip.intersects(tickRect)) {
                paintTicks(g);
            }
            if (slider.getPaintLabels() && clip.intersects(labelRect)) {
                paintLabels(g);
            }
            if (slider.hasFocus() && clip.intersects(focusRect)) {
                paintFocus(g);
            }
            if (clip.intersects(thumbRect)) {
                Color savedColor = slider.getBackground();
                slider.setBackground(thumbColor);
                paintThumb(g);
                slider.setBackground(savedColor);
            }
        }
    }
}
