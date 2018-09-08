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

package discogs;

import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import diskong.api.AbstractDatabase;
import diskong.api.ApiConfigurationException;
import diskong.api.DatabaseSearch;
import diskong.core.AlbumVo;
import diskong.core.EmptyResultException;
import diskong.core.IAlbumVo;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.transform.sax.SAXSource;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class DiscogSearch implements DatabaseSearch {

    private final static Logger LOG = LoggerFactory.getLogger(DiscogSearch.class);
    // base URL for the API calls
    private static final String URL_API = "https://api.discogs.com/";
    private static final String URL_API_IMG = "https://api.discogs.com/";
    // private static final String URL_API = "http://localhost:8080/";
    private static final String URL_IDENTITY = "oauth/identity";
    private static final String URL_PROFILE = "users/";
    private static final String URL_RELEASES = "releases/";
    private static final String URL_SEARCH = "database/search?";
    private static final String URL_MASTER__RELEASE_VERSION = "/masters/{0}/versions";
    private static final String URL_MASTER_ = "/masters/";

    private static final String DEFAULT_REGION = "Europe";
    private static final String DEFAULT_COUNTRY = "France";
    private static final String DEFAULT_FORMAT = "cd";

    private Client client;

    public DiscogSearch() {
        // Create a Jersey client
        client = Client.create();

    }

    public static void main(String[] args) throws ApiConfigurationException {
        LOG.info("Entering application.");
        DiscogSearch app = new DiscogSearch();

        app.search("type=all&artist=babybird");
    }

    public void getUserID() throws ApiConfigurationException {
        client.removeAllFilters();

        // Create a resource to be used to make Twitter API calls
        WebResource resource = client.resource(URL_API + URL_IDENTITY);

        // Add the filter to the resource
        // resource.addFilter(getOauthFilter());
        DiscogsOAuth auth = new DiscogsOAuth();
        auth.addAuthentificationFilters(resource);
        // Parse the JSON array
        // JSONArray jsonArray = resource.get(JSONArray.class);
        List<String> statuses = new ArrayList<>();

        try {
            // for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = resource.get(JSONObject.class);
            StringBuilder builder = new StringBuilder();
            builder.append(jsonObject.getString("username")).append(jsonObject.getString("resource_url"));
            statuses.add(builder.toString());
            // }
        } catch (JSONException ex) {
            LOG.error(DiscogSearch.class.getName(), ex);
        }
    }

    public void getProfile(String userName) throws ApiConfigurationException {
        client.removeAllFilters();

        // Create a resource to be used to make Twitter API calls
        WebResource resource = client.resource(URL_API + URL_PROFILE + userName);

        // Add the filter to the resource
        DiscogsOAuth auth = new DiscogsOAuth();
        auth.addAuthentificationFilters(resource);
        // Parse the JSON array
        // JSONArray jsonArray = resource.get(JSONArray.class);
        List<String> statuses = new ArrayList<>();

        try {
            // for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = resource.get(JSONObject.class);
            StringBuilder builder = new StringBuilder();
            builder.append(jsonObject.getString("username")).append(jsonObject.getString("resource_url"));
            statuses.add(builder.toString());
            // }
        } catch (JSONException ex) {
            LOG.error(DiscogSearch.class.getName(), ex);
        }
    }

    private JSONObject search(String query) throws ApiConfigurationException {
        client.removeAllFilters();
        // Create a resource to be used to make Discogs calls
        String searchParam = URL_API + URL_SEARCH + query;
        LOG.debug("search with url: " + searchParam);
        WebResource resource = client.resource(searchParam);
        // Add the filter to the resource
        DiscogsOAuth auth = new DiscogsOAuth();
        auth.addAuthentificationFilters(resource);

        JSONObject jsonObject = null;
        try {
            jsonObject = resource.get(JSONObject.class);
            LOG.trace(jsonObject.toString());

        } catch (Exception ex) {
            LOG.error(DiscogSearch.class.getName(), ex);
        }
        return jsonObject;
    }

    public IAlbumVo searchRelease(IAlbumVo album) throws EmptyResultException, ApiConfigurationException {

        String query = null;
        if (album == null || StringUtils.isBlank(album.getTitle())) {
            LOG.error("insufficient data for search query..." + album);
            return null;
        }

        MultivaluedMap mapFilter = new MultivaluedMapImpl();
        int numTracks = album.getTracks().size();
        JSONObject jsonObject = null;
        try {
            jsonObject = searchMaster(album.getArtist(), album.getTitle());
        } catch (URIException e) {
            e.printStackTrace();
        }
        IAlbumVo albumInfo = null;
        try {
            for (IAlbumVo albumInfoTmp : albumMapping(jsonObject, album.getTitle())) {
                IAlbumVo albumInfoTmp3 = findReleaseById(albumInfoTmp, URL_MASTER_);

                albumInfo = albumInfoTmp;
                break;
            }


        } catch (JSONException e) {
            //TODO: exception
            e.printStackTrace();
        }

        if (albumInfo == null) {
            throw new EmptyResultException(album.getTitle());
        }
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("country", DEFAULT_REGION);
        params.add("format", DEFAULT_FORMAT);
        IAlbumVo albumInfo2 = null;
        try {
            albumInfo2 = findReleaseFromMaster(albumInfo, params, album.getTracks().size());

        } catch (EmptyResultException e) {
            params.remove("country");
            //params.putSingle("country", DEFAULT_COUNTRY);
            try {
                albumInfo2 = findReleaseFromMaster(albumInfo, params, album.getTracks().size());
            } catch (EmptyResultException e1) {
                params.remove("format");
                albumInfo2 = findReleaseFromMaster(albumInfo, params, album.getTracks().size());
            }
        }


        //use other filter
        if (albumInfo2 == null) {
            throw new EmptyResultException("");
        }
        fillAlbum(albumInfo, albumInfo2);
        IAlbumVo albumInfo3 = findReleaseById(albumInfo2);
        fillAlbum(albumInfo, albumInfo3);

        return albumInfo;
    }

    private JSONObject searchMaster(String artist, String title) throws URIException, ApiConfigurationException {

        StringBuilder sb = new StringBuilder();
        sb.append("type=master");
        if (artist != null) {
            sb.append("&artist=").append(artist);
        }
        sb.append("&title=").append(title);
        // sb.append("&format=").append(DEFAULT_FORMAT);
        String queryWithoutCountry = URIUtil.encodeQuery(sb.toString());
        //sb.append("&country=").append(DEFAULT_REGION);
        String query = URIUtil.encodeQuery(sb.toString());

        LOG.debug("getMasterReleaseQuery: " + query);

        //TODO
        //query = query.replaceAll("'", "%27");

        client.removeAllFilters();
        // Create a resource to be used to make Discogs calls
        String searchParam = URL_API + URL_SEARCH + query;
        LOG.debug("search with url: " + searchParam);
        WebResource resource = client.resource(searchParam);
        // Add the filter to the resource
        DiscogsOAuth auth = new DiscogsOAuth();
        auth.addAuthentificationFilters(resource);

        JSONObject jsonObject = null;
        try {
            jsonObject = resource.get(JSONObject.class);
            JSONArray results = jsonObject.getJSONArray("results");
            if (results.length() == 0) {
                throw new EmptyResultException(query);
            }
            if (results.length() > 1) {
                filterMultiMaster();
            }
            LOG.trace(jsonObject.toString());

        } catch (Exception ex) {
            LOG.error(DiscogSearch.class.getName(), ex);
        }
        return jsonObject;
    }

    private void filterMultiMaster() {
        System.out.println("TODODODOOD");
//        if (results.length() == 0){
//            resource = client.resource(URL_API + URL_SEARCH + queryWithoutCountry);
//            // Add the filter to the resource
//            //DiscogsOAuth auth = new DiscogsOAuth();
//            auth.addAuthentificationFilters(resource);
//            jsonObject = resource.get(JSONObject.class);
//        }
    }

    private void fillAlbum(IAlbumVo albumInfo, IAlbumVo otherAlbumInfo) {
        if (albumInfo.getStyles() == null || albumInfo.getStyles().isEmpty())
            albumInfo.setStyles(otherAlbumInfo.getStyles());
        if (albumInfo.getGenres() == null || albumInfo.getGenres().isEmpty())
            albumInfo.setGenres(otherAlbumInfo.getGenres());
        if (albumInfo.getTracks() == null || albumInfo.getTracks().isEmpty())
            albumInfo.setTracks(otherAlbumInfo.getTracks());
        if (albumInfo.getCoverImageUrl()==null)
            albumInfo.setCoverImageUrl(otherAlbumInfo.getCoverImageUrl());


    }

    private List<IAlbumVo> albumMapping(JSONObject jsonObject, String title) throws JSONException, EmptyResultException {
        List<IAlbumVo> albumInfos = new ArrayList<>();
        JSONArray results = jsonObject.getJSONArray("results");
        if (results.length() == 0)
            throw new EmptyResultException(title);

        LOG.info("results found: " + results.length());
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            IAlbumVo albumInfo = new AlbumVo();
            // TODO: multi genre
            albumInfo.setTitle(result.getString("title"));
            try {
                albumInfo.setArtist(result.getString("artist"));

            } catch (JSONException j) {
            }
            try {
                albumInfo.setCoverImageUrl(result.getString("cover_image"));
                System.out.println("image"+result.getString("cover_image"));
            } catch (JSONException j) {
                j.printStackTrace();
            }
            albumInfo.setStyles(result.getJSONArray("style"));
            albumInfo.setGenres(result.getJSONArray("genre"));

            albumInfo.setId(result.getString("id"));
            albumInfos.add(albumInfo);
        }

        return albumInfos;
    }

    private IAlbumVo fullAlbumMapping(JSONObject jsonObject, IAlbumVo album) throws JSONException, EmptyResultException {
        IAlbumVo albumInfo = new AlbumVo();
        JSONArray results = jsonObject.getJSONArray("tracklist");
        if (results.length() == 0)
            throw new EmptyResultException(album.getTitle());

        albumInfo.setTracks(results);


        // TODO: multi genre
        //artisits ?
        albumInfo.setTitle(jsonObject.getString("title"));

        try {
            albumInfo.setStyles(jsonObject.getJSONArray("styles"));
        } catch (JSONException j) {
        }
        try {
            albumInfo.setGenres(jsonObject.getJSONArray("genres"));
        } catch (JSONException j) {
        }
        albumInfo.setCoverImageUrl(album.getCoverImageUrl());
        //change it
        try {
            String image = jsonObject.getString("cover_image");
            System.out.println("image"+image);
            if (image != null)
                albumInfo.setCoverImageUrl(jsonObject.getString("cover_image"));
        } catch (JSONException j) {
//            j.printStackTrace();
        }


//
//        albumInfo.setId(result.getString("id"));
        //imgae from master
        //albumInfo.setImages(result.getJSONArray("image"));
        return albumInfo;
    }

    private IAlbumVo albumMappingList(JSONObject jsonObject, IAlbumVo albumOri, String masterFormat, int size) throws JSONException, EmptyResultException {
        IAlbumVo albumInfo = albumOri.clone();
        boolean searchMatch = false;
        //List<IAlbumVo> releases = new ArrayList<>();
        JSONArray results = jsonObject.getJSONArray("versions");
        if (results.length() == 0)
            throw new EmptyResultException(albumOri.getTitle());
        JSONObject result = null;
        for (int i = 0; i < results.length(); i++) {
            result = results.getJSONObject(i);
            // int numTracks = jsonObject.getJSONArray("tracklist").length();

            if (masterFormat == null) {
                searchMatch = true;
                break;
            }
            try {
                JSONArray jsa = result.getJSONArray("major_formats");
                if (jsa != null) {
                    String majorFormat = (String) jsa.get(0);
                    if (masterFormat.equalsIgnoreCase(majorFormat)) {
                        searchMatch = true;
                        break;
                    }
                }
            } catch (JSONException e) {
                break;
            }


        }

        LOG.debug("versions found: " + results.length());
        if (!searchMatch)
            throw new EmptyResultException("");

        albumInfo.setId(result.getString("id"));
        LOG.debug("returning version: " + albumInfo.getId());
        return albumInfo;
    }

    public List<IAlbumVo> searchArtist(String artist, String title) throws EmptyResultException, ApiConfigurationException {

        String query = null;
//Mashrou'Leila
        List<IAlbumVo> values = new ArrayList<>();
        try {
            query = getArtistQuery(artist, title);
            // query = query.replaceAll("'", "%27");
        } catch (Exception e) {
            LOG.error("invalid query" + query, e);
        }
        JSONObject jsonObject = search(query);
        try {
            JSONArray results = jsonObject.getJSONArray("results");
            if (results.length() == 0)
                throw new EmptyResultException(artist);
            for (int i = 0; i < results.length(); i++) {
                IAlbumVo albumInfo = new AlbumVo();
                JSONObject result = results.getJSONObject(i);
                // TODO: multi genre
                albumInfo.setTitle(result.getString("title"));
                try {
                    albumInfo.setArtist(result.getString("artist"));

                } catch (JSONException j) {
                }
                try {
                    albumInfo.setCoverImageUrl(result.getString("cover_image"));
                } catch (JSONException j) {
                }
//                albumInfo.setStyles(result.getJSONArray("style"));
//                albumInfo.setGenres(result.getJSONArray("genre"));
                values.add(albumInfo);
            }


            //albumInfo.setImages(result.getJSONArray("image"));

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return values;
    }


    public IAlbumVo findReleaseFromMaster(@NotNull IAlbumVo album, MultivaluedMap<String, String> paramsFilter, int size) throws EmptyResultException, ApiConfigurationException {

        client.removeAllFilters();
        String msg = MessageFormat.format(URL_MASTER__RELEASE_VERSION, album.getId());
        // Create a resource to be used to make Twitter API calls


        String searchParam = URL_API + msg;
        LOG.debug("findReleaseFromMaster with url: " + searchParam);
        if (paramsFilter != null)
            LOG.debug("findReleaseFromMaster with param filters: " + paramsFilter.toString());
        WebResource resource = null;
        if (paramsFilter != null)
            resource = client.resource(searchParam).queryParams(paramsFilter);
        else
            resource = client.resource(searchParam);
        IAlbumVo albumVo = null;
        // Add the filter to the resource
        DiscogsOAuth auth = new DiscogsOAuth();
        auth.addAuthentificationFilters(resource);

        String masterFormat = paramsFilter.getFirst("format");
        try {
            JSONObject jsonObject = resource.get(JSONObject.class);
            albumVo = albumMappingList(jsonObject, album, masterFormat, size);

            // }
        } catch (JSONException ex) {
            LOG.error(DiscogSearch.class.getName(), ex);
        }
        return albumVo;
    }

    /*
     * (non-Javadoc)
     *
     * @see diskong.AbstractDatabase#getMasterReleaseQuery(diskong.AlbumVo)
     */
    protected IAlbumVo findReleaseById(IAlbumVo album, String releaseType) throws EmptyResultException, ApiConfigurationException {

        client.removeAllFilters();
        String urlQuery = URL_API + releaseType + album.getId();

        LOG.debug("find release by ID " + urlQuery);
        // Create a resource to be used to make Twitter API calls
        WebResource resource = client.resource(URL_API + releaseType + album.getId());

        // Add the filter to the resource
        DiscogsOAuth auth = new DiscogsOAuth();
        auth.addAuthentificationFilters(resource);

        try {
            // for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = resource.get(JSONObject.class);
            return fullAlbumMapping(jsonObject, album);
            // }
        } catch (JSONException ex) {
            LOG.error(DiscogSearch.class.getName(), ex);
            return null;
        }

    }

    public IAlbumVo findReleaseById(IAlbumVo album) throws EmptyResultException, ApiConfigurationException {
        return findReleaseById(album, URL_RELEASES);
    }


    protected String getArtistQuery(String artist, String title) throws URIException {

        StringBuilder sb = new StringBuilder();
        String sep = "";
        if (artist != null) {
            sb.append("artist=").append(artist);
            sep = "&";
        }
        if (title != null) {

            sb.append(sep).append("title=").append(title);

        }


        return URIUtil.encodeQuery(sb.toString());

    }

    @Override
    public boolean isAPIAvailable() {
        boolean available = true;

        File file = new File(System.getProperty("user.home") + "/.shipkong/authapis.ini");
        if (!file.exists()) {
            LOG.error("no configuration file found");
            available = false;
        }

        return available;
    }

    class UserAgentFilter extends ClientFilter {
        private final String header;
        private final String value;

        public UserAgentFilter(String header, String value) {
            this.header = header;
            this.value = value;
        }

        @Override
        public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
            // TODO Auto-generated method stub

            cr.getHeaders().add(HttpHeaders.USER_AGENT, "<<diskong-user-agent>>");
            return getNext().handle(cr);
        }

    }

}
