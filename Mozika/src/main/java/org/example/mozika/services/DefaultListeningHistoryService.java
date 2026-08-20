package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.ListeningHistory;
import org.example.mozika.models.dto.ListeningHistorySearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.ListeningHistoryRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.ListeningHistoryService;
import org.example.mozika.specification.ListeningHistorySpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultListeningHistoryService implements ListeningHistoryService {
	private final ListeningHistoryRepository listeningHistoryRepository;

	public DefaultListeningHistoryService(ListeningHistoryRepository listeningHistoryRepository) {
	   this.listeningHistoryRepository = listeningHistoryRepository;
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
