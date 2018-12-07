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

import org.junit.Test;

import java.net.URISyntaxException;
import java.util.*;

import static org.junit.Assert.*;

public class AbcdeHandlerTest {

    private String[] testLists = {"Grabbing track 0bs7: The World's First Ever Monster Truck Front Flip...", " cdparanoia III release 10.2 (September 11, 2008)", "Ripping from sector  106454 (track  7 [0:00.00])", " Done.", "Grabbing track 08: Science Fiction...", " cdparanoia III release 10.2 (September 11, 2008)", " Ripping from sector  119955 (track  8 [0:00.00])", " Done.", "Grabbing track 09: She Looks Like Fun..."};

    @Test
    public void splitRipOne() {
        String line = "Ripping from sector   42246 (track  3 [0:00.00])";
        try {
            AbcdeHandler handler = new AbcdeHandler();
            assertEquals(handler.splitRip(line), Integer.valueOf(3));

        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testParse() {
        Map<Integer, String> state = new HashMap<>();
        try {
            AbcdeHandler handler = new AbcdeHandler();
            handler.parseState(Arrays.asList(testLists), state, new ArrayList<String>(),1);
            int nbOk = 0;
            for (Map.Entry<Integer, String> cursor : state.entrySet()) {
                System.out.println(cursor.getKey() + " | " + cursor.getValue());
                if (cursor.getValue().equalsIgnoreCase("ok"))
                    nbOk++;
            }
            assertEquals(2, nbOk);
        } catch (URISyntaxException | analyseException e) {
            e.printStackTrace();
            fail();
        }

    }


}