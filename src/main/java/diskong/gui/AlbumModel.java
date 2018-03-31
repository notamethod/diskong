package diskong.gui;

import diskong.AlbumVo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AlbumModel extends AbstractTableModel{

    List<AlbumVo> albums;

    public AlbumModel() {
        super();
        //albums= new ArrayList<>();
    }

    String[] colName=new String[]{"Title","Artist","c"};
    public AlbumModel(List<AlbumVo> albums) {
        this.albums = albums;
    }

    public void setAlbums(List<AlbumVo> albums) {
        this.albums = albums;
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int i) {
        return colName[i];
    }

    @Override
    public int getRowCount() {
        return albums.size();
    }

    @Override
    public int getColumnCount() {
        return colName.length;
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

            default :

                return "";
        }

    }
}
