package diskong.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import diskong.rip.AbcdeHandler;
import diskong.rip.RipperException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class GuiPreferences extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tempFolder;
    private JTextField tfOutputDir;
    private JTextField outputFormat;
    AbcdeHandler ah;
    public GuiPreferences() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);


        try {
            ah = new AbcdeHandler();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onOK();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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


        tfOutputDir.setText(ah.getRipProperties().getProperty(ah.OUTPUT_DIR));
        tempFolder.setText(ah.getRipProperties().getProperty(ah.WAVOUTPUTDIR));
        outputFormat.setText(ah.getRipProperties().getProperty(ah.OUTPUT_FORMAT, ah.DEFAULT_OUTPUT_FORMAT));
    }

    private void onOK() throws IOException {

            final Map<String, String> params = new HashMap<>();
            params.put(ah.OUTPUT_DIR, tfOutputDir.getText());
            params.put(ah.WAVOUTPUTDIR, tempFolder.getText());
            params.put(ah.OUTPUT_FORMAT, outputFormat.getText());
            ah.configure(params);

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        GuiPreferences dialog = new GuiPreferences();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }




}
