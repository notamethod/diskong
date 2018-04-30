package discogs;

import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.filter.ClientFilter;
import diskong.AlbumVo;
import diskong.IAlbumVo;
import diskong.ReleaseNotFoundException;
import diskong.api.AbstractDatabase;
import diskong.api.ApiConfigurationException;
import diskong.api.DatabaseSearch;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class DiscogSearch extends AbstractDatabase implements DatabaseSearch {

    final static Logger LOG = LoggerFactory.getLogger(DiscogSearch.class);
    // base URL for the API calls
    private static final String URL_API = "https://diskong.api.discogs.com/";
    private static final String URL_API_IMG = "https://diskong.api.discogs.com/";
    // private static final String URL_API = "http://localhost:8080/";
    private static final String URL_IDENTITY = "oauth/identity";
    private static final String URL_PROFILE = "users/";
    private static final String URL_SEARCH = "database/search?";
    private Client client;

    public DiscogSearch() {
        // Create a Jersey client
        client = Client.create();

    }

    public static void main(String[] args) throws ApiConfigurationException {
        LOG.info("Entering application.");
        DiscogSearch app = new DiscogSearch();

        //app.getProfile("croger42");

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
            LOG.debug(builder.toString());
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
            LOG.debug(builder.toString());
            LOG.debug(jsonObject.toString());
            // }
        } catch (JSONException ex) {
            LOG.error(DiscogSearch.class.getName(), ex);
        }
    }

    public JSONObject search(String query) throws ApiConfigurationException {
        client.removeAllFilters();
        // Create a resource to be used to make Twitter API calls
        WebResource resource = client.resource(URL_API + URL_SEARCH + query);
        // Add the filter to the resource
        DiscogsOAuth auth = new DiscogsOAuth();
        auth.addAuthentificationFilters(resource);
        // Parse the JSON array
        // JSONArray jsonArray = resource.get(JSONArray.class);
        List<String> statuses = new ArrayList<>();
        // for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = null;
        try {
            jsonObject = resource.get(JSONObject.class);
            StringBuilder builder = new StringBuilder();
            // builder.append(jsonObject.getString("username")).append(jsonObject.getString("resource_url"));
            // statuses.add(builder.toString());

            LOG.debug(jsonObject.toString());

        } catch (Exception ex) {
            LOG.error(DiscogSearch.class.getName(), ex);
        }
        return jsonObject;
    }

    public IAlbumVo searchRelease(IAlbumVo album) throws ReleaseNotFoundException, ApiConfigurationException {
        IAlbumVo albumInfo = new AlbumVo();
        String query = null;
        if (album == null || StringUtils.isBlank(album.getTitle())) {
            LOG.error("insufficient data for search query..." + album);
            return null;
        }
        try {
            query = getReleaseQuery(album);
        } catch (Exception e) {
            LOG.error("invalid query" + query, e);
        }
        JSONObject jsonObject = search(query);
        try {
            JSONArray results = jsonObject.getJSONArray("results");
            if (results.length() == 0)
                throw new ReleaseNotFoundException(album.getTitle());
            JSONObject result = results.getJSONObject(0);

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
            albumInfo.setStyles(result.getJSONArray("style"));
            albumInfo.setGenres(result.getJSONArray("genre"));
            //albumInfo.setImages(result.getJSONArray("image"));

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return albumInfo;
    }

    /*
     * (non-Javadoc)
     *
     * @see diskong.AbstractDatabase#getReleaseQuery(diskong.AlbumVo)
     */
    @Override
    protected String getReleaseQuery(IAlbumVo album) throws URIException {

        StringBuilder sb = new StringBuilder();
        sb.append("type=master");
        if (album.getArtist() != null) {
            sb.append("&artist=").append(album.getArtist());
        }
        sb.append("&title=").append(album.getTitle());

        return URIUtil.encodeQuery(sb.toString());

    }

    @Override
    public boolean isAPIAvailable() {
        boolean available=true;

            File file = new File(System.getProperty("user.home")+"/.shipkong/authapis.ini");
       if (!file.exists()){
                log.error("no configuration file found");
                available = false;
            }

        return available;
    }

    public class UserAgentFilter extends ClientFilter {
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
