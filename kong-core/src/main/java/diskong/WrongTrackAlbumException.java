package diskong;

import org.apache.tika.metadata.Metadata;

public class WrongTrackAlbumException extends Exception {

	Metadata metadata;
	public WrongTrackAlbumException(Metadata metadata) {
		this.metadata=metadata;
	}

	public Metadata getTrack() {
		// TODO Auto-generated method stub
		return metadata;
	}

}
