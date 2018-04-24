package diskong.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import diskong.parser.fileutils.FilePath;

/**
 * Parser for audio files using apache Tika
 * 
 * @author christophe
 *
 */
public class AudioParser {

	private Tika tika;
	AutoDetectParser autoParser;

	public AudioParser() {
		super();
		this.tika = new Tika();
		this.autoParser= new AutoDetectParser();
	}

	final static Logger LOG = LoggerFactory.getLogger(AudioParser.class);

	public static void main(String[] args) {
		String fic = "/mnt/media1/music/Collective Soul/Dosage/02. Heavy.flac";

		AudioParser ap = new AudioParser();
		try {
			ap.parse(new File(fic));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void parse(File f) throws IOException, SAXException, TikaException {

		Metadata metadata = new Metadata();
		BodyContentHandler ch = new BodyContentHandler();
		//AutoDetectParser parser = new AutoDetectParser();

		String mimeType = tika.detect(f);
		metadata.set(Metadata.CONTENT_TYPE, mimeType);
		FileInputStream is = new FileInputStream(f);
		autoParser.parse(is, ch, metadata, new ParseContext());
		is.close();

		for (int i = 0; i < metadata.names().length; i++) {

			String item = metadata.names()[i];
			LOG.debug(item + " -- " + metadata.get(item));
		}

		LOG.debug(ch.toString());
	}

	public Metadata parse(FilePath fPath) throws IOException, SAXException, TikaException {

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
					+ metadata.get(XMPDM.TRACK_NUMBER) + " title:" + metadata.get(Metadata.TITLE));
			for (String genre : metadata.getValues(XMPDM.GENRE)) {
				LOG.debug(" genre " + genre);
			}
			for (String style : metadata.getValues("style")) {
				LOG.debug(" style " + style);
			}
		}
		return metadata;
		// for (int i = 0; i < metadata.names().length; i++) {
		// String item = metadata.names()[i];
		// //System.out.println(item + " -- " + metadata.get(item));
		//
		// }

		// System.out.println(ch.toString());
	}

}
