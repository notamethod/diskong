package diskong;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import diskong.parser.AudioParser;
import diskong.parser.DirectoryParser;

import diskong.parser.fileutils.FilePath;
import diskong.parser.fileutils.NioDirectoryParser;

//
/**
 * Gestion des r�pertoires
 * 
 * @author buck
 *
 */
public class MassFlac {

	final static Logger logger = LoggerFactory.getLogger(MassFlac.class);

	public static void main(String[] args) throws URISyntaxException {
		MassFlac mf = new MassFlac();
		if (args == null || args.length < 1) {
			mf.massTag(new File("/mnt/media1/music/Amen/Death Before Musick"));

		} else {
			mf.massTag(new File(args[0]));
			// String path=args[0];
			// path=path.replace("\\", "/");
			// mf.massTag(new File(new URI("file:"+path)));
		}
	}

	public void massTag(File file) {
		DirectoryParser dirParser = new NioDirectoryParser();
		Map<Path, List<FilePath>> map = dirParser.parse(file.getAbsolutePath());
		traiterDir(map);
	}

	private void traiterDir(Map<Path, List<FilePath>> map) {
		for (Entry<Path, List<FilePath>> entry : map.entrySet()) {

			diskong.parser.AudioParser ap = new AudioParser();
			AlbumVo album = AlbumFactory.getAlbum();
			try {
				// parsedir
				for (FilePath fPath : entry.getValue()) {

					try {
						album.add(ap.parse(fPath)); // (metafile)
					} catch (WrongTrackAlbumException e) {
						// put track in right album
						AlbumFactory.orderingTrack(e.getTrack());
					}
					// Content-Type=audio/x-flac
					catch (WrongTrackArtistException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("fin parcours répertoire, infos album:");
				System.out.println(album.toString() + album.getTracks().size() + " pistes");
				System.out.println("Connecting to database:" + SearchAPI.DISCOGS);
				DatabaseSearch ds = DatabaseSearchFactory.getApi(SearchAPI.DISCOGS);
				IAlbumVo alInfos= ds.searchRelease(album);
				System.out.println(alInfos.getStyle()+ " "+alInfos.getGenre());
			} catch (IOException | SAXException | TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
