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

package diskong.gui;

import diskong.core.TrackInfo;

import java.util.HashMap;
import java.util.Map;

public class TrackRipModel extends TrackModel {
    public Map<Integer, String> getState() {
        return state;
    }

    Map<Integer,String> state= new HashMap();
    public TrackRipModel() {
        super();
        colName=new String[]{"Num.", "Title","Artist", "Rip"};
    }


    protected int[] prefSize=new int[]{10,150,80,50};

    @Override
    protected Object getSpecValue(int row, int column) {
        switch (column) {

            case 3:
                return state.get(row);
            default :

                return "";
        }
    }

}
