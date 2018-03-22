package diskong.api.discogs.todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
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
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import diskong.IAlbumVo;
import diskong.api.AbstractDatabase;
import diskong.api.DatabaseSearch;

/**
 * Hello world!
 * 
 */
public class DiscogClient extends AbstractDatabase implements DatabaseSearch {

	// base URL for the API calls
	private static final String URL_API = "https://api.discogs.com/";
	// private static final String URL_API = "http://localhost:8080/";
	private static final String URL_IDENTITY = "oauth/identity";
	private static final String URL_PROFILE = "users/";
	private static final String URL_SEARCH = "database/search?";

	private static final String CONSUMER_KEY = "FFtWwazbcskFPzduEFOK";
	private static final String CONSUMER_SECRET = "GOfYoDjZVsACWagKncvDJcjToXwoILJf";
	// http://www.discogs.com/oauth/authorize?oauth_token=OtaZUJvZoStvbCaHrIjaWgXOLxIZABcUVaNYJkfp
	// http://www.discogs.com/oauth/authorize?oauth_token=qfQxoAjaOmHyKNJxDTQzptikKMtGGhBrsyZUNVMz
	private static final String REQUEST_TOKEN_URL = URL_API + "oauth/request_token";
	private static final String Authorize_URL = "https://www.discogs.com/oauth/authorize";
	private static final String ACCESS_TOKEN_URL = "https://api.discogs.com/oauth/access_token";
	private static final String REQUEST_OAUTH_TOKEN = "rhTaolHuQCieyGZeJlgEccdtcxjBjUsvsUigSlSh";
	private static final String REQUEST_OAUTH_TOKEN_SECRET = "aKrtKqSlLCkjQsgFAIWflijiTKSKQwmPgYIdMYdl";
	private static final String PIN = "WihvwLTsqo";
	private static final String ACCESS_OAUTH_TOKEN = "OtbhfgmnaUlppueocdVwwefaseWshLfQiMVBydKc";
	private static final String ACCESS_OAUTH_TOKEN_SECRET = "EuttCNYaILknXnXdZKyFBBAYEXsPJfJUyBRqAXoe";
//	// oauth_token_secret=byLxdKQdmAmrGojZKiYZBKSeHmwrgdrSTQrSTRSQ&oauth_token=OAUszICFIcekTTMaQAzarDRfwLTGPmUroXRQICwL
//	oauth_token_secret=&oauth_token=&oauth_callback_confirmed=true
//			retour:oauth_token_secret=aKrtKqSlLCkjQsgFAIWflijiTKSKQwmPgYIdMYdl&oauth_token=rhTaolHuQCieyGZeJlgEccdtcxjBjUsvsUigSlSh&oauth_callback_confirmed=true
//			https://www.discogs.com/oauth/authorize?oauth_token=rhTaolHuQCieyGZeJlgEccdtcxjBjUsvsUigSlSh
	
	final static Logger logger = LoggerFactory.getLogger(DiscogClient.class);
	private Client client;

	public DiscogClient() {
		// Create a Jersey client
		client = Client.create();
		client.addFilter(new LoggingFilter());
	}



	public static void main(String[] args) {
		logger.info("Entering application.");
		DiscogClient app = new DiscogClient();
		 app.getUserID();
		// app.getProfile("croger42");
//		Map<String, String> params=app.getRequestToken();
//		app.authorize(params);
		//app.getAccessToken();
		// app.getAccessToken();
	//	 app.search("type=all&artist=babybird");
	}

	public Map<String, String> getRequestToken() {
		client.removeAllFilters();
		// WebTarget ss;
		// Create a resource to be used to make Twitter API calls
		// WebResource resource =
		// client.resource("http://google.fr"/*REQUEST_TOKEN_URL*/);
		WebResource resource = client.resource(REQUEST_TOKEN_URL);

		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(CONSUMER_SECRET);
		OAuthParameters params = new OAuthParameters().consumerKey(CONSUMER_KEY).signatureMethod("PLAINTEXT")
				.version("1.0a").callback("oob");
		// Create the OAuth client filter
		OAuthClientFilter oauthFilter = new OAuthClientFilter(client.getProviders(), params, secrets);

		resource.addFilter(new LoggingFilter(System.out));
		resource.addFilter(new UserAgentFilter(HttpHeaders.USER_AGENT, "<<diskong-user-agent>>"));
		// Add the filter to the resource
		resource.addFilter(oauthFilter);
		String response = resource.get(String.class);
		Map<String, String> mapParams = new HashMap<>();
		for (String reqParm : response.split("&")) {
			String[] val = reqParm.split("=");
			mapParams.put(val[0], val[1]);
		}
		// resource.toString();
		// make the request and print out the result
		System.out.println("retour:" + response);
		return mapParams;
	}

	public void getAccessToken() {
		client.removeAllFilters();

		// Set the OAuth parameters
		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(CONSUMER_SECRET)
				.tokenSecret(REQUEST_OAUTH_TOKEN_SECRET);
		OAuthParameters params = new OAuthParameters().consumerKey(CONSUMER_KEY).signatureMethod("PLAINTEXT")
				.version("1.0a").token(REQUEST_OAUTH_TOKEN).verifier(PIN);
		// Create the OAuth client filter
		OAuthClientFilter oauthFilter = new OAuthClientFilter(client.getProviders(), params, secrets);

		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(ACCESS_TOKEN_URL);

		// Add the filter to the resource
		resource.addFilter(oauthFilter);
		resource.addFilter(new LoggingFilter(System.out));
		resource.addFilter(new UserAgentFilter(HttpHeaders.USER_AGENT, "<<diskong-user-agent>>"));
		// make the request and print out the result
		System.out.println(resource.get(String.class));
	}

	public void authorize(Map<String,String> mapParams) {
		client.removeAllFilters();

		String oauthToken="oauth_token";
		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(Authorize_URL).queryParam(oauthToken, mapParams.get(oauthToken));
		System.out.println(resource.toString());
		// Add the filter to the resource
//		resource.addFilter(getOauthFilter());
//		resource.addFilter(new LoggingFilter(System.out));
//		resource.addFilter(new UserAgentFilter(HttpHeaders.USER_AGENT, "<<diskong-user-agent>>"));
//		// make the request and print out the result
//		System.out.println(resource.get(String.class));
	}

	public void getUserID() {
		client.removeAllFilters();

		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(URL_API + URL_IDENTITY);

		// Add the filter to the resource
		//resource.addFilter(getOauthFilter());
		addOauthFilters(resource);
		// Parse the JSON array
		// JSONArray jsonArray = resource.get(JSONArray.class);
		List<String> statuses = new ArrayList<>();

		try {
			// for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = resource.get(JSONObject.class);
			StringBuilder builder = new StringBuilder();
			builder.append(jsonObject.getString("username")).append(jsonObject.getString("resource_url"));
			statuses.add(builder.toString());
			System.out.println(builder.toString());
			// }
		} catch (JSONException ex) {
			logger.error(DiscogClient.class.getName(), ex);
		}
	}

	public void getProfile(String userName) {
		client.removeAllFilters();

		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(URL_API + URL_PROFILE + userName);

		// Add the filter to the resource
		addOauthFilters(resource);
		// Parse the JSON array
		// JSONArray jsonArray = resource.get(JSONArray.class);
		List<String> statuses = new ArrayList<>();

		try {
			// for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = resource.get(JSONObject.class);
			StringBuilder builder = new StringBuilder();
			builder.append(jsonObject.getString("username")).append(jsonObject.getString("resource_url"));
			statuses.add(builder.toString());
			System.out.println(builder.toString());
			System.out.println(jsonObject.toString());
			// }
		} catch (JSONException ex) {
			logger.error(DiscogClient.class.getName(), ex);
		}
	}

	public void search(String query) {
		client.removeAllFilters();

		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(URL_API + URL_SEARCH + query);

		// Add the filter to the resource
		addOauthFilters(resource);
		// Parse the JSON array
		// JSONArray jsonArray = resource.get(JSONArray.class);
		List<String> statuses = new ArrayList<>();

		try {
			// for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = resource.get(JSONObject.class);
			StringBuilder builder = new StringBuilder();
			// builder.append(jsonObject.getString("username")).append(jsonObject.getString("resource_url"));
			// statuses.add(builder.toString());
			System.out.println(builder.toString());
			System.out.println(jsonObject.toString());
			// List<Customer> customers =
			// resource.path("/findCustomersByCity/Any%20Town").accept("application/json").get(new
			// GenericType<List<Customer>>(){});

			// Release rel = target
			// .request()
			// .post(Entity.entity(new MyObject("Duke", 18),
			// MediaType.APPLICATION_JSON), MyObject.class);
			// }
		} catch (Exception ex) {
			logger.error(DiscogClient.class.getName(), ex);
		}
	}

	private WebResource addOauthFilters(WebResource resource) {
		// Set the OAuth parameters
		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(CONSUMER_SECRET)
				.tokenSecret(ACCESS_OAUTH_TOKEN_SECRET);
		OAuthParameters params = new OAuthParameters().consumerKey(CONSUMER_KEY).signatureMethod("HMAC-SHA1")
				.version("1.0").token(ACCESS_OAUTH_TOKEN);
		// Create the OAuth client filter
		resource.addFilter(new LoggingFilter(System.out));
		resource.addFilter(new UserAgentFilter(HttpHeaders.USER_AGENT, "<<diskong-user-agent>>"));
		resource.addFilter(new OAuthClientFilter(client.getProviders(), params, secrets));
		
		return resource;

	}



	/* (non-Javadoc)
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
