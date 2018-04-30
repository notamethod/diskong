package diskong.services;

import diskong.api.ApiConfigurationException;
import diskong.api.DatabaseSearch;
import diskong.api.DatabaseSearchFactory;
import diskong.api.SearchAPI;
import diskong.parser.MetaUtils;
import org.apache.tika.metadata.XMPDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AlbumService {
    final static Logger LOG = LoggerFactory.getLogger(AlbumService.class);
    private static final String UNKNOWN = "Unknown";
    boolean IsSimulate = true;
    private SearchAPI searchAPI;

    public SearchAPI getSearchAPI() {
        return searchAPI;
    }

    public void setSearchAPI(SearchAPI searchAPI) {
        this.searchAPI = searchAPI;
    }

    public IAlbumVo searchAlbum(AlbumVo album) throws ApiConfigurationException {


        int tagged = 0;
        IAlbumVo alInfos = null;
        LOG.info("Connecting to database:" + SearchAPI.DISCOGS);
        this.searchAPI = SearchAPI.DISCOGS;
        DatabaseSearch ds = DatabaseSearchFactory.getApi(SearchAPI.DISCOGS);
        try {
            alInfos = ds.searchRelease(album);
            if (alInfos.getStyles().isEmpty())
                alInfos.setStyle(UNKNOWN);
            LOG.debug(alInfos.getStyle() + " " + alInfos.getGenre());

        } catch (ReleaseNotFoundException e) {
            String artist = TrackUtils.regexx(album.getArtist(), "(\\((.+)\\))|(!)|(&)");
            if (artist != null && !artist.equals("") && !artist.equals(album.getArtist())) {
                album.setExactMatch(false);
                album.setArtist(artist);
                return searchAlbum(album);
            }

            String title = TrackUtils.regexx(album.getTitle(), "(cd\\d*\\s)|(!)|(&)|(:)|(,)|(Disc\\s\\d*)");
            if (title != null && !title.equals("") && !title.equals(album.getTitle())) {
                album.setExactMatch(false);
                album.setTitle(title);
                return searchAlbum(album);
            } else {
                title = TrackUtils.regexx(album.getTitle(), "\\((.+)\\)");
                if (title != null && !title.equals("") && !title.equals(album.getTitle())) {
                    album.setExactMatch(false);
                    album.setTitle(title);
                    return searchAlbum(album);
                }
            }
            if (artist != null && artist.equals("Various")) {
                album.setExactMatch(false);
                album.setArtist(null);
                return searchAlbum(album);
            }


            return null;
        }
        return alInfos;

    }

    /**
     * @param album
     */
    int actionOnAlbum(AlbumVo album, IAlbumVo alInfos) {

        int tagged = 0;
        if (alInfos == null) {
            LOG.warn("NO DATA");
            return 0;
        }
        if (!album.isExactMatch()) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Not exact match for " + album.getTitle() + "/" + album.getArtist() + " - release found: " + alInfos.getTitle() + ", " + alInfos.getArtist());
            System.out.println("retag with this ? (O/N)");
            String str = sc.nextLine();
            if (!str.equals("O")) {
                return 0;
            }
        }

        LOG.debug(alInfos.getStyle() + " " + alInfos.getGenre());
        LOG.debug("iteration inutile");
        for (TrackInfo track : album.getTracks()) {
            MetaUtils.setGenre(alInfos, track.getMetadata());
            MetaUtils.setStyle(alInfos, track.getMetadata());
            if (retag(track) == 0)
                tagged++;
        }

        LOG.info("*** TAGGED TRACK ***" + tagged);
        return tagged;
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
