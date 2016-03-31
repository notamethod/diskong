package diskong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONJAXBContext.JSONNotation;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

/**
 * Hello world!
 * 
 */
public class DiscogSearch extends AbstractDatabase implements DatabaseSearch {

	// base URL for the API calls
	private static final String URL_API = "https://api.discogs.com/";
	// private static final String URL_API = "http://localhost:8080/";
	private static final String URL_IDENTITY = "oauth/identity";
	private static final String URL_PROFILE = "users/";
	private static final String URL_SEARCH = "database/search?";

	final static Logger LOG = LoggerFactory.getLogger(DiscogSearch.class);
	private Client client;

	public DiscogSearch() {
		// Create a Jersey client
		client = Client.create();

	}

	public static void main(String[] args) {
		LOG.info("Entering application.");
		DiscogSearch app = new DiscogSearch();

		app.getProfile("croger42");

		// app.search("type=all&artist=babybird");
	}

	public void getUserID() {
		client.removeAllFilters();

		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(URL_API + URL_IDENTITY);

		// Add the filter to the resource
		// resource.addFilter(getOauthFilter());
		DiscogsOAuth auth = new DiscogsOAuth();
		auth.addAuthentificationFilters(resource);
		// Parse the JSON array
		// JSONArray jsonArray = resource.get(JSONArray.class);
		List<String> statuses = new ArrayList<String>();

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

	public void getProfile(String userName) {
		client.removeAllFilters();

		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(URL_API + URL_PROFILE + userName);

		// Add the filter to the resource
		DiscogsOAuth auth = new DiscogsOAuth();
		auth.addAuthentificationFilters(resource);
		// Parse the JSON array
		// JSONArray jsonArray = resource.get(JSONArray.class);
		List<String> statuses = new ArrayList<String>();

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

	public JSONObject search(String query) {
		client.removeAllFilters();
		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(URL_API + URL_SEARCH + query);
		// Add the filter to the resource
		DiscogsOAuth auth = new DiscogsOAuth();
		auth.addAuthentificationFilters(resource);
		// Parse the JSON array
		// JSONArray jsonArray = resource.get(JSONArray.class);
		List<String> statuses = new ArrayList<String>();
		// for (int i = 0; i < jsonArray.length(); i++) {
		JSONObject jsonObject = null;
		try {
			jsonObject = resource.get(JSONObject.class);
			StringBuilder builder = new StringBuilder();
			// builder.append(jsonObject.getString("username")).append(jsonObject.getString("resource_url"));
			// statuses.add(builder.toString());

			LOG.info(jsonObject.toString());

		} catch (Exception ex) {
			LOG.error(DiscogSearch.class.getName(), ex);
		}
		return jsonObject;
	}

	public IAlbumVo searchRelease(IAlbumVo album) throws ReleaseNotFoundException {
		IAlbumVo albumInfo = new AlbumVo();
		String query = null;
		if (album == null || StringUtils.isBlank(album.getTitle())) {
			log.error("insufficient data for search query..." + album);
			return null;
		}
		try {
			query = getReleaseQuery(album);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonObject = search(query);
		try {
			JSONArray results = jsonObject.getJSONArray("results");
			if (results.length()==0)
				throw new ReleaseNotFoundException(album.getTitle());
			JSONObject result = results.getJSONObject(0);
			
			//TODO: multi genre
			albumInfo.setStyles(result.getJSONArray("style"));
			albumInfo.setGenres(result.getJSONArray("genre"));

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
		sb.append("&artist=").append(album.getArtist());
		sb.append("&title=").append(album.getTitle());

		return URIUtil.encodeQuery(sb.toString());

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
