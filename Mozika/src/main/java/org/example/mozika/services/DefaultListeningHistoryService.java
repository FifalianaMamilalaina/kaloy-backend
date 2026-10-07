package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.ListeningHistory;
import org.example.mozika.models.Song;
import org.example.mozika.models.PlayMode;
import org.example.mozika.models.dto.ListeningHistorySearch;
import org.example.mozika.models.dto.ListeningHistoryItemResponse;
import org.example.mozika.models.dto.CreateMyListeningHistoryRequest;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.ListeningHistoryRepository;
import org.example.mozika.repositories.UserRepository;
import org.example.mozika.models.User;
import java.util.Optional;
import org.example.mozika.services.interfaces.ListeningHistoryService;
import org.example.mozika.specification.ListeningHistorySpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Service
public class DefaultListeningHistoryService implements ListeningHistoryService {
	private final ListeningHistoryRepository listeningHistoryRepository;
	private final UserRepository userRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public DefaultListeningHistoryService(
			ListeningHistoryRepository listeningHistoryRepository,
			UserRepository userRepository
	) {
	   this.listeningHistoryRepository = listeningHistoryRepository;
	   this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ListeningHistoryItemResponse> getMyListeningHistory(
			String email,
			LocalDateTime startAt,
			LocalDateTime endAt,
			String search,
			Pageable pageable
	) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));

	    Specification<ListeningHistory> spec =
	            (root, query, cb) -> cb.equal(root.get("useridUsers"), user);

	    if (startAt != null) {
	        spec = spec.and((root, query, cb) ->
	                cb.greaterThanOrEqualTo(root.get("listenedAt"), startAt));
	    }
	    if (endAt != null) {
	        spec = spec.and((root, query, cb) ->
	                cb.lessThan(root.get("listenedAt"), endAt));
	    }
	    if (search != null && !search.isBlank()) {
	        String pattern = "%" + search.toLowerCase() + "%";
	        spec = spec.and((root, query, cb) -> cb.or(
	                cb.like(cb.lower(root.get("songidSongs").get("title")), pattern),
	                cb.like(cb.lower(root.get("songidSongs").get("artistidArtists").get("stageName")), pattern)
	        ));
	    }

	    return listeningHistoryRepository.findAll(spec, pageable)
	            .map(history -> new ListeningHistoryItemResponse(
	                    history.getId(),
	                    history.getListenedAt(),
	                    history.getDurationListenedSeconds(),
	                    history.getCompleted(),
	                    history.getSongidSongs().getId(),
	                    history.getSongidSongs().getTitle(),
	                    history.getSongidSongs().getDurationSeconds(),
	                    history.getSongidSongs().getArtistidArtists().getStageName()
	            ));
	}

	@Override
	public String exportListeningHistoryToCSV(List<ListeningHistory> listeningHistory) {
	   return ExportUtils.generateCsv(listeningHistory);
	}  

	@Override
	public Page<ListeningHistory> getAllListeningHistory(Pageable pageable) {
	    try {
	        return listeningHistoryRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving listening history", ex);
	    }
	}

	@Override
	public Page<ListeningHistory> getAllListeningHistory(Pageable pageable, ListeningHistorySearch object) {
	    try {
	        Specification<ListeningHistory> spec=ListeningHistorySpecification.filter(object);
	        return listeningHistoryRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving listening history", ex);
	    }
	}

	@Override
	public ListeningHistory getListeningHistoryById(Long id) {
	    Optional<ListeningHistory> listeningHistory = listeningHistoryRepository.findById(id);
	    if (listeningHistory.isPresent()) {
	        return listeningHistory.get();
	    } else {
	        throw new ResourceNotFoundException("ListeningHistory not found with id : " + id);
	    }
	}

	@Override
	@Transactional
	public ListeningHistory createMyListeningHistory(String email, CreateMyListeningHistoryRequest request) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));

	    List<ListeningHistory> existing = listeningHistoryRepository
	            .findByUserIdAndSongId(user.getId(), request.songId());

	    ListeningHistory lh;
	    if (existing.isEmpty()) {
	        lh = new ListeningHistory();
	        lh.setUseridUsers(user);
	        lh.setSongidSongs(entityManager.getReference(Song.class, request.songId()));
	        lh.setPlaymodeidPlayModes(entityManager.getReference(PlayMode.class, request.playModeId()));
	    } else {
	        lh = existing.get(0);
	        if (existing.size() > 1) {
	            listeningHistoryRepository.deleteAll(existing.subList(1, existing.size()));
	        }
	    }

	    lh.setListenedAt(LocalDateTime.now(ZoneId.of("Indian/Antananarivo")));
	    lh.setDurationListenedSeconds(request.durationListenedSeconds());
	    lh.setCompleted(request.completed());
	    return listeningHistoryRepository.save(lh);
	}

	@Override
	@Transactional
	public void deleteMyListeningHistory(String email, Long id) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
	    ListeningHistory lh = listeningHistoryRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Entrée d'historique introuvable."));
	    if (!lh.getUseridUsers().getId().equals(user.getId())) {
	        throw new ResourceNotFoundException("Entrée d'historique introuvable.");
	    }
	    listeningHistoryRepository.deleteById(id);
	}

	@Override
	public ListeningHistory createListeningHistory(ListeningHistory listeningHistory) {
	    try {
	        return listeningHistoryRepository.save(listeningHistory);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating listening history", ex);
	    }
	}

	@Override
	public ListeningHistory updateListeningHistory(Long id, ListeningHistory listeningHistory) {
	    Optional<ListeningHistory> existingListeningHistory = listeningHistoryRepository.findById(id);
	    if (existingListeningHistory.isPresent()) {
	        listeningHistory.setId(id);
	        try {
	            return listeningHistoryRepository.save(listeningHistory);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating listening history", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("ListeningHistory not found with id : " + id);
	    }
	}

	@Override
	public void deleteListeningHistory(Long id) {
	    try {
	        listeningHistoryRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting listening history", ex);
	    }
	}



}
