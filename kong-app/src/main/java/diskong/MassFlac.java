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

package diskong;

import diskong.api.ApiConfigurationException;
import diskong.api.DatabaseSearch;
import diskong.api.DatabaseSearchFactory;
import diskong.api.SearchAPI;
import diskong.core.*;
import diskong.core.TagState;
import diskong.parser.*;
import diskong.parser.MetaUtils;
import diskong.services.AudioService;
import diskong.tag.metatag.ArgAction;
import diskong.tag.metatag.Arguments;
import org.apache.tika.metadata.XMPDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;


//
/**
 * Gestion des r�pertoires
 * 
 * @author buck
 *
 */
class MassFlac {

	private final static Logger LOG = LoggerFactory.getLogger(MassFlac.class);
	private static final String UNKNOWN = "Unknown";
	private int checkTagged;
	private int taggedTrack;
	private static int NBCHECK = 200;
	private boolean IsSimulate=true;
	private AudioService audioService = null;

	public MassFlac() throws Exception {
		super();
		audioService = new AudioService();
	}

	public static void main(String[] args) throws Exception {
		MassFlac mf = new MassFlac();

		if (args == null || args.length < 1) {
			 mf.massTag(new
			 File("/media/syno/music/R.E.M" /**"/media/syno/music/Weezer/test"**/));
		} else {
			mf.massTag(new File(args[0]));
			// String path=args[0];
			// path=path.replace("\\", "/");
			// mf.massTag(new File(new URI("file:"+path)));
		}
	}

	private void massTag(File file) {
		checkTagged = 0;
		taggedTrack = 0;
		DirectoryParser dirParser = new NioDirectoryParser();
		Map<Path, List<FilePath>> map = null;
		try {
			map = dirParser.parse(file);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File not found: " + e.getLocalizedMessage(), "error", JOptionPane.ERROR_MESSAGE);

		}
		traiterDir(map);
		System.out.println("END...tagged tracks:" + taggedTrack);
	}

	private void traiterDir(Map<Path, List<FilePath>> map) {
		for (Entry<Path, List<FilePath>> entry : map.entrySet()) {
			long startTime = System.currentTimeMillis();

			//continue or stop asked every NBCHECK parsed files
			if (checkTagged >= NBCHECK) {
				if (contineParse()) {
					checkTagged = 0;
				} else {
					break;
				}
			}

			diskong.core.AlbumVo album = AlbumFactory.getAlbum();
			try {
				// parsedir
				LOG.debug("iteration");

				album.setState(diskong.core.TagState.TOTAG);
				LOG.debug("**************START******************************");
				ExecutorService executor = Executors.newFixedThreadPool(10);
				List<Future<diskong.core.TrackInfo>> list = new ArrayList<>();
				for (FilePath fPath : entry.getValue()) {
					LOG.debug(fPath.getFile().getAbsolutePath());
					Callable<diskong.core.TrackInfo> worker = new CallTrackInfo(fPath);
					Future<diskong.core.TrackInfo> submit = executor.submit(worker);
					list.add(submit);
				}

				for (Future<diskong.core.TrackInfo> future : list) {
					try {
						diskong.core.TrackInfo tinf = future.get();
						audioService.addTrack(album, tinf);// (metafile)
					} catch (InterruptedException |ExecutionException e) {
						e.printStackTrace();
					} catch (WrongTrackAlbumException e) {
						// put track in right album
						AlbumFactory.orderingTrack(e.getTrack());
					}
		
				}
				executor.shutdown();

				if (album.getTracks().isEmpty())
					album.setState(diskong.core.TagState.NOTRACKS);
				else
					LOG.info("Album parsed:" + album.toString() + album.getTracks().size());
				Statistics.getInstance().addStats(album);
				if (checkAction(album)) {
					IAlbumVo alInfos = searchAlbum(album);
					checkTagged += actionOnAlbum(album, alInfos);
					taggedTrack += checkTagged;
				}

//				1 <
//						{"pagination": {"per_page": 50, "items": 1, "page": 1, "urls": {}, "pages": 1}, "results": [{"style": ["Alternative Rock", "Power Pop"], "thumb": "https://img.discogs.com/h--kMjRx67LHlr9H2DCiaGhQ6zc=/fit-in/150x150/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/R-6167017-1412736726-5831.jpeg.jpg", "format": ["CD", "Album"], "country": "US", "barcode": ["602537990726"], "uri": "/Weezer-Everything-Will-Be-Alright-In-The-End/master/741903", "community": {"have": 2744, "want": 712}, "label": ["Republic Records", "Republic Records", "Republic Records", "The Village Recorder", "South Beach Studios", "Sterling Sound"], "cover_image": "https://img.discogs.com/gpekxrOlT4AVW9-ykuOKsNQwpEQ=/fit-in/500x500/filters:strip_icc():format(jpeg):mode_rgb():quality(90)/discogs-images/R-6167017-1412736726-5831.jpeg.jpg", "catno": "B0021619-02", "year": "2014", "genre": ["Rock"], "title": "Weezer - Everything Will Be Alright In The End", "resource_url": "https://api.discogs.com/masters/741903", "type": "master", "id": 741903}]}
//				22:58:47.641 [main] DEBUG discogs.DiscogSearch - {"pagination":{"per_page":50,"items":1,"page":1,"urls":{},"pages":1},"results":[{"style":["Alternative Rock","Power Pop"],"thumb":"https:\/\/img.discogs.com\/h--kMjRx67LHlr9H2DCiaGhQ6zc=\/fit-in\/150x150\/filters:strip_icc():format(jpeg):mode_rgb():quality(40)\/discogs-images\/R-6167017-1412736726-5831.jpeg.jpg","format":["CD","Album"],"country":"US","barcode":["602537990726"],"uri":"\/Weezer-Everything-Will-Be-Alright-In-The-End\/master\/741903","community":{"have":2744,"want":712},"label":["Republic Records","Republic Records","Republic Records","The Village Recorder","South Beach Studios","Sterling Sound"],"cover_image":"https:\/\/img.discogs.com\/gpekxrOlT4AVW9-ykuOKsNQwpEQ=\/fit-in\/500x500\/filters:strip_icc():format(jpeg):mode_rgb():quality(90)\/discogs-images\/R-6167017-1412736726-5831.jpeg.jpg","catno":"B0021619-02","year":"2014","genre":["Rock"],"title":"Weezer - Everything Will Be Alright In The End","resource_url":"https:\/\/diskong.api.discogs.com\/masters\/741903","type":"master","id":741903}]}
//				22:58:47.649 [main] DEBUG diskong.MassFlac - Alternative Rock Rock


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			long endTime = System.currentTimeMillis();
			LOG.debug(" files metaparsed in " + (endTime - startTime) + " ms");
		}

	}

	private boolean contineParse() {
		Scanner sc = new Scanner(System.in);
		System.out.println(NBCHECK + " or more tracks tagged ");
		System.out.println("continue ? (O/N)");
		String str = sc.nextLine();
		return str.equals("O");
	}

	/**
	 * @param album
	 */
	private boolean checkAction(diskong.core.AlbumVo album) {

		boolean forceRetag = false;
		// if (forceRetag){
		// return ;
		// }
		boolean isOK = isAllOK(album);
		if (album.getState().equals(diskong.core.TagState.TOTAG) && !forceRetag && isOK) {
			LOG.debug(
					"album " + album.getTitle() + " not parsed: state:" + album.getState() + " all tags found:" + isOK);

			return false;
		}
		if (!album.getState().equals(TagState.TOTAG) || (!forceRetag && isOK)) {
			LOG.warn(
					"album " + album.getTitle() + " not parsed: state:" + album.getState() + " all tags found:" + isOK);

			return false;
		}

		return true;
	}

	private IAlbumVo searchAlbum(diskong.core.AlbumVo album) throws ApiConfigurationException {


		int tagged = 0;
		IAlbumVo alInfos = null;
		LOG.info("Connecting to database:" + SearchAPI.DISCOGS);
		DatabaseSearch ds = DatabaseSearchFactory.getApi(SearchAPI.DISCOGS);
		try {
			alInfos = ds.searchRelease(album);
			if (alInfos.getStyles().isEmpty())
				alInfos.setStyle(UNKNOWN);
			LOG.debug(alInfos.getStyle() + " " + alInfos.getGenre());

		} catch (EmptyResultException e) {
			String artist = regexx(album.getArtist(), "(\\((.+)\\))|(!)|(&)");
			if (artist != null && !artist.equals("") && !Objects.equals(artist, album.getArtist())) {
				album.setExactMatch(false);
				album.setArtist(artist);
				return searchAlbum(album);
			}

			String title = regexx(album.getTitle(), "(cd\\d*\\s)|(!)|(&)|(:)|(,)|(Disc\\s\\d*)");
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
			if (artist != null && artist.equals("Various")){
				album.setExactMatch(false);
				album.setArtist(null);
				return searchAlbum(album);
			}
			

			return null;
		}
		return alInfos;

	}

	private String regexx(String string, String regex) {
		if (string==null)
			return null;
		return string.replaceAll(regex, "").trim();
	}

	/**
	 * @param album
	 */
	private int actionOnAlbum(AlbumVo album, IAlbumVo alInfos) {

		int tagged = 0;
		if (alInfos == null) {
			LOG.warn("NO DATA");
			return 0;
		}
		if (!album.isExactMatch()) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Not exact match for "+album.getTitle()+"/"+album.getArtist()+" - release found: " + alInfos.getTitle() + ", " + alInfos.getArtist());
			System.out.println("retag with this ? (O/N)");
			String str = sc.nextLine();
			if (!str.equals("O")) {
				return 0;
			}
		}

		LOG.debug(alInfos.getStyle() + " " + alInfos.getGenre());
		LOG.debug("iteration inutile");
		for (diskong.core.TrackInfo track : album.getTracks()) {
			MetaUtils.setGenre(alInfos, track.getMetadata());
			MetaUtils.setStyle(alInfos, track.getMetadata());
			if (retag(track) == 0)
				tagged++;
		}

		LOG.info("*** TAGGED TRACK ***" + tagged);
		return tagged;
	}

	private boolean isAllOK(IAlbumVo iAlbum) {
		String preferences = "genre_exists+style_exists";
		LOG.debug("iteration");
		for (diskong.core.TrackInfo track : iAlbum.getTracks()) {
			if (MetaUtils.getGenre(track.getMetadata()).isEmpty()) {
				return false;
			}
			if (MetaUtils.getStyle(track.getMetadata()).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private int retag(List<diskong.core.TrackInfo> tracks) {

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

		if (IsSimulate)
			return 0;
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

		} catch (IOException | InterruptedException e) {
			LOG.error("retag error", e);
			exitCode = 99;
		}

		return exitCode;
	}
}
