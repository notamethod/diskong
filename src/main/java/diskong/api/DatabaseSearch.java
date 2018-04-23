package diskong.api;

import diskong.IAlbumVo;
import diskong.ReleaseNotFoundException;

public interface DatabaseSearch {

	IAlbumVo searchRelease(IAlbumVo album) throws ReleaseNotFoundException, ApiConfigurationException;

}
