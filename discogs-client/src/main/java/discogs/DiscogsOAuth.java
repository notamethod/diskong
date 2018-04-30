package discogs;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import diskong.api.ApiConfigurationException;
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
	private static final String URL_API = "https://diskong.api.discogs.com/";


	private static final String PROP_CONSUMER_KEY = "DISCOGS_CONSUMER_KEY";
	private static final String PROP_CONSUMER_SECRET = "DISCOGS_CONSUMER_SECRET";
	private static final String PROP_ACCESS_OAUTH_TOKEN = "DISCOGS_ACCESS_OAUTH_TOKEN";
	private static final String PROP_ACCESS_OAUTH_TOKEN_SECRET = "DISCOGS_ACCESS_OAUTH_TOKEN_SECRET";
	private static final String REQUEST_TOKEN_URL = URL_API + "oauth/request_token";
	private static final String Authorize_URL = "https://www.discogs.com/oauth/authorize";
	private static final String ACCESS_TOKEN_URL = "https://diskong.api.discogs.com/oauth/access_token";
	private static final String REQUEST_OAUTH_TOKEN = "DISCOGS_REQUEST_OAUTH_TOKEN";
	private static final String REQUEST_OAUTH_TOKEN_SECRET = "DISCOGS_REQUEST_OAUTH_TOKEN_SECRET";
	private static final String PIN = "DISCOGS_PIN";


	
	final static Logger logger = LoggerFactory.getLogger(DiscogsOAuth.class);
	Properties authProperties = new Properties();
	private Client client;

	public DiscogsOAuth() throws ApiConfigurationException {
		try {
			authProperties.load(new FileInputStream(System.getProperty("user.home")+"/.shipkong/authapis.ini"));
		} catch (IOException e) {
			throw new ApiConfigurationException(e.getMessage(), e);
		}
		// Create a Jersey client
		client = Client.create();
		client.addFilter(new LoggingFilter());
	}



	public static void main(String[] args) {
		logger.info("Entering application.");
        try {
            DiscogsOAuth app = new DiscogsOAuth();

        } catch (ApiConfigurationException e) {
            e.printStackTrace();
        }

        // app.getProfile("croger42");
//		Map<String, String> params=app.getRequestToken();
//		app.authorize(params);
		//app.getAccessToken();
		// app.getAccessToken();
	//	 app.search("type=all&artist=babybird");
	}

	public Map<String, String> getRequestToken() {
		client.removeAllFilters();
		// Create a resource
		WebResource resource = client.resource(REQUEST_TOKEN_URL);

		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(authProperties.getProperty(PROP_CONSUMER_SECRET));
		OAuthParameters params = new OAuthParameters().consumerKey(authProperties.getProperty(PROP_CONSUMER_KEY)).signatureMethod("PLAINTEXT")
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


	public void authorize(Map<String,String> mapParams) {
		client.removeAllFilters();

		String oauthToken="oauth_token";
		// Create a resource
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
		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(authProperties.getProperty(PROP_CONSUMER_SECRET))
				.tokenSecret(authProperties.getProperty(PROP_ACCESS_OAUTH_TOKEN_SECRET));
		OAuthParameters params = new OAuthParameters().consumerKey(authProperties.getProperty(PROP_CONSUMER_KEY)).signatureMethod("HMAC-SHA1")
				.version("1.0").token(authProperties.getProperty(PROP_ACCESS_OAUTH_TOKEN));
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
