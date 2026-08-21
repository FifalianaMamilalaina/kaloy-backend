package org.example.mozika.services.interfaces;

import org.example.mozika.models.Album;
import org.example.mozika.models.Song;

import org.example.mozika.models.dto.AlbumSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface AlbumService {
    Page<Album> getAllAlbum(Pageable pageable);

    Page<Album> getAllAlbum(Pageable pageable, AlbumSearch object);

    Album getAlbumById(Long id);

    public String exportAlbumToCSV(List<Album> album);

    

    Album createAlbum(Album album);

    Album updateAlbum(Long id, Album album);

    void deleteAlbum(Long id);
    

    Album createFullAlbum(Album album,List<Song> songs);
    Album updateFullAlbum(Long id, Album album,List<Song> songs);
    void deleteFullAlbum(Long id);
}
