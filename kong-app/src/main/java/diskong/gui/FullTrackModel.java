/*
 * Copyright 2019 org.dpr & croger
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

import diskong.app.data.track.TrackEntity;
import diskong.core.bean.TrackInfo;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;


public class FullTrackModel extends AbstractTableModel{

    private List<TrackEntity> tracks;

    public List<TrackEntity> getTracks() {
        return tracks;
    }

    public FullTrackModel() {
        super();
    }

    protected String[] colName=new String[]{"Num.", "Title","Artist", "Album", "Year"};

    public FullTrackModel(List<TrackEntity> tracks) {
        this.tracks = tracks;
        Collections.sort(this.tracks);
    }

    public void setElements(List<TrackEntity> tracks) {
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
        System.out.println(colName.length);
        return colName.length;
    }


    @Override
    public Object getValueAt(int row, int column) {
        TrackEntity track = tracks.get(row);

        switch (column) {

            case 0:
                return track.getNumber();
            case 1:
                return track.getTitle();
            case 2:
                return track.getArtist();

            case 3:
                return track.getAlbum().getTitle();
            case 4:
                return track.getAlbum().getYear();

            default :

                return getSpecValue(row, column);
        }

    }

    protected Object getSpecValue(int row, int column) {
        return "";
    }

    public TrackEntity getRow(int row){
        return tracks.get(row);
    }

}
