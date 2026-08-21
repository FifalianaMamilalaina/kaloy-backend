package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.SearchHistory;
import org.example.mozika.models.dto.SearchHistorySearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.SearchHistoryRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.SearchHistoryService;
import org.example.mozika.specification.SearchHistorySpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultSearchHistoryService implements SearchHistoryService {
	private final SearchHistoryRepository searchHistoryRepository;

	public DefaultSearchHistoryService(SearchHistoryRepository searchHistoryRepository) {
	   this.searchHistoryRepository = searchHistoryRepository;
	}

	@Override
	public String exportSearchHistoryToCSV(List<SearchHistory> searchHistory) {
	   return ExportUtils.generateCsv(searchHistory);
	}  

	@Override
	public Page<SearchHistory> getAllSearchHistory(Pageable pageable) {
	    try {
	        return searchHistoryRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving search history", ex);
	    }
	}

	@Override
	public Page<SearchHistory> getAllSearchHistory(Pageable pageable, SearchHistorySearch object) {
	    try {
	        Specification<SearchHistory> spec=SearchHistorySpecification.filter(object);
	        return searchHistoryRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving search history", ex);
	    }
	}

	@Override
	public SearchHistory getSearchHistoryById(Long id) {
	    Optional<SearchHistory> searchHistory = searchHistoryRepository.findById(id);
	    if (searchHistory.isPresent()) {
	        return searchHistory.get();
	    } else {
	        throw new ResourceNotFoundException("SearchHistory not found with id : " + id);
	    }
	}

	@Override
	public SearchHistory createSearchHistory(SearchHistory searchHistory) {
	    try {
	        return searchHistoryRepository.save(searchHistory);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating search history", ex);
	    }
	}

	@Override
	public SearchHistory updateSearchHistory(Long id, SearchHistory searchHistory) {
	    Optional<SearchHistory> existingSearchHistory = searchHistoryRepository.findById(id);
	    if (existingSearchHistory.isPresent()) {
	        searchHistory.setId(id);
	        try {
	            return searchHistoryRepository.save(searchHistory);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating search history", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("SearchHistory not found with id : " + id);
	    }
	}

	@Override
	public void deleteSearchHistory(Long id) {
	    try {
	        searchHistoryRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting search history", ex);
	    }
	}



}
