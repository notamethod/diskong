package diskong;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Utils {
    /**
     * Download a file from an URL
     * @param url
     * @param target
     * @throws IOException
     */
    public static void downloadFile(String url, Path target) throws IOException {

        URL website = new URL(url);
        try (InputStream in = website.openStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
