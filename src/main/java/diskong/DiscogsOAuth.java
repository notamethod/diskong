package diskong;

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

/**
 * Hello world!
 * 
 */
public class DiscogsOAuth  {

	// base URL for the API calls
	private static final String URL_API = "https://api.discogs.com/";


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
	
	final static Logger logger = LoggerFactory.getLogger(DiscogsOAuth.class);
	private Client client;

	public DiscogsOAuth() {
		// Create a Jersey client
		client = Client.create();
		client.addFilter(new LoggingFilter());
	}



	public static void main(String[] args) {
		logger.info("Entering application.");
		DiscogsOAuth app = new DiscogsOAuth();

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
		Map<String, String> mapParams = new HashMap<String, String>();
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

	public WebResource addAuthentificationFilters(WebResource resource) {
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
