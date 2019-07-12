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

package diskong.api;

import diskong.core.bean.IAlbumVo;
import diskong.core.EmptyResultException;

public abstract class AbstractDatabase {
	


	/**
	 * Search for an unique album release
	 * @param album
	 * @throws EmptyResultException
	 */
	public abstract IAlbumVo searchRelease(IAlbumVo album) throws EmptyResultException, ApiConfigurationException ;

	//protected  abstract void search(String query);

	//protected  abstract String getReleaseQuery(IAlbumVo album) throws Exception;

	public abstract boolean isAPIAvailable();
}
