package diskong.gui;

import diskong.AlbumVo;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class AlbumModel extends AbstractTableModel{

    List<AlbumVo> albums;

    public AlbumModel() {
        super();
    }

    String[] colName=new String[]{"a","b","c"};
    public AlbumModel(List<AlbumVo> albums) {
        this.albums = albums;
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
    public Object getValueAt(int i, int i1) {
        return null;
    }
}
