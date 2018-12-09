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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Handler for abcde command line
 * in order to use musicbrainz you have to install this:
 * sudo apt-get install libmusicbrainz-discid-perl libwebservice-musicbrainz-perl
 */
public class AbcdeHandler implements RipHandler {
    @Override
    public String getArtist() {
        return artist;
    }

    private Process p;

    @Override
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
    BufferedReader reader;

    public AbcdeHandler() throws URISyntaxException {
        InputStream is = null;
        try {

            ClassLoader classLoader = getClass().getClassLoader();
            is = classLoader.getResourceAsStream("abcde.conf");

            @NotNull
            URL u = Objects.requireNonNull(classLoader.getResource("abcde.conf"));

            configurationFileName = Paths.get(u.toURI()).toFile().getAbsolutePath();
            is = classLoader.getResourceAsStream("abcde.conf");
            ripProperties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Properties getRipProperties() {
        LOG.debug(configurationFileName);
        Enumeration e = ripProperties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            LOG.debug(key + " " + ripProperties.getProperty(key));
        }
        return ripProperties;
    }

    @Override
    public void stop() {
        InputStream is = p.getInputStream();

        p.destroy();
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(1000);
            String cdProcess = getCDProcess();
            if (cdProcess != null) {
                killProcess(cdProcess);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
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
        //TODO: can't parse if redirect !
        // pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        //  pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        try {
            p = pb.start();
            if (!p.waitFor(30, TimeUnit.SECONDS)) {
                exitCode = 88;
            } else {
                exitCode = p.exitValue();
            }
            reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                LOG.debug(line);
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
        } catch (IOException e) {
            exitCode = 99;
            throw new RipperException("abcde error", exitCode, e);
        } finally {
            if (exitCode == 88) {
                //TIME OUT on process
            }
            //OK
        }


        return actionResult;
    }

    @Override
    public ProcessBuilder processCb(List<String> actionList) throws RipperException {
        List<String> actionResult = null;

        int exitCode = 0;

        List<String> liste = new ArrayList<>();
        liste.add(COMMAND_NAME);
        liste.add("-c" + configurationFileName);

        String actions = "-a " + String.join(",", actionList);
        liste.addAll(actionList);
        System.out.println(liste.toString());
        ProcessBuilder pb = new ProcessBuilder(liste);

        return pb;
    }

    @Override
    public List<TrackInfo> parseTrack(List<String> actionResult) throws analyseException {
        List<TrackInfo> tracks = new ArrayList<>();
        for (String line : actionResult) {
            String[] splitted = line.split(":");
            if (splitted.length > 1) {
                TrackInfo track = new TrackInfo(Integer.valueOf(splitted[0]), splitted[1], artist);
                tracks.add(track);
            }
        }
        if (tracks.isEmpty())
            throw new analyseException("no tracks found");
        return tracks;
    }

    @Override
    public void parseState(List<String> actionResult, Map<Integer, String> state, List<String> filteredList, int start) throws analyseException {
        final String grab = "Grabbing track";
        for (String line : actionResult) {
            String[] splitted = line.split(":");
            if (splitted.length > 1) {
                if (splitted[0].contains(grab)) {
                    filteredList.add(line);
                    String numTrack = splitted[0].replace(grab, "").trim();
                    System.out.println("numtrack " + numTrack);
                    Integer num = null;
                    try {
                        num = Integer.parseInt(numTrack);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        num = null;
                    }
                    if (num != null) {
                        state.put(num - start, "...");
                    }
                }

            }
            if (line.contains("Done")) {
                for (Map.Entry<Integer, String> cursor : state.entrySet()) {
                    if ("...".equals(cursor.getValue())) {
                        cursor.setValue("OK");
                    }
                }
            }
            //if first parsing fails
            Integer splitRip = splitRip(line);
            if (splitRip != null) {
                filteredList.add(line);
                if (state.get(splitRip) == null) {
                    state.put(splitRip - start, "...");
                }
            }

        }

    }

    @Override
    public Integer splitRip(String line) {
        String ret = "";
        if (line.contains("Ripping from sector")) {
            String[] splitted2 = line.split("(track)");
            if (splitted2.length > 1) {
                String[] splitted3 = splitted2[1].split("\\[");
                ret = splitted3[0];
            }
        }
        if (ret != null && !ret.isEmpty()) {
            return Integer.valueOf(ret.trim());
        }
        return null;
    }

    private List<String> parseInfo(String result, List<String> actionList) throws IOException {
        String line;
        List<String> sb = new ArrayList<>();
        BufferedReader parseReader = new BufferedReader(new StringReader(result));
        String firstLine = null;
        while ((line = parseReader.readLine()) != null) {
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
        String[] split = trimedString.split("/");
        if (split.length == 2) {
            artist = split[0].trim();
            album = split[1].trim();
        }
        return trimedString;
    }

    @Override
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

    @Override
    public String getCoverImage() {
        String dir = ripProperties.getProperty(OUTPUT_DIR);
        File f = new File(dir, artist.replaceAll(" ", "_") + "-" + album.replaceAll(" ", "_"));

        //   File f = new File(dir, artist==null?"":artist.replaceAll(" ", "_")+"-"+album==null?"":album.replaceAll(" ", "_"));
        File image = new File(f, "cover.jpg");
        return image.getAbsolutePath();
    }

    @Override
    public String getCDProcess() throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"pgrep", "-lf", "cdparanoia"});
        StringBuilder sb = new StringBuilder();

        InputStream is = null;
        BufferedReader br = null;
        try {
            is = process.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            br.close();
            is.close();
        }

        String[] split = sb.toString().split(" ");
        if (split.length > 1)
            return split[0];

        return null;
    }

    private void killProcess(String pid) throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"kill", "-9", pid});
    }
    // /tmp/Dinosaur Jr.-I Bet on Sky/cover.jpg
    //  /tmp/Dinosaur_Jr.-I_Bet_on_Sky
    //sudo apt-get install libmusicbrainz-discid-perl libwebservice-musicbrainz-perl
}
