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

package diskong.app.data.album;

import diskong.app.data.genre.GenreEntity;
import diskong.core.bean.AlbumVo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;

@Getter
@Setter
@Entity
@Table(name = "album")
public class AlbumEntity {

    @Id @GeneratedValue long id;

    @Column(length = 240, nullable = false)
    private String title;

    @Column(length = 240, nullable = false)
    private String artist;

    @Column
    private int year;

    @ManyToMany
    private Collection<GenreEntity> genres ;


    public AlbumEntity(){};

    public AlbumEntity(String title, String artist, int year) {
        this.title = title;
        this.artist = artist;
        this.year = year;
    }

    public AlbumEntity(AlbumVo album) {
        this.artist= (String) album.getArtist();
        this.title = album.getTitle();
        if (!album.getYear().isEmpty())
            this.year = Integer.parseInt(album.getYear());


    }

}