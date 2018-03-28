package diskong.parser;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diskong.TrackInfo;
import diskong.parser.fileutils.FilePath;

public class CallTrackInfo implements Callable<TrackInfo> {
	final static Logger LOG = LoggerFactory.getLogger(CallTrackInfo.class);
	private FilePath fPath;

	public CallTrackInfo(FilePath fPath) {
		this.fPath = fPath;
	}

	@Override
	public TrackInfo call() throws Exception {
		Metadata metadata = new Metadata();
		BodyContentHandler ch = new BodyContentHandler();
		AutoDetectParser parser = new AutoDetectParser();

		String mimeType = new Tika().detect(fPath.getFile());
		metadata.set(Metadata.CONTENT_TYPE, mimeType);
		InputStream is = Files.newInputStream(fPath.getPath(), StandardOpenOption.READ);

		parser.parse(is, ch, metadata, new ParseContext());
		is.close();
		if (LOG.isDebugEnabled()) {
			LOG.debug("artist:" + metadata.get(XMPDM.ARTIST) + " album:" + metadata.get(XMPDM.ALBUM) + " track:no:"
					+ metadata.get(XMPDM.TRACK_NUMBER) + " title:" + metadata.get(Metadata.TITLE) +  " length:" + metadata.get(XMPDM.DURATION));
			for (String genre : metadata.getValues(XMPDM.GENRE)) {
				LOG.debug(" genre " + genre);
			}
			for (String style : metadata.getValues("style")) {
				LOG.debug(" style " + style);
			}
		}
		return new TrackInfo(fPath, metadata);
	}
}