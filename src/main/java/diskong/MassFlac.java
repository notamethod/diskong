package diskong;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.tika.metadata.XMPDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import diskong.parser.AudioParser;
import diskong.parser.DirectoryParser;
import diskong.parser.MetaUtils;
import diskong.parser.NioDirectoryParser;
import diskong.parser.fileutils.FilePath;

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
			mf.massTag(new File("/mnt/media1/music/Weezer/test"));//mnt/media1/music/Amen/Death Before Musick"));

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
			System.out.println("iteration");
			diskong.parser.AudioParser ap = new AudioParser();
			AlbumVo album = AlbumFactory.getAlbum();
			try {
				// parsedir
				System.out.println("iteration");
				for (FilePath fPath : entry.getValue()) {

					try {
						album.add(fPath, ap.parse(fPath)); // (metafile)
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
			
				actionOnAlbum(album);
			} catch (IOException | SAXException | TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void actionOnAlbum(IAlbumVo album) {
		
		
		boolean forceRetag = true;
		
//		if (!forceRetag || !isAllOK(album)){
//			System.out.println("OK");
//			return;
//		}
		System.out.println("Connecting to database:" + SearchAPI.DISCOGS);
		DatabaseSearch ds = DatabaseSearchFactory.getApi(SearchAPI.DISCOGS);
		IAlbumVo alInfos= ds.searchRelease(album);
		System.out.println(alInfos.getStyle()+ " "+alInfos.getGenre());
		System.out.println("iteration");
		for (TrackInfo track:album.getTracks()){
			MetaUtils.setGenre(alInfos, track.getMetadata());
			MetaUtils.setStyle(alInfos, track.getMetadata());
			retag(track);
		}
		
	}

	private boolean isAllOK(IAlbumVo iAlbum) {
		String preferences="genre_exists+style_exists";
		System.out.println("iteration");
		for (TrackInfo track:iAlbum.getTracks()){
			if (MetaUtils.getGenre(track.getMetadata()).isEmpty()){
				return false;
			}
		}
		return true;
	}
	
	private int retag(List<TrackInfo> tracks){
		
//	    metaflac --show-tag=style /mnt/media1/music/Amen/Death\ Before\ Musick/01.\ Liberation\ For....flac
//	    		metaflac --set-tag=GENRE=Rock /mnt/media1/music/Weezer/Everything\ Will\ Be\ Alright\ in\ the\ End/01.\ Ain’t\ Got\ Nobody.flac 
//	    		 $ metaflac --show-tag=genre /mnt/media1/music/Weezer/Everything\ Will\ Be\ Alright\ in\ the\ End/01.\ Ain’t\ Got\ Nobody.flac 
//  		
	    		 
		return 0;
	}
	private int retag(TrackInfo track){
	
		Arguments args = new Arguments();
		args.add(ArgAction.REMOVE_TAG, MetaUtils.STYLE);
		args.add(ArgAction.REMOVE_TAG, XMPDM.GENRE);
		//metaflac --remove-tag=genre --set-tag=GENRE=Rruick --set-tag=GENRE=Rack /mnt/media1/music/Weezer/test/01.\ Ain’t\ Got\ Nobody.flac
		for (String genre:MetaUtils.getGenre(track.getMetadata())){
			args.add(ArgAction.SET_TAG, XMPDM.GENRE, genre);
		}
		for (String style:MetaUtils.getStyle(track.getMetadata())){
			args.add(ArgAction.SET_TAG, MetaUtils.STYLE, style);
		}
		args.getList().add(0, "metaflac");
		args.getList().add(track.getfPath().getFile().getAbsolutePath());
		 ProcessBuilder pb = new ProcessBuilder(args.getList());
		 try {
			Process p = pb.start();
			BufferedReader output = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String ligne;
			while ((ligne = output.readLine()) != null) {
				 System.out.println(ligne);
				}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
//		 Map<String, String> env = pb.environment();
//		 env.put("VAR1", "myValue");
//		 env.remove("OTHERVAR");
//		 env.put("VAR2", env.get("VAR1") + "suffix");
//		 pb.directory("myDir");
		
	
//	    metaflac --show-tag=style /mnt/media1/music/Amen/Death\ Before\ Musick/01.\ Liberation\ For....flac
//	    		metaflac --set-tag=GENRE=Rock /mnt/media1/music/Weezer/Everything\ Will\ Be\ Alright\ in\ the\ End/01.\ Ain’t\ Got\ Nobody.flac 
//	    		 $ metaflac --show-tag=genre /mnt/media1/music/Weezer/Everything\ Will\ Be\ Alright\ in\ the\ End/01.\ Ain’t\ Got\ Nobody.flac 
//  		
	    		 
		return 0;
	}
}
