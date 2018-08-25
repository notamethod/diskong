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

import diskong.core.IAlbumVo;
import diskong.core.TrackInfo;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class TrackTagModel extends AbstractTableModel{

    private List<TrackInfo> tracks;
    private List<TrackInfo> newTracks;

    public TrackTagModel() {
        super();
        //albums= new ArrayList<>();
    }

    private String[] colName=new String[]{"Num.", "File name", "Title","new Title"};
    public TrackTagModel(List<TrackInfo> tracks, List<TrackInfo> newTracks) {
        this.tracks = tracks;
        Collections.sort(this.tracks);
        this.newTracks = newTracks;

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
        return colName.length;
    }


    @Override
    public Object getValueAt(int row, int column) {
        TrackInfo track = tracks.get(row);
        if (newTracks != null) {
            TrackInfo newTrackInfo = newTracks.get(row);
            switch (column) {

                case 0:
                    return track.getNumber();
                case 1:
                    return track.getfPath().getPath().getFileName();

                case 2:
                    return track.getTitle();
                case 3:

                        return newTrackInfo.getTitle();

                default :

                    return "";
            }
        }
        else {
            switch (column) {

                case 0:
                    return track.getNumber();
                case 1:
                    return track.getfPath().getPath().getFileName();

                case 2:
                    return track.getTitle();

                default:

                    return "";
            }
        }

    }

    public TrackInfo getRow(int row){
        return tracks.get(row);
    }

    public void setNewInfo(IAlbumVo a2) {
        this.newTracks = a2.getTracks();
        Collections.sort(this.newTracks);
        fireTableDataChanged();
    }
}
