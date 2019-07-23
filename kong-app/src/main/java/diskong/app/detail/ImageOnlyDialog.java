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

import diskong.api.TrackList;
import diskong.core.bean.IAlbumVo;

import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;

public class ImageOnlyDialog extends JDialog {
    private JPanel contentPane;
    private JLabel imgLabel;

    public ImageOnlyDialog(IAlbumVo album) {
        setContentPane(contentPane);
        setModal(true);
        // setting (if any) image from folder
        if (album.getFolderImagePath() != null) {
            ImageIcon imgi;
            try {
                imgi = new ImageIcon(new File(album.getFolderImagePath()).toURI().toURL());
                imgLabel.setIcon(new ImageIcon(imgi.getImage()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public ImageOnlyDialog(TrackList trackList) {
        setContentPane(contentPane);
        setModal(true);
        // setting (if any) image from folder
        if (trackList.getFolderImagePath() != null) {
            ImageIcon imgi;
            try {
                imgi = new ImageIcon(new File(trackList.getFolderImagePath()).toURI().toURL());
                imgLabel.setIcon(new ImageIcon(imgi.getImage()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ImageOnlyDialog dialog = new ImageOnlyDialog((IAlbumVo) null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

}
