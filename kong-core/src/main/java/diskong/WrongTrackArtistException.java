package diskong;

import org.apache.tika.metadata.Metadata;

public class WrongTrackArtistException extends Exception {

	public WrongTrackArtistException(Metadata metadata) {
		// TODO Auto-generated constructor stub
		System.out.println("non géré");
	}

}
