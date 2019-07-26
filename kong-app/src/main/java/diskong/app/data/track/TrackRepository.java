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

package diskong.app.data.track;

import diskong.app.common.SimpleStatObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository
        extends JpaRepository<TrackEntity, String> {

    @Query("SELECT " +
            "    new diskong.app.common.SimpleStatObject(album.artist, COUNT(*)) " +
            "FROM " +
            "     TrackEntity track  join track.album album " +
            "GROUP BY " +
            "    album.artist")
    List<SimpleStatObject> findArtistCount();

    @Query("SELECT " +
            "    new diskong.app.common.SimpleStatObject(album.title, COUNT(*)) " +
            "FROM " +
            "     TrackEntity track  join track.album album " +
            "GROUP BY " +
            "    album.title")
    List<SimpleStatObject> findAlbumCount();


    @Query("SELECT " +
            "    track " +
            "FROM " +
            "     TrackEntity track  join track.album album " +
            "where " +
            "    album.artist =?1 order by track.number")
    List<TrackEntity> findByArtistAlbum(String artist);
    @Query("SELECT " +
            "    track " +
            "FROM " +
            "     TrackEntity track  join track.album album " +
            "where " +
            "    album.title =?1 order by track.number")
    List<TrackEntity> findByAlbum(String label);
    @Query("SELECT " +
            "    track " +
            "FROM " +
            "     TrackEntity track  join track.album album join " +
            "     album.genres genre " +
            "where " +
            "    genre.name =?1 order by track.number")
    List<TrackEntity> findByGenre(String label);
}
