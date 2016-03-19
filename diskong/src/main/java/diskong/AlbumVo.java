package diskong;

import java.util.ArrayList;
import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;

public class AlbumVo implements IAlbumVo {
	private List<Metadata> tracks = new ArrayList<Metadata>();
	private String title;
	private String artist;
	private String genre;
	private String style;

	// add track to album
	public void add(Metadata metadata) throws WrongTrackAlbumException, WrongTrackArtistException {
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
				throw new WrongTrackArtistException(metadata);
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
			tracks.add(metadata);

		} else {
			System.err.println("type de fichier non géré:" + metadata.get(Metadata.CONTENT_TYPE));
		}
	}

	@Override
	public String toString() {
		return "AlbumVo [tracks=" + tracks + ", title=" + title + ", artist=" + artist + ", genre=" + genre + ", style="
				+ style + "]";
	}

	public List<Metadata> getTracks() {
		return tracks;
	}

	public void setTracks(List<Metadata> tracks) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStyle(String style) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getGenre() {
		// TODO Auto-generated method stub
		return null;
	}

}
