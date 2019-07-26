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

package diskong.app.data.genre;

import diskong.app.common.SimpleStatObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository
        extends JpaRepository<GenreEntity, String> {

    @Query("SELECT " +
            "    new diskong.app.common.SimpleStatObject(genre.name, COUNT(*)) " +
            "FROM " +
            "     TrackEntity track  join track.album album " +
            "     join album.genres genre " +
            "GROUP BY " +
            "    genre.name")
    List<SimpleStatObject> findGenreCount();

     GenreEntity findByName(String name);
}
