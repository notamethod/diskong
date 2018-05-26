package diskong.gui;

import diskong.rip.AbcdeHandler;
import diskong.rip.ArgAction;
import diskong.rip.RipperException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RipForm {
    final String[] formats = new String[]{"flac", "m4a", "mp3", "mpc", "ogg", "opus", "spx", "vorbis", "wav", "wv", "ape"};
    AbcdeHandler ah;
    private JPanel JPanel1;
    private JComboBox comboBox1;
    private JButton button1;
    private JTextArea textArea1;
    private JTextField tfOutputDir;
    private JButton ripButton;
    private JComboBox comboBox2;
    private JTextField tempFolder;
    private JTextField outputFormat;
    private JCheckBox albumArtCheckBox;
    private String outputDir;


    public RipForm() {


        final Map<String, String> params = new HashMap<>();

        try {
            ah = new AbcdeHandler();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                params.put(ah.OUTPUT_DIR, tfOutputDir.getText());
                params.put(ah.WAVOUTPUTDIR, tempFolder.getText());
                params.put(ah.OUTPUT_FORMAT, outputFormat.getText());

                List<String> liste = new ArrayList<>();

                liste.add("-a clean,cddb");
                try {

                    textArea1.setText(ah.process(params, liste));
                } catch (RipperException e) {
                    JOptionPane.showMessageDialog(null,
                            e.getMessage(), "information", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,
                            e.getMessage(), "information", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });
        tfOutputDir.setText(ah.getRipProperties().getProperty(ah.OUTPUT_DIR));
        tempFolder.setText(ah.getRipProperties().getProperty(ah.WAVOUTPUTDIR));
        outputFormat.setText(ah.getRipProperties().getProperty(ah.OUTPUT_FORMAT, ah.DEFAULT_OUTPUT_FORMAT));
        ripButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                params.put(ah.OUTPUT_DIR, tfOutputDir.getText());
                params.put(ah.WAVOUTPUTDIR, tempFolder.getText());
                params.put(ah.OUTPUT_FORMAT, outputFormat.getText());
                File f = new File(tfOutputDir.getText());
                System.out.println(f.getAbsolutePath());
                if (!f.exists()) {
                    boolean isCreated = f.mkdirs();
                    JOptionPane.showMessageDialog(null,
                            "error creating " + f.getAbsolutePath(), "information", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                List<String> liste = new ArrayList<>();

                liste.add(ah.FORMAT_PREFIX + comboBox2.getSelectedItem());
                if (albumArtCheckBox.isSelected()){
                    liste.add(ArgAction.DEFAULT.getString());
                }

                try {

                    System.out.println(ah.process(params, liste));
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
        frame.setContentPane(new RipForm().JPanel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {

        comboBox2 = new JComboBox(formats);
    }
}
