package diskong.parser.fileutils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diskong.MassFlac;
import diskong.parser.DirectoryParser;

public class NioDirectoryParser implements DirectoryParser {

	final static Logger LOG = LoggerFactory.getLogger(MassFlac.class);

	static int cpt = 0;

	Map<Path, List<FilePath>> map = new HashMap<Path, List<FilePath>>();

	public static void main(String[] args) throws MalformedURLException, URISyntaxException {
		NioDirectoryParser dp = new NioDirectoryParser();

		URI uri = new File(args[0]).toURI();//
		// testURL(new File(args[0]));
		Path dir = FileSystems.getDefault().getPath(args[0]);
		long startTime = System.currentTimeMillis();

		dp.parse(uri);
		long endTime = System.currentTimeMillis();
		System.out.println("nb fichiers " + cpt);
		System.out.println("temps " + (endTime - startTime) / 1000);

	}

	public Map<Path, List<FilePath>> parse(String dirName) {
		Path dirPath = FileSystems.getDefault().getPath(dirName);
		long startTime = System.currentTimeMillis();

		LOG.debug("parsing..." + dirName);
		parsePath(dirPath);
		long endTime = System.currentTimeMillis();
		System.out.println(cpt + " files parsed in " + (endTime - startTime) + " ms");
		return map;
	}

	public Map<Path, List<FilePath>> parse(URI dirName) {
		Path dirPath = Paths.get(dirName);
		long startTime = System.currentTimeMillis();

		parsePath(dirPath);
		long endTime = System.currentTimeMillis();
		System.out.println(cpt + " files parsed in " + (endTime - startTime) + " ms");
		return map;
	}

	private void parsePath(Path dir) {

		DirectoryStream<Path> stream;

		try {

			stream = Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {

				@Override
				public boolean accept(Path entry) throws IOException {

					if ("@eaDir".equals(entry.getFileName().toString())) {
						return false;
					}
					return true;
				}
			});
			for (Path path : stream) {
				System.out.println("parsedir" + path.getFileName());
				if (Files.isDirectory(path)) {
					
					parsePath(path);

				} else {
					traiterFichier(path);
					cpt++;
				}
			}
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	// l.add(new FilePath(new File(path. toUri()), path));
	// // System.out.println(path.getFileName());
	//
	// }

	private void traiterFichier(Path path) {
		List<FilePath> l = map.get(path.getParent());
		if (null == map.get(path.getParent())) {
			l = new ArrayList<FilePath>();
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
