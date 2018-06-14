/*
 * Copyright 2018 org.dpr & croger
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

package diskong;

import java.util.ArrayList;
import java.util.List;

public class Statistics {


    private List<AlbumVo> missingImages = new ArrayList<>();

    /** Constructeur privé */
    private Statistics()
    {}

    public void addStats(AlbumVo album) {
        if (null==album.getFolderImagePath())
            missingImages.add(album);
    }

    /** Holder */
    private static class StatisticsHolder
    {
        /** Instance unique non préinitialisée */
        private final static Statistics instance = new Statistics();
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static Statistics getInstance()
    {
        return StatisticsHolder.instance;
    }
}
