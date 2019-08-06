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
import diskong.core.bean.TrackInfo;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

public class CallTrackInfo implements Callable<TrackInfo> {
	private final static Logger LOG = LoggerFactory.getLogger(CallTrackInfo.class);
	private FilePath fPath;
	private AudioParser parser;

	public CallTrackInfo(FilePath fPath) {
		this.fPath = fPath;
	}

	public CallTrackInfo(FilePath fPath, AudioParser autoParser) {
		this.fPath = fPath;
		this.parser= autoParser;
	}

	@Override
	public TrackInfo call() throws Exception {
		Metadata metadata = new Metadata();
		BodyContentHandler ch = new BodyContentHandler();
		AudioParser callParser = parser==null?new TikaAudioParser():parser;

		String mimeType = new Tika().detect(fPath.getFile());
		metadata.set(Metadata.CONTENT_TYPE, mimeType);

		try(InputStream is = Files.newInputStream(fPath.getPath(), StandardOpenOption.READ)) {
			callParser.parse(is, ch, metadata);
		} catch (ParserException e) {
			e.printStackTrace();
		}

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