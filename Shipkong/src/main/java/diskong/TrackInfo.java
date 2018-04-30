package diskong;

import org.apache.tika.metadata.Metadata;

import diskong.parser.fileutils.FilePath;

public class TrackInfo {
	public Metadata getMetadata() {
		return metadata;
	}

	public FilePath getfPath() {
		return fPath;
	}

	private Metadata metadata;
	private FilePath fPath;

	public TrackInfo(Metadata metadata) {
		this.metadata=metadata;
	}

	public TrackInfo(FilePath fPath, Metadata metadata) {
		this.metadata=metadata;
		this.fPath=fPath;
		
	}

}
