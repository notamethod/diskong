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
import java.util.Map;
import java.util.Set;

import static javax.swing.UIManager.setLookAndFeel;

public class UIInfos {


    public static void main(String[] args) {

        UIManager.LookAndFeelInfo auxLaF = null;
        UIManager.LookAndFeelInfo forcedLaf = null;
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            System.out.println(info.getClassName());
            if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                forcedLaf = info;

            }
            if ("javax.swing.plaf.metal.MetalLookAndFeel".equals(info.getClassName())) {
                auxLaF = info;

            }
        }

        if (forcedLaf!=null) {
            try {
                setLookAndFeel(forcedLaf.getClassName());
                if (auxLaF!=null) {
                  //  addAuxiliaryLookAndFeel(auxLaF);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Set<Map.Entry<Object, Object>> entries = UIManager.getLookAndFeelDefaults().entrySet();
        for (Map.Entry entry : entries) {
            if (entry.getKey().toString().contains("Slider")) {
                System.out.print(entry.getKey() + " = ");
                System.out.print(entry.getValue() + "\n");
            }
        }
    }

    public void UISetDefaults(){

        UIDefaults uiDefaults = UIManager.getDefaults();

        uiDefaults.put("Slider.trackWidth",7);
        uiDefaults.put("Slider.majorTickLength",6);
        uiDefaults.put("Slider.thumb",6);
//        horizThumbIcon = SAFE_HORIZ_THUMB_ICON =
//                UIManager.getIcon( "Slider.horizontalThumbIcon" );
//        vertThumbIcon = SAFE_VERT_THUMB_ICON =
//                UIManager.getIcon( "Slider.verticalThumbIcon" );
//

//        thumbColor = UIManager.getColor("Slider.thumb");
//        highlightColor = UIManager.getColor("Slider.highlight");
//        darkShadowColor = UIManager.getColor("Slider.darkShadow");


    }
}

