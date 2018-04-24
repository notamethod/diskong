package diskong.api;

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
	public abstract IAlbumVo searchRelease(IAlbumVo album) throws ReleaseNotFoundException, ApiConfigurationException ;

	//protected  abstract void search(String query);

	protected  abstract String getReleaseQuery(IAlbumVo album) throws Exception;

	public abstract boolean isAPIAvailable();
}
