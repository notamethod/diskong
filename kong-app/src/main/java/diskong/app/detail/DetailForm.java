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

import diskong.app.FlacPlayer;
import diskong.core.AlbumVo;

import diskong.gui.TrackModel;
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

public class DetailForm extends JDialog {

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
    private BasicSliderUI sliderUi;

    AlbumVo albumOri;

    public DetailForm(AlbumVo albumOri) {
        this.albumOri = albumOri;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        sliderUi = new MetalSliderUI();
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


            }
        });



        pageTitle.setText(albumOri.getArtist() + " - " + albumOri.getTitle());
        styles.setText(String.join(", ", albumOri.getStyles()));

        genres.setText(String.join(", ", albumOri.getGenres()));

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
                FlacPlayer player = new FlacPlayer(albumOri);
                player.playAlbum();
            }
        });
        musicSlider.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent ev) {
                moveSlider(ev);
            }
        });
    }

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


    /*-- Helper interface --*/

    public interface Listener {

        public void seekRequested(double t);  // 0.0 <= t <= 1.0

        public void windowClosing();

        public void pauseRequested();


    }
}
//    public void installUI( JComponent c ) {
//        trackWidth = ((Integer)UIManager.get( "Slider.trackWidth" )).intValue();
//        tickLength = safeLength = ((Integer)UIManager.get( "Slider.majorTickLength" )).intValue();
//        horizThumbIcon = SAFE_HORIZ_THUMB_ICON =
//                UIManager.getIcon( "Slider.horizontalThumbIcon" );
//        vertThumbIcon = SAFE_VERT_THUMB_ICON =
//                UIManager.getIcon( "Slider.verticalThumbIcon" );
//
//        super.installUI( c );
//
//        thumbColor = UIManager.getColor("Slider.thumb");
//        highlightColor = UIManager.getColor("Slider.highlight");
//        darkShadowColor = UIManager.getColor("Slider.darkShadow");
//
//        scrollListener.setScrollByBlock( false );
//
//        prepareFilledSliderField();
//    }