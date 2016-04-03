package diskong.dao;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import diskong.pojos2.Artist;

public class ArtistDao {

	public Artist getByID(String ID) throws JsonParseException, JsonMappingException, IOException{
		
//		DiscogClientJackson discogCli = new DiscogClientJackson();
//		
//		String jsString=discogCli.get("Release.ByID", ID);
//		
//		
//		ObjectMapper mapper = new ObjectMapper();
//	
//			// for (int i = 0; i < jsonArray.length(); i++) {
//			
//			//System.out.println(jsonObject.toString());
//			Artist art = mapper.readValue(jsString, Artist.class);
//
//		
//		return art;
		//todo
		return null;
	}
}
