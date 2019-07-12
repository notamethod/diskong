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

package diskong;

import diskong.api.ApiConfigurationException;
import diskong.core.EmptyResultException;
import diskong.core.bean.IAlbumVo;
import diskong.app.services.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class SearchDialog extends JDialog {
    private final static Logger LOG = LoggerFactory.getLogger(SearchDialog.class);
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfArtist;
    private JButton button1;
    private JLabel nbCount;
    private JTextField tfTitle;

    public SearchDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AlbumService albumService = new AlbumService();
                try {
                    List<IAlbumVo> values = albumService.searchArtist(tfArtist.getText(), tfTitle.getText());
                    nbCount.setText(String.valueOf(values.size()));
                    System.out.println(values);
                } catch (ApiConfigurationException e) {
                    LOG.error("oauth error", e);

                    JOptionPane.showMessageDialog(null, "Oauth authentication failed. Please check your credentials", "error", JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (EmptyResultException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //
//    a11y.label.keyboardShortcutA
//            Veracode
//
//    a11y.label.keyboardShortcutB
//            Checkmarx
//
//    a11y.label.keyboardShortcutC
//    AppScan Source
//
//    a11y.label.keyboardShortcutD
//            Fortify
//
//    a11y.label.keyboardShortcutE
//            Sonatype
//
//    a11y.label.keyboardShortcutF
//            BlackDuck
//
//    a11y.label.keyboardShortcutG
//            None
//
//    a11y.label.keyboardShortcutH
//            Snyk
//
//    a11y.label.keyboardShortcutI
//            Other
    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        SearchDialog dialog = new SearchDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

}
