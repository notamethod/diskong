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
@Table(name = "TRACK")
public class TrackEntity {

    @Id
    @Column(name = "ID", length = 240,  nullable = false)
    private String id;

    @Column(name = "TITLE", length = 240, nullable = false)
    private String title;

    @Column(name = "ARTIST", length = 240, nullable = false)
    private String artist;

    public TrackEntity(){};

    public TrackEntity(TrackInfo trackInfo) {
        this.id="xxxx";
        this.artist= (String) trackInfo.getArtist();
        this.title = trackInfo.getTitle();

    }
}