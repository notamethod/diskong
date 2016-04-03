package diskong.api;

import diskong.api.discogs.DiscogSearch;

public class DatabaseSearchFactory { 
	
	Enum SearchEngines;

	public static DatabaseSearch getApi(SearchAPI api) {
		
		return new DiscogSearch();
	}

}
