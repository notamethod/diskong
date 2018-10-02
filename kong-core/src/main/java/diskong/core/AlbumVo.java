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


import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.AttributeList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AlbumVo implements IAlbumVo, Cloneable {
    private final static Logger LOG = LoggerFactory.getLogger(AlbumVo.class);
    private final static String VARIOUS = "Various";

    private List<TrackInfo> tracks = new ArrayList<>();
    private String title;
    private String artist;
    private String id;
    private List arts = new ArrayList();

    @Override
    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    @Override
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    private String coverImageUrl;

    public void setFolderImagePath(String folderImagePath) {
        this.folderImagePath = folderImagePath;
    }

    public String getFolderImagePath() {
        return folderImagePath;
    }

    private String folderImagePath;

    private List<String> styles = new ArrayList<>();
    private List<String> genres = new ArrayList<>();
    private List<String> images = new ArrayList<>();

    private LocalDate releaseDate;

    private boolean exactMatch = true;

    public boolean isExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    @Override
    public void setGenres(List<String> genres) {
        this.genres = genres;

    }

    private TagState tagState;

    public void addTrack(Metadata metadata) {

    }

    /**
     * Add track to album
     * @param fPath
     * @param metadata
     * @throws WrongTrackAlbumException
     */
    private void addTrack(FilePath fPath, Metadata metadata) throws WrongTrackAlbumException {
        // check if all tracks in folder belong to same album
        if (metadata.get(Metadata.CONTENT_TYPE).contains("flac") || metadata.get(Metadata.CONTENT_TYPE).contains("vorbis")) {
            if (title == null) {
                title = metadata.get(XMPDM.ALBUM);
            } else if (!title.equals(metadata.get(XMPDM.ALBUM))) {
                // wrong album ?
                throw new WrongTrackAlbumException(metadata);
            }
            if (artist == null) {
                artist = metadata.get(XMPDM.ARTIST);
            } else if (!artist.equals(metadata.get(XMPDM.ARTIST))) {
                // wrong artist or various ?
                artist = VARIOUS;
                //throw new WrongTrackArtistException(metadata);
            }
            if (genres == null) {
                genres = MetaUtils.getGenre(metadata);
            }
            if (genres.isEmpty()){
                genres.addAll(MetaUtils.getGenre(metadata));
            }

            if (styles == null) {
                styles = MetaUtils.getStyle(metadata);
            }

            if (styles.isEmpty()){
                styles.addAll(MetaUtils.getStyle(metadata));
            }

            if (releaseDate == null) {
                //may be a date or a year (afaik)
                String dateInString = metadata.get(XMPDM.RELEASE_DATE);
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (dateInString != null) {

                    try {
                        releaseDate = LocalDate.parse(dateInString, dtf);
                    } catch (DateTimeParseException e) {
                        try {
                            int a = Integer.parseInt(dateInString);
                            releaseDate = LocalDate.parse(dateInString + "-01-01", dtf);
                        } catch (DateTimeParseException | NumberFormatException e1) {
                            System.out.println("date error " + dateInString);
                        }
                    }
                }
            }

            tracks.add(new TrackInfo(fPath, metadata));

        } else if (metadata.get(Metadata.CONTENT_TYPE).contains("image") ){
            if (fPath.getFile().getName().toLowerCase().contains("folder") || fPath.getFile().getName().toLowerCase().contains("cover"))
            {
                LOG.debug("cover image found " + fPath.getFile().getName());
                folderImagePath = fPath.getFile().getAbsolutePath();
            }
            else{
                arts.add(fPath);
            }
        } else {
            LOG.debug("type de fichier non géré:" + metadata.get(Metadata.CONTENT_TYPE));
        }
    }

    @Override
    public String toString() {

        return "AlbumVo [title=" + title + ", artist=" + artist /* + ", tracks=" + tracks + "]"*/;
    }

    public List<TrackInfo> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackInfo> tracks) {
        this.tracks = tracks;
        Collections.sort(this.tracks);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;

    }

    /* (non-Javadoc)
     * @see diskong.IAlbumVo#getTitle()
     */
    @Override
    public String getTitle() {
        return title;
    }

    /* (non-Javadoc)
     * @see diskong.IAlbumVo#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see diskong.IAlbumVo#getArtist()
     */
    @Override
    public String getArtist() {
        return artist;
    }

    /* (non-Javadoc)
     * @see diskong.IAlbumVo#setArtist(java.lang.String)
     */
    @Override
    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public void setGenre(String genre) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getStyle() {
        if (styles != null && !styles.isEmpty()) {
            return styles.get(0);
        }
        return null;
    }

    @Override
    public void setStyle(String style) {
        styles = new ArrayList<>();
        styles.add(style);

    }

    @Override
    public String getGenre() {
        if (genres != null) {
            return genres.get(0);
        }
        return null;
    }

    @Override
    public void setStyles(JSONArray jsonArray) {
        styles = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                styles.add(jsonArray.getString(i));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public void setStyles(List<String> styles) {
        this.styles = styles;

    }

    @Override
    public void setGenres(JSONArray jsonArray) {
        genres = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                genres.add(jsonArray.getString(i));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public void setTracks(JSONArray jsonArray) {
        tracks = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject result = jsonArray.getJSONObject(i);
                Metadata metadata = new Metadata();
                metadata.set(TikaCoreProperties.TITLE, (String) result.get("title"));
                metadata.set(XMPDM.TRACK_NUMBER, (String) result.get("position"));
                metadata.set(XMPDM.DURATION, (String) result.get("duration"));
                TrackInfo track = new TrackInfo(metadata);
                tracks.add(track);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Collections.sort(tracks);
    }

    @Override
    public void setImages(JSONArray jsonArray) {
        images = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                images.add(jsonArray.getString(i));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public List<String> getStyles() {
        // TODO Auto-generated method stub
        return styles;
    }

    @Override
    public List<String> getGenres() {
        // TODO Auto-generated method stub
        return genres;
    }

    @Override
    public List<String> getImages() {
        return images;
    }

    public TagState getState() {
        if (tagState == null)
            return TagState.UNKNOWN;
        return tagState;

    }

    public void setState(TagState state) {
        tagState = state;

    }

    public void addTrack(TrackInfo trackInfo) throws WrongTrackAlbumException {
        addTrack(trackInfo.getfPath(), trackInfo.getMetadata());

    }

    public AlbumVo clone() {
        AlbumVo o = null;
        try {
            // On récupère l'instance à renvoyer par l'appel de la
            // méthode super.clone()
            o = (AlbumVo) super.clone();
        } catch (CloneNotSupportedException cnse) {
            // Ne devrait jamais arriver car nous implémentons
            // l'interface Cloneable
            cnse.printStackTrace(System.err);
        }
        // on renvoie le clone
        return o;
    }

    @Override
    public String getYear() {
        return (releaseDate == null ? "" : String.valueOf(releaseDate.getYear()));

    }
}
