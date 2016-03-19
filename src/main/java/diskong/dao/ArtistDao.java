package diskong.dao;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;

import diskong.old.DiscogClientJackson;
import diskong.pojos.Release;
import diskong.pojos.Result;
import diskong.pojos2.Artist;

public class ArtistDao {

	public Artist getByID(String ID) throws JsonParseException, JsonMappingException, IOException{
		
		DiscogClientJackson discogCli = new DiscogClientJackson();
		
		String jsString=discogCli.get("Release.ByID", ID);
		
		
		ObjectMapper mapper = new ObjectMapper();
	
			// for (int i = 0; i < jsonArray.length(); i++) {
			
			//System.out.println(jsonObject.toString());
			Artist art = mapper.readValue(jsString, Artist.class);

		
		return art;
	}
}
