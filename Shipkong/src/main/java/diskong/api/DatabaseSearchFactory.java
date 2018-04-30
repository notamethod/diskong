package diskong.api;

import discogs.DiscogSearch;

public class DatabaseSearchFactory { 
	
	Enum SearchEngines;

	public static DatabaseSearch getApi(SearchAPI api) {
		
		return new DiscogSearch();
	}

}
