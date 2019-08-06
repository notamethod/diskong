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

import diskong.app.common.SkinColor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
public class Utils {
    /**
     * Download a file from an URL
     * @param url
     * @param target
     * @throws IOException
     */
    public static void downloadFile(String url, Path target) throws IOException {

        URL website = new URL(url);
        try (InputStream in = website.openStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }


    public static ImageIcon getColorIcon(SkinColor color, String iconResource) {

        try {
            Image image =  ImageIO.read( Utils.class.getClassLoader().getResource(color.getPath() + iconResource));
            return new ImageIcon(image);
        } catch (IOException e) {
           log.error("No image found", e);
           return null;
        }

    }
}
