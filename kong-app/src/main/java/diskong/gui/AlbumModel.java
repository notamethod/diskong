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

package diskong.gui;

import diskong.core.bean.AlbumVo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class AlbumModel extends AbstractTableModel{

    private List<AlbumVo> albums = new ArrayList<>();

    public AlbumModel() {
        super();
        //albums= new ArrayList<>();
    }

    private String[] colName=new String[]{"Title","Artist","style", "year", "image"};

    public void setAlbums(List<AlbumVo> albums) {
        this.albums.clear();
        this.albums.addAll(albums);
        fireTableDataChanged();
    }

    public void addAlbum(AlbumVo album) {

        this.albums.add(album);
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int i) {
        return colName[i];
    }

    @Override
    public int getRowCount() {
        if (albums==null)
            return 0;
        return albums.size();
    }

    @Override
    public int getColumnCount() {
        return colName.length;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 4:
                return ImageIcon.class;
            default:
                return super.getColumnClass(column);
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        AlbumVo album  = null;
        album= albums.get(row);

        switch (column) {

            case 0:
                return album.getTitle();
            case 1:
                return album.getArtist();
            case 2:
                return album.getStyle();
            case 3:
                return album.getYear();
            case 4:
                if (album.getFolderImagePath()!=null){
                    ImageIcon imgi;
                    try {
                        imgi = new ImageIcon(new File(album.getFolderImagePath()).toURI().toURL());

                        return new ImageIcon(imgi.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                return "no image";
            default :

                return "";
        }

    }

    public AlbumVo getRow(int row){
        return albums.get(row);
    }

    public void clear() {
        this.albums.clear();
    }
}
