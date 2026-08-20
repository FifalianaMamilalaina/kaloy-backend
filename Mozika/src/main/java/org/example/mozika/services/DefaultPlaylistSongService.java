package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.PlaylistSong;
import org.example.mozika.models.dto.PlaylistSongSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.PlaylistSongRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.PlaylistSongService;
import org.example.mozika.specification.PlaylistSongSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultPlaylistSongService implements PlaylistSongService {
	private final PlaylistSongRepository playlistSongRepository;

	public DefaultPlaylistSongService(PlaylistSongRepository playlistSongRepository) {
	   this.playlistSongRepository = playlistSongRepository;
	}

	@Override
	public String exportPlaylistSongToCSV(List<PlaylistSong> playlistSong) {
	   return ExportUtils.generateCsv(playlistSong);
	}  

	@Override
	public Page<PlaylistSong> getAllPlaylistSong(Pageable pageable) {
	    try {
	        return playlistSongRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving playlist song", ex);
	    }
	}

	@Override
	public Page<PlaylistSong> getAllPlaylistSong(Pageable pageable, PlaylistSongSearch object) {
	    try {
	        Specification<PlaylistSong> spec=PlaylistSongSpecification.filter(object);
	        return playlistSongRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving playlist song", ex);
	    }
	}

	@Override
	public PlaylistSong getPlaylistSongById(Long id) {
	    Optional<PlaylistSong> playlistSong = playlistSongRepository.findById(id);
	    if (playlistSong.isPresent()) {
	        return playlistSong.get();
	    } else {
	        throw new ResourceNotFoundException("PlaylistSong not found with id : " + id);
	    }
	}

	@Override
	public PlaylistSong createPlaylistSong(PlaylistSong playlistSong) {
	    try {
	        return playlistSongRepository.save(playlistSong);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating playlist song", ex);
	    }
	}

	@Override
	public PlaylistSong updatePlaylistSong(Long id, PlaylistSong playlistSong) {
	    Optional<PlaylistSong> existingPlaylistSong = playlistSongRepository.findById(id);
	    if (existingPlaylistSong.isPresent()) {
	        playlistSong.setId(id);
	        try {
	            return playlistSongRepository.save(playlistSong);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating playlist song", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("PlaylistSong not found with id : " + id);
	    }
	}

	@Override
	public void deletePlaylistSong(Long id) {
	    try {
	        playlistSongRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting playlist song", ex);
	    }
	}



}
