/*
 * Copyright 2018 org.dpr & croger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package diskong.parser;

import diskong.core.FilePath;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;


/**
 * Parser for audio files using apache Tika
 * 
 * @author christophe
 *
 */
public class TikaAudioParser implements AudioParser {

	private Tika tika;

    public Parser getParser() {
        return parser;
    }

    private Parser parser;

	public TikaAudioParser() throws Exception {
		super();
		this.tika = new Tika();
		try {
			this.parser = createParser();
		} catch (Exception e ) {
		    throw new Exception("can't instantiate parser", e);

		}
	}

	private final static Logger LOG = LoggerFactory.getLogger(TikaAudioParser.class);

	public static void main(String[] args) throws Exception {
		String fic = "/mnt/media1/music/Collective Soul/Dosage/02. Heavy.flac";

		TikaAudioParser ap = new TikaAudioParser();
		try {
			ap.parse(new File(fic));
		} catch (IOException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Parser createParser() throws TikaException, IOException, SAXException {

		LOG.info("Init Tika parser");
        ClassLoader classLoader = getClass().getClassLoader();
        Parser autoDetectParser = null;
        try(  InputStream is = classLoader.getResourceAsStream("tika-config.xml")){
            TikaConfig config = new TikaConfig(is);
             autoDetectParser = new AutoDetectParser(config);

        }

		//Detector detector = config.getDetector();

		return autoDetectParser;
	}
	private void parse(File f) throws IOException, SAXException, TikaException {

		Metadata metadata = new Metadata();
		BodyContentHandler ch = new BodyContentHandler();

		String mimeType = tika.detect(f);
		metadata.set(Metadata.CONTENT_TYPE, mimeType);
		FileInputStream is = new FileInputStream(f);
		parser.parse(is, ch, metadata, new ParseContext());
		is.close();

		for (int i = 0; i < metadata.names().length; i++) {

			String item = metadata.names()[i];
			LOG.debug(item + " -- " + metadata.get(item));
		}

		LOG.debug(ch.toString());
	}

	@Override
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
					+ metadata.get(XMPDM.TRACK_NUMBER) + " title:" + metadata.get(TikaCoreProperties.TITLE));
			for (String genre : metadata.getValues(XMPDM.GENRE)) {
				LOG.debug(" genre " + genre);
			}
			for (String style : metadata.getValues("style")) {
				LOG.debug(" style " + style);
			}
		}
		return metadata;
	}

	@Override
	public void parse(InputStream is, BodyContentHandler ch, Metadata metadata) throws ParserException {
		try {
			parser.parse(is,ch , metadata, new ParseContext());
		} catch (IOException | SAXException | TikaException e) {
			throw new ParserException(e);

		}
	}

}
