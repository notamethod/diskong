/*
 * Copyright 2019 org.dpr & croger
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

package diskong.app.services;

import diskong.app.data.album.AlbumEntity;
import diskong.app.data.album.AlbumRepository;
import diskong.app.common.SimpleStatObject;
import diskong.app.data.genre.GenreEntity;
import diskong.app.data.genre.GenreRepository;
import diskong.app.data.track.TrackEntity;
import diskong.app.data.track.TrackRepository;
import diskong.core.bean.AlbumVo;
import diskong.core.bean.IAlbumVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Override
    public List<TrackEntity> findAll() {
        return trackRepository.findAll();
    }

    @Override
    public TrackEntity findOne(Long id) {
        return null;
    }

    @Override
    public TrackEntity create(TrackEntity track) {
        return trackRepository.save(track);
    }

    @Override
    public AlbumEntity createAlbum(AlbumEntity album) {
        AlbumEntity entity = albumRepository.save(album);
        return entity;
    }

    @Override
    public List<SimpleStatObject> findArtistCount() {
        return trackRepository.findArtistCount();
    }

    @Override
    public List<SimpleStatObject> findAlbumCount() {
        return trackRepository.findAlbumCount();
    }

    @Override
    public List<TrackEntity> findTrackByArtist(String label) {
        return trackRepository.findByArtistAlbum(label);
    }
    @Override
    public List<TrackEntity> findTrackByAlbum(String label) {
        return trackRepository.findByAlbum(label);
    }

    @Override
    public List<TrackEntity> findTrackByGenre(String label) {
        return trackRepository.findByGenre(label);
    }

    @Override
    public List<SimpleStatObject> findGenreCount() {
        return genreRepository.findGenreCount();
    }

    @Override
    public GenreEntity findOrSaveGenre(String name) {
        try {
            GenreEntity genre = genreRepository.findByName(name);
            if (genre!=null)
                return genre;
            else{
                genre = new GenreEntity(name);
                genreRepository.save(genre);
                return genre;
            }

        } catch (EmptyResultDataAccessException e) {
            GenreEntity genre = new GenreEntity(name);
            genreRepository.save(genre);
            return genre;
        }
    }

    @Override
    public AlbumEntity createAlbum(AlbumVo album) {
        AlbumEntity entity = domainToEntity(album);
        if (!album.getGenres().isEmpty()){
            List<GenreEntity> entities = new ArrayList();

            for (String genre : album.getGenres()){
                entities.add(findOrSaveGenre(genre));
            }
            entity.setGenres(entities);
        }
        albumRepository.save(entity);
        return entity;
    }

    private AlbumEntity domainToEntity(AlbumVo album) {
        AlbumEntity entity = new AlbumEntity();
        entity.setArtist(album.getArtist());
        entity.setTitle(album.getTitle());
        if (!album.getYear().isEmpty())
            entity.setYear(Integer.parseInt(album.getYear()));
        return entity;
    }

    public IAlbumVo entityToDomain(AlbumEntity entity) {
        IAlbumVo album = new AlbumVo();
        album.setArtist(entity.getArtist());
        album.setTitle(entity.getTitle());
        return album;
    }

    @Override
    public long countTrack() {
        return trackRepository.count();
    }

    @Override
    public void saveAlbum(AlbumEntity entity) {
        albumRepository.save(entity);

    }
}
