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
import diskong.app.data.album.AlbumEntity;
import diskong.app.data.album.AlbumRepository;
import diskong.app.data.track.TrackRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TrackEntityRepositoryTest {


    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    public void injectedComponentsAreNotNull(){

        assertThat(trackRepository).isNotNull();
    }

    @Test
    public void findByArtistAlbum(){
        AlbumEntity album = new AlbumEntity("title", "artist",0);
        AlbumEntity entity = albumRepository.save(album);
        TrackEntity track = new TrackEntity();
        track.setAlbum(entity);
        track.setArtist("art");
        track.setTitle("title");
        track.setPath("path");
        trackRepository.save(track);
         track = new TrackEntity();
        track.setAlbum(entity);
        track.setArtist("art2");
        track.setTitle("title2");
        track.setPath("path");
        trackRepository.save(track);
        assertThat(trackRepository).isNotNull();
        List<TrackEntity> list=trackRepository.findByArtistAlbum("artist");
        assertThat(list).hasSize(2);

    }
}