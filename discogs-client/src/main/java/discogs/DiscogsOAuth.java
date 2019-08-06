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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import diskong.api.ApiConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.google.common.net.HttpHeaders;
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

import javax.ws.rs.core.HttpHeaders;

/**
 * Hello world!
 * 
 */
class DiscogsOAuth  {

	// base URL for the API calls
	private static final String URL_API = "https://api.discogs.com/";


	private static final String PROP_CONSUMER_KEY = "DISCOGS_CONSUMER_KEY";
	private static final String PROP_CONSUMER_SECRET = "DISCOGS_CONSUMER_SECRET";
	private static final String PROP_ACCESS_OAUTH_TOKEN = "DISCOGS_ACCESS_OAUTH_TOKEN";
	private static final String PROP_ACCESS_OAUTH_TOKEN_SECRET = "DISCOGS_ACCESS_OAUTH_TOKEN_SECRET";
	private static final String REQUEST_TOKEN_URL = URL_API + "oauth/request_token";
	private static final String Authorize_URL = "https://www.discogs.com/oauth/authorize";
	private static final String ACCESS_TOKEN_URL = "https://api.discogs.com/oauth/access_token";
	private static final String REQUEST_OAUTH_TOKEN = "DISCOGS_REQUEST_OAUTH_TOKEN";
	private static final String REQUEST_OAUTH_TOKEN_SECRET = "DISCOGS_REQUEST_OAUTH_TOKEN_SECRET";
	private static final String PIN = "DISCOGS_PIN";


	
	private final static Logger logger = LoggerFactory.getLogger(DiscogsOAuth.class);
	private Properties authProperties = new Properties();
	private Client client;

	public DiscogsOAuth() throws ApiConfigurationException {
		try {
			authProperties.load(new FileInputStream(System.getProperty("user.home")+"/.shipkong/authapis.ini"));
		} catch (IOException e) {
			throw new ApiConfigurationException(e.getMessage(), e);
		}
		// Create a Jersey client
		client = Client.create();
		client.addFilter(new LoggingFilter(new JulFacade()));
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

		resource.addFilter(new LoggingFilter(new JulFacade()));

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

	}

	public WebResource addAuthentificationFilters(WebResource resource) {
		// Set the OAuth parameters
		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(authProperties.getProperty(PROP_CONSUMER_SECRET))
				.tokenSecret(authProperties.getProperty(PROP_ACCESS_OAUTH_TOKEN_SECRET));
		OAuthParameters params = new OAuthParameters().consumerKey(authProperties.getProperty(PROP_CONSUMER_KEY)).signatureMethod("HMAC-SHA1")
				.version("1.0").token(authProperties.getProperty(PROP_ACCESS_OAUTH_TOKEN));
		// Create the OAuth client filter
		resource.addFilter(new LoggingFilter(new JulFacade()));
		resource.addFilter(new UserAgentFilter(HttpHeaders.USER_AGENT, "<<diskong-user-agent>>"));
		resource.addFilter(new OAuthClientFilter(client.getProviders(), params, secrets));
		
		return resource;

	}



	class UserAgentFilter extends ClientFilter {
		private final String header;
		private final String value;

		UserAgentFilter(String header, String value) {
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





    private static class JulFacade extends java.util.logging.Logger {
        JulFacade() { super("Jersey", null); }
        @Override public void info(String msg) { logger.trace(msg); }
    }

}
