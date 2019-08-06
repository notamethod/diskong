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
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "album")
@IdClass(AlbumId.class)
public class AlbumEntity {

    @Id
    private String title;
    @Id
    private String artist;

    @Column
    private int year;

    @Lob
    private byte[] cover;

    @Column
    private int nbTracks;

    @ManyToMany
    private Collection<GenreEntity> genres ;


    public String getHash(){
        String key = DigestUtils.sha1Hex(artist+"---"+title);
        return key;
    }
    public AlbumEntity(){}

    public AlbumEntity(String title, String artist, int year) {
        this.title = title;
        this.artist = artist;
        this.year = year;
    }



}