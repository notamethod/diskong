package diskong;

public class DatabaseSearchFactory {
	
	Enum SearchEngines;

	public static DatabaseSearch getApi(SearchAPI api) {
		
		return new DiscogSearch();
	}

}
