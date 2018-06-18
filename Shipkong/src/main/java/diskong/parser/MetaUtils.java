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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.XMPDM;

import diskong.core.IAlbumVo;

public class MetaUtils {

	public static final Property STYLE = Property.externalText("style");

	public static List<String> getGenre(Metadata metadata) {
		List<String> genres = new ArrayList<>();
		genres.addAll(Arrays.asList(metadata.getValues(XMPDM.GENRE)));
		return genres;

	}

	public static List<String> getStyle(Metadata metadata) {
		List<String> styles = new ArrayList<>();
		Collections.addAll(styles, metadata.getValues("style"));
		return styles;
	}

	//refactoring: setTag
	public static void setGenre(IAlbumVo source, Metadata metadata) {

		metadata.set(XMPDM.GENRE, source.getGenres().toArray(new String[source.getGenres().size()]));

	}

	public static void setStyle(IAlbumVo source, Metadata metadata) {

		metadata.set(STYLE, source.getStyles().toArray(new String[source.getStyles().size()]));

	}

}
