/*
 * Copyright 2019 org.dpr & croger
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

package diskong.app.services;

import diskong.app.data.album.AlbumEntity;
import diskong.app.common.SimpleStatObject;
import diskong.app.data.genre.GenreEntity;
import diskong.app.data.track.TrackEntity;
import diskong.core.bean.AlbumVo;

import java.util.Collection;
import java.util.List;

public interface DataService {

    Collection<TrackEntity> findAll();
    TrackEntity findOne(Long id);
    TrackEntity create(TrackEntity greeting);
    AlbumEntity createAlbum(AlbumEntity album);

    List<SimpleStatObject> findArtistCount();

    List<SimpleStatObject> findAlbumCount();

    List<TrackEntity> findTrackByArtist(String label);

    List<TrackEntity> findTrackByAlbum(String label);

    List<TrackEntity> findTrackByGenre(String label);

    List<SimpleStatObject> findGenreCount();

    GenreEntity findOrSaveGenre(String name);

    AlbumEntity createAlbum(AlbumVo album);

    long countTrack();

    void saveAlbum(AlbumEntity entity);
}
