package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.PlaylistVisibilitie;
import org.example.mozika.models.dto.PlaylistVisibilitieSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.PlaylistVisibilitieRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.PlaylistVisibilitieService;
import org.example.mozika.specification.PlaylistVisibilitieSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultPlaylistVisibilitieService implements PlaylistVisibilitieService {
	private final PlaylistVisibilitieRepository playlistVisibilitieRepository;

	public DefaultPlaylistVisibilitieService(PlaylistVisibilitieRepository playlistVisibilitieRepository) {
	   this.playlistVisibilitieRepository = playlistVisibilitieRepository;
	}

	@Override
	public String exportPlaylistVisibilitieToCSV(List<PlaylistVisibilitie> playlistVisibilitie) {
	   return ExportUtils.generateCsv(playlistVisibilitie);
	}  

	@Override
	public Page<PlaylistVisibilitie> getAllPlaylistVisibilitie(Pageable pageable) {
	    try {
	        return playlistVisibilitieRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving playlist visibilitie", ex);
	    }
	}

	@Override
	public Page<PlaylistVisibilitie> getAllPlaylistVisibilitie(Pageable pageable, PlaylistVisibilitieSearch object) {
	    try {
	        Specification<PlaylistVisibilitie> spec=PlaylistVisibilitieSpecification.filter(object);
	        return playlistVisibilitieRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving playlist visibilitie", ex);
	    }
	}

	@Override
	public PlaylistVisibilitie getPlaylistVisibilitieById(Long id) {
	    Optional<PlaylistVisibilitie> playlistVisibilitie = playlistVisibilitieRepository.findById(id);
	    if (playlistVisibilitie.isPresent()) {
	        return playlistVisibilitie.get();
	    } else {
	        throw new ResourceNotFoundException("PlaylistVisibilitie not found with id : " + id);
	    }
	}

	@Override
	public PlaylistVisibilitie createPlaylistVisibilitie(PlaylistVisibilitie playlistVisibilitie) {
	    try {
	        return playlistVisibilitieRepository.save(playlistVisibilitie);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating playlist visibilitie", ex);
	    }
	}

	@Override
	public PlaylistVisibilitie updatePlaylistVisibilitie(Long id, PlaylistVisibilitie playlistVisibilitie) {
	    Optional<PlaylistVisibilitie> existingPlaylistVisibilitie = playlistVisibilitieRepository.findById(id);
	    if (existingPlaylistVisibilitie.isPresent()) {
	        playlistVisibilitie.setId(id);
	        try {
	            return playlistVisibilitieRepository.save(playlistVisibilitie);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating playlist visibilitie", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("PlaylistVisibilitie not found with id : " + id);
	    }
	}

	@Override
	public void deletePlaylistVisibilitie(Long id) {
	    try {
	        playlistVisibilitieRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting playlist visibilitie", ex);
	    }
	}



}
