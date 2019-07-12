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

import diskong.core.bean.TrackInfo;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class TrackModel extends AbstractTableModel{

    private List<TrackInfo> tracks;

    public TrackModel() {
        super();
    }

    protected String[] colName=new String[]{"Num.", "Title","Artist"};
    protected int[] prefSize=new int[]{10,150,80};
    public TrackModel(List<TrackInfo> tracks) {
        this.tracks = tracks;
        Collections.sort(this.tracks);
    }

    public void setElements(List<TrackInfo> tracks) {
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
        TrackInfo track = tracks.get(row);

        switch (column) {

            case 0:
                return track.getNumber();
            case 1:
                return track.getTitle();
            case 2:
                return track.getArtist();

            default :

                return getSpecValue(row, column);
        }

    }

    protected Object getSpecValue(int row, int column) {
        return "";
    }

    public TrackInfo getRow(int row){
        return tracks.get(row);
    }

    public int getPrefSize(int i) {
        if (prefSize.length<(i+1))
            return 50;
        return prefSize[i];
    }
}
