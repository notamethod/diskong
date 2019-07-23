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

package diskong.api;

import diskong.app.data.track.TrackEntity;
import diskong.core.bean.AlbumVo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TrackList {
    private List<TrackEntity> tracks = new ArrayList<>();
    private String title;
    private String id;

    public TrackList(List<TrackEntity> tracks, String title) {
        this.tracks = tracks;
        this.title = title;
    }

    public AlbumVo toAlbum() {
        return null;
    }

    public String getFolderImagePath() {
        return null;
    }
}
