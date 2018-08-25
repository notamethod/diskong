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

package diskong.services;

import diskong.api.ApiConfigurationException;
import diskong.api.DatabaseSearch;
import diskong.api.DatabaseSearchFactory;
import diskong.api.SearchAPI;
import diskong.app.cdrip.RipperException;
import diskong.app.tagger.TaggerException;
import diskong.core.AlbumVo;
import diskong.core.IAlbumVo;
import diskong.core.EmptyResultException;
import diskong.core.TrackInfo;
import diskong.parser.MetaUtils;
import diskong.tag.metatag.ArgAction;
import diskong.tag.metatag.Arguments;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AlbumService {
    private final static Logger LOG = LoggerFactory.getLogger(AlbumService.class);
    private static final String UNKNOWN = "Unknown";
    private boolean isSimulate = true;
    private SearchAPI searchAPI;

    public void setSimulate(boolean simulate) {
        isSimulate = simulate;
    }

    public SearchAPI getSearchAPI() {
        return searchAPI;
    }

    public void setSearchAPI(SearchAPI searchAPI) {
        this.searchAPI = searchAPI;
    }

    public void searchAlbumByID(IAlbumVo album) throws ApiConfigurationException {


        int tagged = 0;
        IAlbumVo alInfos = null;
        LOG.info("Connecting to database:" + SearchAPI.DISCOGS);
        this.searchAPI = SearchAPI.DISCOGS;
        DatabaseSearch ds = DatabaseSearchFactory.getApi(SearchAPI.DISCOGS);

        if (album.getId() != null) {
            try {
                ds.getReleaseById(album);
            } catch (EmptyResultException e) {
                e.printStackTrace();
            }
        }
    }
    public IAlbumVo searchAlbum(IAlbumVo album) throws ApiConfigurationException {


        int tagged = 0;
        IAlbumVo alInfos = null;
        LOG.info("Connecting to database:" + SearchAPI.DISCOGS);
        this.searchAPI = SearchAPI.DISCOGS;
        DatabaseSearch ds = DatabaseSearchFactory.getApi(SearchAPI.DISCOGS);
        try {
            if (album.getId()!=null) {
                 ds.getReleaseById(album);
            }

            alInfos = ds.searchRelease(album);
            if (alInfos.getStyles().isEmpty())
                alInfos.setStyle(UNKNOWN);
            LOG.debug(alInfos.getStyle() + " " + alInfos.getGenre());
            if (null == alInfos.getArtist() || alInfos.getArtist().isEmpty())
                alInfos.setArtist(album.getArtist());
            if (null == alInfos.getArtist() || alInfos.getArtist().isEmpty() || !alInfos.getTitle().equals(album.getTitle()))
                alInfos.setTitle(album.getTitle());


        } catch (EmptyResultException e) {
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

    public List<IAlbumVo> searchArtist(String artist, String title) throws ApiConfigurationException, EmptyResultException {


        int tagged = 0;
        List<IAlbumVo> alInfos = null;
        LOG.info("Connecting to database:" + SearchAPI.DISCOGS);
        this.searchAPI = SearchAPI.DISCOGS;
        DatabaseSearch ds = DatabaseSearchFactory.getApi(SearchAPI.DISCOGS);

        alInfos = ds.searchArtist(artist, title);

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
        for (diskong.core.TrackInfo track : album.getTracks()) {
            MetaUtils.setGenre(alInfos, track.getMetadata());
            MetaUtils.setStyle(alInfos, track.getMetadata());
            if (retag(track) == 0)
                tagged++;
        }

        LOG.info("*** TAGGED TRACK ***" + tagged);
        return tagged;
    }


    private int retag(diskong.core.TrackInfo track) {

        if (isSimulate)
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

    private int retag2(Metadata metadata, diskong.core.TrackInfo track) throws TaggerException {

        if (isSimulate)
            return 0;


        int exitCode = 0;
        Arguments args = new Arguments();

        // metaflac --remove-tag=genre --set-tag=GENRE=Rruick
        // --set-tag=GENRE=Rack /mnt/media1/music/Weezer/test/01.\ Ain’t\ Got\
        // Nobody.flac
        Map<String, String> controlMap = new HashMap<>();
        for (String name : metadata.names()) {
            // ?   if (metadata.isMultiValued(name)){
            String cleanName = name.replaceAll("xmpDM:", "");
            args.add(ArgAction.REMOVE_TAG, cleanName);
            for (String value : metadata.getValues(name)) {

                args.add(ArgAction.SET_TAG, cleanName, value);
            }
            //       }
        }

//        for (String value : metadata.getValues(XMPDM.ARTIST)) {
//
//            args.add(ArgAction.SET_TAG, XMPDM.ARTIST, value);
//        }
//
//        for (String value : metadata.getValues(XMPDM.ALBUM)) {
//
//            args.add(ArgAction.SET_TAG, XMPDM.ALBUM, value);
//        }
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
            throw new TaggerException("retag error: "+e.getLocalizedMessage(), exitCode, e);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            exitCode = 98;
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
    private int retag2(IAlbumVo albumNew, diskong.core.TrackInfo track) throws TaggerException {

        if (isSimulate)
            return 0;


        int exitCode = 0;
        Arguments args = new Arguments();

        // metaflac --remove-tag=genre --set-tag=GENRE=Rruick
        // --set-tag=GENRE=Rack /mnt/media1/music/Weezer/test/01.\ Ain’t\ Got\
        // Nobody.flac
        int numTrack = Integer.parseInt(track.getNumber());
         args.add(ArgAction.REMOVE_TAG, TikaCoreProperties.TITLE);
        args.add(ArgAction.SET_TAG, TikaCoreProperties.TITLE, albumNew.getTracks().get(numTrack-1).getTitle());


//        for (String value : metadata.getValues(XMPDM.ARTIST)) {
//
//            args.add(ArgAction.SET_TAG, XMPDM.ARTIST, value);
//        }
//
//        for (String value : metadata.getValues(XMPDM.ALBUM)) {
//
//            args.add(ArgAction.SET_TAG, XMPDM.ALBUM, value);
//        }
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
            throw new TaggerException("retag error: "+e.getLocalizedMessage(), exitCode, e);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            exitCode = 98;
        }



        return exitCode;
    }
    /**
     * retag all tracks with metada infos.
     *
     * @param data
     * @param album
     * @return number of tagged tracks
     */
    public int retagAlbum(Metadata data, IAlbumVo album) throws TaggerException {
        int tagged = 0;
        if (data == null || data.size() < 1) {
            LOG.warn("NO DATA");
            return 0;
        }

        for (TrackInfo track : album.getTracks()) {

            if (retag2(data, track) == 0)
                tagged++;
        }

        LOG.info("*** TAGGED TRACK ***" + tagged);
        return tagged;
    }

    public int retagAlbum(IAlbumVo album, IAlbumVo albumNew) throws TaggerException {
        int tagged = 0;


        for (TrackInfo track : album.getTracks()) {

            if (retag2(albumNew, track) == 0)
                tagged++;
        }

        LOG.info("*** TAGGED TRACK ***" + tagged);
        return tagged;
    }
}
