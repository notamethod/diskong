package diskong.gui;

import diskong.parser.DirectoryParser;
import diskong.parser.NioDirectoryParser;
import diskong.parser.fileutils.FilePath;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class MainForm {

    private JButton button1;
    private JTextField textField1;
    private JPanel Panel1;
    private JTable table1;

    public MainForm() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File f = new File(textField1.getText());
                parseDir(f);
            }
        });
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().Panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    private void createUIComponents() {
        table1 = new JTable();
    }

    private void parseDir(File file         ) {
        DirectoryParser dirParser = new NioDirectoryParser();
        Map<Path, List<FilePath>> map = dirParser.parse(file.getAbsolutePath());
    }
}
