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

package diskong;

import java.util.List;

import diskong.core.IAlbumVo;
import diskong.core.TrackInfo;
import org.codehaus.jettison.json.JSONArray;

public class SimpleAlbumVO implements IAlbumVo {

	private String title;
	private String artist;
	private String genre;
	private String style;
	private String coverImageUrl;

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public String getTitle() {
		
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title=title;

	}

	@Override
	public String getArtist() {
		
		return artist;
	}

	@Override
	public void setArtist(String artist) {
		this.artist=artist;

	}

	@Override
	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	@Override
	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl=coverImageUrl;
	}

	@Override
	public List<TrackInfo> getTracks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStyles(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGenres(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getStyles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getGenres() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getImages() {
		return null;
	}

	@Override
	public void setImages(JSONArray jsonArray) {

	}

	@Override
	public boolean isExactMatch() {
		return false;
	}

	@Override
	public void setExactMatch(boolean exactMatch) {

	}

}
