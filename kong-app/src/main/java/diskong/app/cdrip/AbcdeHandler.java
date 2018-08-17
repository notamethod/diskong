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


import diskong.gui.TrackVO;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AbcdeHandler {
    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    final static Logger LOG = LoggerFactory.getLogger(AbcdeHandler.class);

    public final String OUTPUT_DIR = "OUTPUTDIR";
    public final String FORMAT_PREFIX = "-o ";
    public final String OUTPUT_FORMAT = "OUTPUTFORMAT";
    public final String WAVOUTPUTDIR = "WAVOUTPUTDIR";
    public final String DEFAULT_OUTPUT_FORMAT = "'${ARTISTFILE}-${ALBUMFILE}/${TRACKNUM}.${TRACKFILE}'";
    private final String TITLE_SEPARATOR = "----";
    private final String COMMAND_NAME = "abcde";
    final String CDDB = "CDDBMETHOD";
    final Properties ripProperties = new Properties();
    private String configurationFileName;
    private String artist;
    private String album;

    public AbcdeHandler() throws URISyntaxException {
        InputStream is = null;
        try {

            ClassLoader classLoader = getClass().getClassLoader();
            is = classLoader.getResourceAsStream("abcde.conf");

            @NotNull
            URL u = classLoader.getResource("abcde.conf");

            configurationFileName = Paths.get(u.toURI()).toFile().getAbsolutePath();
            is = classLoader.getResourceAsStream("abcde.conf");
            ripProperties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getRipProperties() {
        System.out.println(configurationFileName);
        Enumeration e = ripProperties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            System.out.println(key);
            System.out.println(ripProperties.getProperty(key));
            // do what you want
        }
        return ripProperties;
    }

    public List<String> process(List<String> actionList) throws RipperException {
        List<String> actionResult = null;

        int exitCode = 0;

        List<String> liste = new ArrayList<>();
        liste.add(COMMAND_NAME);
        liste.add("-c" + configurationFileName);

        String actions = "-a " + String.join(",", actionList);
        liste.addAll(actionList);
        System.out.println(liste.toString());
        ProcessBuilder pb = new ProcessBuilder(liste);
        try {
            Process p = pb.start();
            if (!p.waitFor(30, TimeUnit.SECONDS)) {
                exitCode = 88;
            } else {
                exitCode = p.exitValue();
            }
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            actionResult = parseInfo(result, actionList);
            if (exitCode != 0) {
                BufferedReader output = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                throw new RipperException(parseError(output, exitCode), result, exitCode);
            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            exitCode = 98;
            throw new RipperException("exit code is " + exitCode);
        } catch (IOException  e) {
            exitCode = 99;
            throw new RipperException("abcde error", exitCode, e);
        } finally {
            if (exitCode == 88){

            }
            //OK
        }


        return actionResult;
    }

    public List<TrackVO> parseTrack(List<String> actionResult) throws analyseException {
        List<TrackVO> tracks = new ArrayList<>();
        for (String line:actionResult){
            String[] splitted = line.split(":");
            if(splitted.length>1) {
                TrackVO track = new TrackVO(Integer.valueOf(splitted[0]), splitted[1], artist);
                tracks.add(track);
            }
        }
        if (tracks.isEmpty())
            throw new analyseException("no tracks found");
        return tracks;
    }

    private List<String> parseInfo(String result, List<String> actionList) throws IOException {
        String line;
        List<String> sb = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(result));
        String firstLine = null;
        while ((line = reader.readLine()) != null) {
            if (null == firstLine)
                firstLine = getTitle(line);
            if (null != firstLine && !firstLine.equals(line))
                sb.add(line);//.append(System.getProperty("line.separator"));
            if (null != firstLine && firstLine.equals(line))
                parseHeader(line);
        }
        return sb;
    }

    private String getTitle(String line) {
        if (line.startsWith("#"))
            if (line.startsWith("#1"))
                return line;
        if (line.startsWith(TITLE_SEPARATOR))
            return line;
        return null;

    }

    private String parseHeader(String line) {
        String trimedString = line.replaceAll("----", "");
//        String[] strArray = trimedString.split(" ");
//        for (int i = 0; i < 4; i++) {
//            trimedString = trimedString.replaceAll(strArray[i], "");
//        }
        if (null!=trimedString){
            String[] split = trimedString.split("/");
            if (split.length==2){
                artist=split[0].trim();
                album = split[1].trim();
            }
        }
        return trimedString;
    }

    public void configure(Map<String, String> params) throws IOException {
        boolean isEdited = false;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value = ripProperties.getProperty(entry.getKey());
            if (value == null || !value.equals(entry.getValue())) {
                isEdited = true;
                ripProperties.setProperty(entry.getKey(), entry.getValue());
            }
        }
        if (isEdited) {

            FileOutputStream fileOut = new FileOutputStream(new File(configurationFileName));
            ripProperties.store(fileOut, "");
            fileOut.close();
        }
    }

    private String parseError(BufferedReader output, int exitCode) throws RipperException {
        String ligne;
        try {
            String message = "";
            StringBuilder sb = new StringBuilder();

            while ((ligne = output.readLine()) != null) {
                if (ligne.toLowerCase().contains("no medium found".toLowerCase())) {
                    message = "No medium found";

                }

                sb.append(System.getProperty("line.separator"));
                sb.append(ligne);

            }
            return message + sb.toString();
        } catch (IOException e) {
            throw new RipperException("Error executing process", e);

        }

    }

    public String getCoverImage() {
        String dir = ripProperties.getProperty(OUTPUT_DIR);
        File f = new File(dir, artist.replaceAll(" ", "_")+"-"+album.replaceAll(" ", "_"));
        File image = new File(f, "cover.jpg");
        return image.getAbsolutePath();
    }
   // /tmp/Dinosaur Jr.-I Bet on Sky/cover.jpg
   //  /tmp/Dinosaur_Jr.-I_Bet_on_Sky
    //sudo apt-get install libmusicbrainz-discid-perl libwebservice-musicbrainz-perl
}
