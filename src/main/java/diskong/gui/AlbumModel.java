package diskong.gui;

import diskong.AlbumVo;
import diskong.IAlbumVo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class AlbumModel extends AbstractTableModel{

    List<AlbumVo> albums;

    public AlbumModel() {
        super();
        //albums= new ArrayList<>();
    }

    String[] colName=new String[]{"Title","Artist","style", "image"};
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
            case 3:
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
}
