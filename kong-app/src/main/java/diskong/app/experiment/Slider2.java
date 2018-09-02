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

package diskong.app.experiment;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.metal.MetalSliderUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import static javax.swing.UIManager.setLookAndFeel;

public class Slider2 {


    private JPanel Jpanel1;
    private JTextField textField1;
    private JButton button1;
    private JSlider slider1;
    private BasicSliderUI sliderUi;

    public Slider2() {
        slider1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent ev) {
                moveSlider(ev);
            }

            public void mouseReleased(MouseEvent ev) {
                moveSlider(ev);

            }
        });
        slider1.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent ev) {
                moveSlider(ev);
            }
        });
    }

    private void moveSlider(MouseEvent ev) {

        System.out.println("x"+slider1.getValue());
        slider1.setValue(sliderUi.valueForXPosition(ev.getX()));
        System.out.println("y"+slider1.getValue());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Slider2");
        frame.setContentPane(new Slider2().Jpanel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {

        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("com.sun.jxava.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                try {
                    setLookAndFeel(info.getClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        button1 = new JButton("xxxxxxxxxxx");
        slider1 = new JSlider(SwingConstants.HORIZONTAL, 0, 10000, 0);
        // TODO: place custom component creation code here

        sliderUi = (BasicSliderUI) slider1.getUI();
//        slider1.setUI(sliderUi);
    }
}
