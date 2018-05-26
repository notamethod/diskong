package diskong.rip;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AbcdeHandler {

    final static Logger logger = LoggerFactory.getLogger(AbcdeHandler.class);
    public final String OUTPUT_DIR = "OUTPUTDIR";
    public final String FORMAT_PREFIX = "-o ";
    public final String OUTPUT_FORMAT = "OUTPUTFORMAT";
    public final String WAVOUTPUTDIR = "WAVOUTPUTDIR";
    public final String DEFAULT_OUTPUT_FORMAT = "'${ARTISTFILE}-${ALBUMFILE}/${TRACKNUM}.${TRACKFILE}'";
    final String COMMAND_NAME = "abcde";
    Properties ripProperties = new Properties();
    String configurationFileName;

    public AbcdeHandler() throws URISyntaxException {
        InputStream is = null;
        try {

            ClassLoader classLoader = getClass().getClassLoader();
            is = classLoader.getResourceAsStream("abcde.conf");
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

    public String process(Map<String, String> params, List<String> actionList) throws RipperException {
        String actionResult="";
        try {
            configure(params);
        } catch (IOException e) {
            throw new RipperException("configuration error", e);
        }
        int exitCode = 0;

        List<String> liste = new ArrayList<>();
        liste.add(COMMAND_NAME);
        liste.add("-c" + configurationFileName);
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
//            System.out.println("result: " + result);
            actionResult= parseInfo(result, actionList);
            if (exitCode != 0) {
                BufferedReader output = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                parseError(output, exitCode);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            exitCode = 99;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            exitCode = 99;
        }finally {
            if (exitCode!=0)
                throw new RipperException("exit code is "+exitCode);
        }


        return actionResult;
    }

    private String parseInfo(String result, List<String> actionList) throws IOException {
        String line;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new StringReader(result));
        boolean firstLine = false;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#"))
                if (line.startsWith("#1"))
                    firstLine = true;
                else
                    firstLine = false;
            if (firstLine && !line.startsWith("#1"))
                sb.append(line).append(System.getProperty("line.separator"));
            if (firstLine && line.startsWith("#1"))
                sb.append(parseHeader(line)).append(System.getProperty("line.separator"));


        }
        return sb.toString();
    }

    private String parseHeader(String line) {
        String trimedString = line.replaceAll("----", "");
        String[] strArray = trimedString.split(" ");
        for (int i=0; i<4;i++) {
            trimedString = trimedString.replaceAll(strArray[i], "");
        }
        return trimedString;
    }

    private void configure(Map<String, String> params) throws IOException {
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

    private void parseError(BufferedReader output, int exitCode) throws RipperException {
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
                throw new RipperException(message + sb.toString());
            }
        } catch (IOException e) {
            throw new RipperException("Error executing process", e);

        }

    }
}
