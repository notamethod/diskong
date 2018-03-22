package diskong.api.discogs.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
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
public class DiscogClientMoxy extends AbstractDatabase implements DatabaseSearch{

	// base URL for the API calls
	private static final String URL_API = "http://api.discogs.com/";
	private static final String URL_IDENTITY = "oauth/identity";
	private static final String URL_PROFILE = "users/";
	private static final String URL_SEARCH = "database/search?q=";
	

	private static final String CONSUMER_KEY = "FFtWwazbcskFPzduEFOK";
	private static final String CONSUMER_SECRET = "GOfYoDjZVsACWagKncvDJcjToXwoILJf";
	// http://www.discogs.com/oauth/authorize?oauth_token=OtaZUJvZoStvbCaHrIjaWgXOLxIZABcUVaNYJkfp
	private static final String REQUEST_TOKEN_URL = "http://api.discogs.com/oauth/request_token";
	private static final String Authorize_URL = "http://www.discogs.com/oauth/authorize&oauth_token=qfQxoAjaOmHyKNJxDTQzptikKMtGGhBrsyZUNVMz";
	private static final String ACCESS_TOKEN_URL = "http://api.discogs.com/oauth/access_token";
	private static final String REQUEST_OAUTH_TOKEN = "qfQxoAjaOmHyKNJxDTQzptikKMtGGhBrsyZUNVMz";
	private static final String PIN = "bqNrVGBall";
	private static final String ACCESS_OAUTH_TOKEN = "aHQpwraAUsWnpuhnfvCTlvUkNCimVlXJYDNHXiuQ";
	private static final String ACCESS_OAUTH_TOKEN_SECRET = "WCCHmmozfjbgezWJWJwQUXasKaKgWsoVMfbkCyay";
	// oauth_token_secret=WCCHmmozfjbgezWJWJwQUXasKaKgWsoVMfbkCyay&oauth_token=aHQpwraAUsWnpuhnfvCTlvUkNCimVlXJYDNHXiuQ
	// oauth_token_secret=vLBzEKasidOAroTRsfslCVkIGpSxMbPCymhoVhsb&oauth_token=
	private Client client;

	public DiscogClientMoxy() {
		// Create a Jersey client
		client = Client.create();
		client.addFilter(new LoggingFilter());
	}

	public static void main(String[] args) throws Exception {
		DiscogClientMoxy app = new DiscogClientMoxy();
		//app.getUserID();
		//app.getProfile("croger42");
		app.getRequestToken();
		app.search("type=all&artist=babybird");
	}

	public void getRequestToken() {
		client.removeAllFilters();
		//WebTarget ss;
		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(REQUEST_TOKEN_URL);

		// Set the OAuth parameters
		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(CONSUMER_SECRET);
		OAuthParameters params = new OAuthParameters().consumerKey(CONSUMER_KEY).signatureMethod("HMAC-SHA1")
				.version("1.0");
		// Create the OAuth client filter
		OAuthClientFilter oauthFilter = new OAuthClientFilter(client.getProviders(), params, secrets);

		// Add the filter to the resource
		resource.addFilter(oauthFilter);

		// make the request and print out the result
		System.out.println(resource.get(String.class));
	}

	public void getAccessToken() {
		client.removeAllFilters();

		// Set the OAuth parameters
		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(CONSUMER_SECRET).tokenSecret(
				"vLBzEKasidOAroTRsfslCVkIGpSxMbPCymhoVhsb");
		OAuthParameters params = new OAuthParameters().consumerKey(CONSUMER_KEY).signatureMethod("HMAC-SHA1")
				.version("1.0").token(REQUEST_OAUTH_TOKEN).verifier(PIN);
		// Create the OAuth client filter
		OAuthClientFilter oauthFilter = new OAuthClientFilter(client.getProviders(), params, secrets);

		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(ACCESS_TOKEN_URL);

		// Add the filter to the resource
		resource.addFilter(oauthFilter);

		// make the request and print out the result
		System.out.println(resource.get(String.class));
	}

	public void getUserID() {
		client.removeAllFilters();

		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(URL_API + URL_IDENTITY);

		// Add the filter to the resource
		resource.addFilter(getOauthFilter());

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
			Logger.getLogger(DiscogClientMoxy.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void getProfile(String userName) {
		client.removeAllFilters();

		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(URL_API + URL_PROFILE + userName);

		// Add the filter to the resource
		resource.addFilter(getOauthFilter());

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
			Logger.getLogger(DiscogClientMoxy.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void search(String query) {
		client.removeAllFilters();

		// Create a resource to be used to make Twitter API calls
		WebResource resource = client.resource(URL_API);

		// Add the filter to the resource
		resource.addFilter(getOauthFilter());
		String encQuery = null;
		try {
			encQuery = URIUtil.encodeQuery(URL_SEARCH + query);
		} catch (URIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        List<Object> customers = resource.path(encQuery).accept("application/json").get(new GenericType<List<Object>>(){});
		 
		// Parse the JSON array
		// JSONArray jsonArray = resource.get(JSONArray.class);
		List<String> statuses = new ArrayList<>();

		try {
			// for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = resource.get(JSONObject.class);
			StringBuilder builder = new StringBuilder();
//			builder.append(jsonObject.getString("username")).append(jsonObject.getString("resource_url"));
//			statuses.add(builder.toString());
			System.out.println(builder.toString());
			System.out.println(jsonObject.toString());
	       // List<Customer> customers = resource.path("/findCustomersByCity/Any%20Town").accept("application/json").get(new GenericType<List<Customer>>(){});
	        
//			Release rel = target
//			        .request()
//			        .post(Entity.entity(new MyObject("Duke", 18), MediaType.APPLICATION_JSON), MyObject.class);
			// }
		} catch (Exception ex) {
			Logger.getLogger(DiscogClientMoxy.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	private ClientFilter getOauthFilter() {
		// Set the OAuth parameters
		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(CONSUMER_SECRET)
				.tokenSecret(ACCESS_OAUTH_TOKEN_SECRET);
		OAuthParameters params = new OAuthParameters().consumerKey(CONSUMER_KEY).signatureMethod("HMAC-SHA1")
				.version("1.0").token(ACCESS_OAUTH_TOKEN);
		// Create the OAuth client filter
		return new OAuthClientFilter(client.getProviders(), params, secrets);

	}


	@Override
	protected String getReleaseQuery(IAlbumVo album) throws URIException {
		StringBuilder sb=new StringBuilder();
		sb.append("type=all");
		sb.append("&artist=").append(album.getArtist());
		sb.append("&album=").append(album.getTitle());
			
		return URIUtil.encodeQuery(sb.toString());

	}


}