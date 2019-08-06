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

package diskong.parser;

import diskong.core.FilePath;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NioDirectoryParser implements DirectoryParser {

	private final static Logger LOG = LoggerFactory.getLogger(NioDirectoryParser.class);

	private static int cpt = 0;

	private Map<Path, List<FilePath>> map = new HashMap<>();


	public Map<Path, List<FilePath>> parse(@NotNull File file) throws FileNotFoundException {
		//Path dirPath = FileSystems.getDefault().getPath(dirName);
        if (!file.exists()){
            throw  new FileNotFoundException(file.getAbsolutePath());
        }
		Path dirPath = file.toPath();
		long startTime = System.currentTimeMillis();

		LOG.debug("parsing..." + file.getAbsolutePath());
		parsePath(dirPath);
		long endTime = System.currentTimeMillis();
		LOG.info(cpt + " files parsed in " + (endTime - startTime) + " ms");
		return map;
	}

	public Map<Path, List<FilePath>> parse(URI dirName) {
		Path dirPath = Paths.get(dirName);
		long startTime = System.currentTimeMillis();

		parsePath(dirPath);
		long endTime = System.currentTimeMillis();
		LOG.info(cpt + " files parsed in " + (endTime - startTime) + " ms");
		return map;
	}

	private void parsePath(Path dir) {

		DirectoryStream<Path> stream = null;

		try {

			stream = Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {

				@Override
				public boolean accept(Path entry) {

                    return !"@eaDir".equals(entry.getFileName().toString());
                }
			});
			for (Path path : stream) {
				LOG.trace("parsedir" + path.getFileName());

				if (Files.isDirectory(path)) {

					parsePath(path);

				} else {
					traiterFichier(path);
					cpt++;
				}
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	// private void traiterFichier(Path path) {
	// List<FilePath> l = map.get(path.getParent());
	// File f =new File(path. toUri());
	// if (f.getName().endsWith("JPG")){
	//
	// }
	// if (null == map.get(path.getParent())) {
	// l = new ArrayList<FilePath>();
	// map.put(path.getParent(), l);
	// }
	// l.addTrack(new FilePath(new File(path. toUri()), path));
	//
	// }

	private void traiterFichier(Path path) {
		List<FilePath> l = map.get(path.getParent());
		if (null == map.get(path.getParent())) {
			l = new ArrayList<>();
			map.put(path.getParent(), l);
		}
		l.add(new FilePath(new File(path.toUri()), path));
		// System.out.println(path.getFileName());

	}

	private static void testURL(File urlFile) throws MalformedURLException, URISyntaxException {

		String urlString = urlFile.toURI().toString();//
		URL url = new URL(urlString);
		System.out.println("URL is: " + url.toString());

		URI uri = url.toURI();
		System.out.println("URI is: " + uri.toString());

		if (uri.getAuthority() != null && uri.getAuthority().length() > 0) {
			// Hack for UNC Path
			uri = (new URL("file://" + urlString.substring("file:".length()))).toURI();
		}

		File file = new File(uri);
		System.out.println("File is: " + file.toString());

		String parent = file.getParent();
		System.out.println("Parent is: " + parent);

		System.out.println("____________________________________________________________");
	}
}
