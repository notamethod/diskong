package diskong;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;

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

	void setStyles(JSONArray jsonArray);

	void setGenres(JSONArray jsonArray);
	
	List<String> getStyles();

	List<String> getGenres();


	List<String> getImages();
	void setImages(JSONArray jsonArray);
}