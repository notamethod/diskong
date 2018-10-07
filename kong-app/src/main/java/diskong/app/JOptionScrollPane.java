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

import javax.swing.*;
import java.awt.*;

/**
 * Option pane with a scrollable text area<br>
 * usage:
 * <pre>
 *  JOptionScrollPane.showMessageDialog(
 *                             Your long message,
 *                             "Your title",
 *                             JOptionPane.messagetype);
 * </pre>
 */
public class JOptionScrollPane {


    public static void main(String[] args) {
        JOptionScrollPane.showMessageDialog("kjkljkjlk\nkkjjklj\nkoiipi\ncscsccsc\nkjkljkjlk\nkkjjklj\nkoiipi\ncscsccsc\nkjkljkjlk\nkkjjklj\nkoiipi\ncscsccsc\nkjkljkjlk\nkkjjklj\nkoiipi\ncscsccsc\n", "test title", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void showMessageDialog(Dimension size, String message, String title, int informationMessage) {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(size);
        JTextArea textArea = new JTextArea(message);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(5,5,5,5));
        scrollPane.getViewport().setView(textArea);
        JOptionPane.showMessageDialog(null,
                scrollPane,
                title,
                informationMessage);
    }

    public static void showMessageDialog(String message, String title, int informationMessage) {
        JOptionScrollPane.showMessageDialog(new Dimension(400,200), message, title, informationMessage);
    }
}
