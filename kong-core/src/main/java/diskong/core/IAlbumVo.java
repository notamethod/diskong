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

package diskong.core;

import java.util.List;

import diskong.core.TrackInfo;
import org.codehaus.jettison.json.JSONArray;

public interface IAlbumVo {

	String getId();

	void setId(String id);

	String getTitle();

	void setTitle(String title);

	String getArtist();

	void setArtist(String artist);

	String getCoverImageUrl();

	void setCoverImageUrl(String image);

	void setGenre(String genre);

	String getStyle();
	
	String getGenre();

	void setStyle(String style);
	
	 List<TrackInfo> getTracks();

    void setTracks(JSONArray jsonArray);

	void setStyles(JSONArray jsonArray);
	void setStyles(List<String> styles);

	void setGenres(JSONArray jsonArray);
	
	List<String> getStyles();

	List<String> getGenres();


	List<String> getImages();
	void setImages(JSONArray jsonArray);

	boolean isExactMatch();
	void setExactMatch(boolean exactMatch);

	void setGenres(List<String> genres);

    void setTracks(List<TrackInfo> tracks);
}