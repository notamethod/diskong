package diskong;

import java.util.List;

public class SimpleAlbumVO implements IAlbumVo {

	private String title;
	private String artist;
	private String genre;
	private String style;
	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public String getTitle() {
		
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title=title;

	}

	@Override
	public String getArtist() {
		
		return artist;
	}

	@Override
	public void setArtist(String artist) {
		this.artist=artist;

	}

	@Override
	public List<TrackInfo> getTracks() {
		// TODO Auto-generated method stub
		return null;
	}

}
