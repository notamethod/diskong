package diskong;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.tika.metadata.XMPDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diskong.api.DatabaseSearch;
import diskong.api.DatabaseSearchFactory;
import diskong.api.SearchAPI;
import diskong.parser.AudioParser;
import diskong.parser.DirectoryParser;
import diskong.parser.MetaUtils;
import diskong.parser.MyCallable;
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

	final static Logger LOG = LoggerFactory.getLogger(MassFlac.class);

	public static void main(String[] args) throws URISyntaxException {
		MassFlac mf = new MassFlac();

		if (args == null || args.length < 1) {
			// mf.massTag(new
			// File("/mnt/media1/music/Weezer/test"));//mnt/media1/music/Amen/Death
			// Before Musick"));

			mf.massTag(new File("/mnt/media1/music/Soundtrack/"));
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
			long startTime = System.currentTimeMillis();

		
			LOG.debug("iteration");
			diskong.parser.AudioParser ap = new AudioParser();
			AlbumVo album = AlbumFactory.getAlbum();
			try {
				// parsedir
				LOG.debug("iteration");

				album.setState(TagState.TOTAG);
				LOG.info("**************START******************************");
				ExecutorService executor = Executors.newFixedThreadPool(10);
				List<Future<TrackInfo>> list = new ArrayList<Future<TrackInfo>>();
				for (FilePath fPath : entry.getValue()) {
					LOG.debug(fPath.getFile().getAbsolutePath());
					 Callable<TrackInfo> worker = new MyCallable(fPath);
				      Future<TrackInfo> submit = executor.submit(worker);
				      list.add(submit);
				}
				
				for (Future<TrackInfo> future : list) {
				      try {
				    	  album.add(future.get()); // (metafile)
				      } catch (InterruptedException e) {
				        e.printStackTrace();
				      } catch (ExecutionException e) {
				        e.printStackTrace();
				      } catch (WrongTrackAlbumException e) {
							// put track in right album
							AlbumFactory.orderingTrack(e.getTrack());
						}
						// Content-Type=audio/x-flac
//						catch (WrongTrackArtistException e) {
//							// TODO Auto-generated catch block
//							LOG.error("wrong artist, various artists not implemented...skipping...");
//							album.setState(TagState.VARIOUS);
//							break;
//						}
				    }
				    System.out.println("xx");
				    executor.shutdown();
					
					
				
				if (album.getTracks().isEmpty())
					album.setState(TagState.NOTRACKS);
				else
					LOG.info("fin parcours répertoire, infos album:" + album.toString() + album.getTracks().size()
							+ " pistes " + album.getState());

				if (checkAction(album)) {
					IAlbumVo alInfos = searchAlbum(album);
					actionOnAlbum(album, alInfos);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			long endTime = System.currentTimeMillis();
			LOG.info(" files metaparsed in " + (endTime - startTime) + " ms");
		}

	}

	/**
	 * @param album
	 */
	private boolean checkAction(AlbumVo album) {

		boolean forceRetag = false;
		// if (forceRetag){
		// return ;
		// }
		boolean isOK = isAllOK(album);
		if (!album.getState().equals(TagState.TOTAG) || (!forceRetag && isOK)) {
			LOG.warn(
					"album " + album.getTitle() + " not parsed: state:" + album.getState() + " all tags found:" + isOK);

			return false;
		}
		return true;
	}

	private IAlbumVo searchAlbum(AlbumVo album) {

		int tagged = 0;
		IAlbumVo alInfos = null;
		LOG.info("Connecting to database:" + SearchAPI.DISCOGS);
		DatabaseSearch ds = DatabaseSearchFactory.getApi(SearchAPI.DISCOGS);
		try {
			alInfos = ds.searchRelease(album);
			LOG.debug(alInfos.getStyle() + " " + alInfos.getGenre());

		} catch (ReleaseNotFoundException e) {
			String title = regexx(album.getTitle(), "cd\\d*\\s");
			if (title != null && !title.equals("") && title != album.getTitle()) {
				album.setExactMatch(false);
				album.setTitle(title);
				return searchAlbum(album);
			} else {
				title = regexx(album.getTitle(), "\\((.+)\\)");
				if (title != null && !title.equals("") && title != album.getTitle()) {
					album.setExactMatch(false);
					album.setTitle(title);
					return searchAlbum(album);
				}
			}

			return null;
		}
		return alInfos;

	}

	private String regexx(String title, String regex) {
		return title.replaceAll(regex, "");
	}

	/**
	 * @param album
	 */
	private void actionOnAlbum(AlbumVo album, IAlbumVo alInfos) {

		int tagged = 0;
		if (alInfos == null) {
			LOG.warn("NO DATA");
			return;
		}
		if (!album.isExactMatch()) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Not exact match :release found: " + alInfos.getTitle() + ", " + alInfos.getArtist()
					+ " for album " + album.getTitle() + ", " + album.getArtist());
			System.out.println("retag with this ? (O/N)");
			String str = sc.nextLine();
			if (!str.equals("O")) {
				return;
			}
		}

		LOG.debug(alInfos.getStyle() + " " + alInfos.getGenre());
		LOG.debug("iteration");
		for (TrackInfo track : album.getTracks()) {
			MetaUtils.setGenre(alInfos, track.getMetadata());
			MetaUtils.setStyle(alInfos, track.getMetadata());
			if (retag(track) == 0)
				tagged++;
		}

		LOG.info("*** TAGGED TRACK ***" + tagged);
	}

	private boolean isAllOK(IAlbumVo iAlbum) {
		String preferences = "genre_exists+style_exists";
		LOG.debug("iteration");
		for (TrackInfo track : iAlbum.getTracks()) {
			if (MetaUtils.getGenre(track.getMetadata()).isEmpty()) {
				return false;
			}
			if (MetaUtils.getStyle(track.getMetadata()).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private int retag(List<TrackInfo> tracks) {

		// metaflac --show-tag=style /mnt/media1/music/Amen/Death\ Before\
		// Musick/01.\ Liberation\ For....flac
		// metaflac --set-tag=GENRE=Rock /mnt/media1/music/Weezer/Everything\
		// Will\ Be\ Alright\ in\ the\ End/01.\ Ain’t\ Got\ Nobody.flac
		// $ metaflac --show-tag=genre /mnt/media1/music/Weezer/Everything\
		// Will\ Be\ Alright\ in\ the\ End/01.\ Ain’t\ Got\ Nobody.flac
		//

		return 0;
	}

	private int retag(TrackInfo track) {

		int exitCode = 0;
		Arguments args = new Arguments();
		args.add(ArgAction.REMOVE_TAG, MetaUtils.STYLE);
		args.add(ArgAction.REMOVE_TAG, XMPDM.GENRE);
		// metaflac --remove-tag=genre --set-tag=GENRE=Rruick
		// --set-tag=GENRE=Rack /mnt/media1/music/Weezer/test/01.\ Ain’t\ Got\
		// Nobody.flac
		for (String genre : MetaUtils.getGenre(track.getMetadata())) {
			args.add(ArgAction.SET_TAG, XMPDM.GENRE, genre);

		}
		for (String style : MetaUtils.getStyle(track.getMetadata())) {
			args.add(ArgAction.SET_TAG, MetaUtils.STYLE, style);
			args.add(ArgAction.SET_TAG, XMPDM.GENRE, style);
		}
		args.getList().add(0, "metaflac");
		args.getList().add(track.getfPath().getFile().getAbsolutePath());
		ProcessBuilder pb = new ProcessBuilder(args.getList());
		try {
			Process p = pb.start();
			if (!p.waitFor(30, TimeUnit.SECONDS)) {
				exitCode = 88;
			} else {
				exitCode = p.exitValue();
			}
			if (exitCode != 0) {
				BufferedReader output = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String ligne;
				while ((ligne = output.readLine()) != null) {
					System.out.println(ligne);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exitCode = 99;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exitCode = 99;
		}

		// Map<String, String> env = pb.environment();
		// env.put("VAR1", "myValue");
		// env.remove("OTHERVAR");
		// env.put("VAR2", env.get("VAR1") + "suffix");
		// pb.directory("myDir");

		// metaflac --show-tag=style /mnt/media1/music/Amen/Death\ Before\
		// Musick/01.\ Liberation\ For....flac
		// metaflac --set-tag=GENRE=Rock /mnt/media1/music/Weezer/Everything\
		// Will\ Be\ Alright\ in\ the\ End/01.\ Ain’t\ Got\ Nobody.flac
		// $ metaflac --show-tag=genre /mnt/media1/music/Weezer/Everything\
		// Will\ Be\ Alright\ in\ the\ End/01.\ Ain’t\ Got\ Nobody.flac
		//

		return exitCode;
	}
}
