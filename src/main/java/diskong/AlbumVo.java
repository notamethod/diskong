package diskong;

import java.util.ArrayList;
import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diskong.parser.fileutils.FilePath;

public class AlbumVo implements IAlbumVo {
	final static Logger LOG = LoggerFactory.getLogger(AlbumVo.class);
	final static String VARIOUS="Various";
	
	private List<TrackInfo> tracks = new ArrayList<TrackInfo>();
	private String title;
	private String artist;
	private String genre;
	private String style;
	
	private List<String> styles;
	private List<String> genres;
	private boolean exactMatch=true;
	public boolean isExactMatch() {
		return exactMatch;
	}
	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}

	private TagState tagState;

	public void add(Metadata metadata) throws WrongTrackAlbumException, WrongTrackArtistException {
		
	}
	// add track to album
	public void add(FilePath fPath, Metadata metadata) throws WrongTrackAlbumException, WrongTrackArtistException {
		// check if all tracks in folder belong to same album
		if (metadata.get(Metadata.CONTENT_TYPE).contains("flac")) {
			if (title == null) {
				title = metadata.get(XMPDM.ALBUM);
			} else if (!title.equals(metadata.get(XMPDM.ALBUM))) {
				// wrong album ?
				throw new WrongTrackAlbumException(metadata);
			}
			if (artist == null) {
				artist = metadata.get(XMPDM.ARTIST);
			} else if (!artist.equals(metadata.get(XMPDM.ARTIST))) {
				// wrong artist or various ?
				artist=VARIOUS;
				//throw new WrongTrackArtistException(metadata);
			}
			if (genre == null) {
				genre = metadata.get(XMPDM.GENRE);
			} else if (!genre.equals(metadata.get(XMPDM.GENRE))) {
				//TODO
			}
			if (style == null) {
				style = metadata.get("style");
			} else if (!style.equals(metadata.get(XMPDM.GENRE))) {
				//TODO
			}
			tracks.add(new TrackInfo(fPath, metadata));

		} else {
			LOG.debug("type de fichier non géré:" + metadata.get(Metadata.CONTENT_TYPE));
		}
	}

	@Override
	public String toString() {
		return "AlbumVo [title=" + title + ", artist=" + artist + ", genre=" + genre + ", style="
				+ style + ", tracks=" + tracks + "]";
	}

	public List<TrackInfo> getTracks() {
		return tracks;
	}

	public void setTracks(List<TrackInfo> tracks) {
		this.tracks = tracks;
	}

	/* (non-Javadoc)
	 * @see diskong.IAlbumVo#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/* (non-Javadoc)
	 * @see diskong.IAlbumVo#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see diskong.IAlbumVo#getArtist()
	 */
	@Override
	public String getArtist() {
		return artist;
	}

	/* (non-Javadoc)
	 * @see diskong.IAlbumVo#setArtist(java.lang.String)
	 */
	@Override
	public void setArtist(String artist) {
		this.artist = artist;
	}

	@Override
	public void setGenre(String genre) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getStyle() {
		if (styles!=null && !styles.isEmpty()){
			return styles.get(0);
		}
		return null;
	}

	@Override
	public void setStyle(String style) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getGenre() {
		if (genres!=null){
			return genres.get(0);
		}
		return null;
	}
	@Override
	public void setStyles(JSONArray jsonArray) {
		styles = new ArrayList<String>();
		for (int i=0; i<jsonArray.length(); i++) {
			try {
				styles.add( jsonArray.getString(i) );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
		@Override
		public void setGenres(JSONArray jsonArray) {
			genres = new ArrayList<String>();
			for (int i=0; i<jsonArray.length(); i++) {
				try {
					genres.add( jsonArray.getString(i) );
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		@Override
		public List<String> getStyles() {
			// TODO Auto-generated method stub
			return styles;
		}
		@Override
		public List<String> getGenres() {
			// TODO Auto-generated method stub
			return genres;
		}
		public TagState getState() {
			if (tagState==null)
				return  TagState.UNKNOWN;
			return tagState;
			
		}
		
		public void setState(TagState state) {
			tagState=state;
			
		}

}
