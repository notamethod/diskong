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

package diskong.app.cdrip;

import diskong.core.TrackInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface RipHandler {
    String getArtist();

    String getAlbum();

    Properties getRipProperties();

    void stop();

    List<String> process(List<String> actionList) throws RipperException;

    ProcessBuilder processCb(List<String> actionList) throws RipperException;

    List<TrackInfo> parseTrack(List<String> actionResult) throws analyseException;

    void parseState(List<String> actionResult, Map<Integer, String> state, List<String> filteredList, int start) throws analyseException;

    Integer splitRip(String line);

    void configure(Map<String, String> params) throws IOException;

    String getCoverImage();

    String getCDProcess() throws IOException;

}
