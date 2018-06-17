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

import diskong.AlbumVo;
import diskong.TrackInfo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class TrackModel extends AbstractTableModel{

    List<TrackVO> tracks;

    public TrackModel() {
        super();
        //albums= new ArrayList<>();
    }

    String[] colName=new String[]{"Num.", "Title","Artist"};
    public TrackModel(List<TrackVO> albums) {
        this.tracks = tracks;
    }

    public void setElements(List<TrackVO> tracks) {
        this.tracks = tracks;
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int i) {
        return colName[i];
    }

    @Override
    public int getRowCount() {
        if (tracks==null)
            return 0;
        return tracks.size();
    }

    @Override
    public int getColumnCount() {
        return colName.length;
    }


    @Override
    public Object getValueAt(int row, int column) {
        TrackVO track = tracks.get(row);

        switch (column) {

            case 0:
                return track.getNumber();
            case 1:
                return track.getTitle();
            case 2:
                return track.getArtist();

            default :

                return "";
        }

    }

    public TrackVO getRow(int row){
        return tracks.get(row);
    }
}
