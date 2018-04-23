package diskong.api;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diskong.IAlbumVo;
import diskong.ReleaseNotFoundException;

public abstract class AbstractDatabase {
	
	protected final static Logger log = LoggerFactory.getLogger(AbstractDatabase.class);


	/**
	 * Search for an unique album release
	 * @param album
	 * @throws ReleaseNotFoundException 
	 */
	public IAlbumVo searchRelease(IAlbumVo album) throws ReleaseNotFoundException, ApiConfigurationException {
		String query=null;
		if (album==null ||  StringUtils.isBlank(album.getTitle())){
			log.error("insufficient data for search query..."+album);
			return null;
		}
		try {
			query = getReleaseQuery(album);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//search(query);
		return null;
	}

	//protected  abstract void search(String query);

	protected  abstract String getReleaseQuery(IAlbumVo album) throws Exception;

	public abstract boolean isAPIAvailable();
}
