package diskong.parser;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
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
	AutoDetectParser parser;

	public CallTrackInfo(FilePath fPath) {
		this.fPath = fPath;
	}

	public CallTrackInfo(FilePath fPath, AutoDetectParser autoParser) {
		this.fPath = fPath;
		this.parser=autoParser;
	}

	@Override
	public TrackInfo call() throws Exception {
		Metadata metadata = new Metadata();
		BodyContentHandler ch = new BodyContentHandler();
		AutoDetectParser callParser = parser==null?new AutoDetectParser():parser;

		String mimeType = new Tika().detect(fPath.getFile());
		metadata.set(Metadata.CONTENT_TYPE, mimeType);
		InputStream is = Files.newInputStream(fPath.getPath(), StandardOpenOption.READ);

		callParser.parse(is, ch, metadata, new ParseContext());
		is.close();
		if (LOG.isTraceEnabled()) {
			LOG.trace("artist:" + metadata.get(XMPDM.ARTIST) + " album:" + metadata.get(XMPDM.ALBUM) + " track:no:"
					+ metadata.get(XMPDM.TRACK_NUMBER) + " title:" + metadata.get(TikaCoreProperties.TITLE) );
			for (String genre : metadata.getValues(XMPDM.GENRE)) {
				LOG.trace(" genre " + genre);
			}
			for (String style : metadata.getValues("style")) {
				LOG.trace(" style " + style);
			}
		}
		return new TrackInfo(fPath, metadata);
	}
}