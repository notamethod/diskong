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
import diskong.core.bean.TrackInfo;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(name = "track")
public class TrackEntity implements Comparable<TrackEntity>{

    @Id @GeneratedValue long id;

    @Column(length = 240, nullable = false)
    private String title;

    @Column(length = 240, nullable = false)
    private String artist;

    @Column(nullable = false)
    private String path;

    @Column
    private int number;

    @ManyToOne
    private AlbumEntity album ;

    public TrackEntity(){};

    public TrackEntity(TrackInfo trackInfo) {
        this.artist= (String) trackInfo.getArtist();
        this.title = trackInfo.getTitle();
        this.path = trackInfo.getfPath().getFile().getAbsolutePath();
    }
    public TrackEntity(TrackInfo trackInfo, AlbumEntity entity) {
        this.artist= (String) trackInfo.getArtist();
        this.title = trackInfo.getTitle();
        this.path = trackInfo.getfPath().getFile().getAbsolutePath();
        this.album = entity;
        this.number=Integer.valueOf(trackInfo.getNumber());
    }

    @Override
    public int compareTo(@NotNull TrackEntity o) {

                try {
                    return (number - o.getNumber());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }

}