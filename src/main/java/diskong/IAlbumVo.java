package diskong;

import java.util.List;

public interface IAlbumVo {

	String getTitle();

	void setTitle(String title);

	String getArtist();

	void setArtist(String artist);

	void setGenre(String genre);

	String getStyle();
	
	String getGenre();

	void setStyle(String style);
	
	 List<TrackInfo> getTracks();

}