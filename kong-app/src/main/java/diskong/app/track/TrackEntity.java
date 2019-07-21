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

package diskong.app.track;

import diskong.core.bean.TrackInfo;

import javax.persistence.*;
@Entity
@Table(name = "track")
public class TrackEntity {

    @Id @GeneratedValue long id;

    @Column(length = 240, nullable = false)
    private String title;

    @Column(length = 240, nullable = false)
    private String artist;

    @Column(nullable = false)
    private String path;

    public TrackEntity(){};

    public TrackEntity(TrackInfo trackInfo) {
        this.artist= (String) trackInfo.getArtist();
        this.title = trackInfo.getTitle();
        this.path = trackInfo.getfPath().getFile().getAbsolutePath();

    }
}