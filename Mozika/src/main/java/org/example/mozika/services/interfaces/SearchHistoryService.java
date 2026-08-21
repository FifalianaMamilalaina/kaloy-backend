package org.example.mozika.services.interfaces;

import org.example.mozika.models.SearchHistory;
import org.example.mozika.models.dto.SearchHistorySearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface SearchHistoryService {
    Page<SearchHistory> getAllSearchHistory(Pageable pageable);

    Page<SearchHistory> getAllSearchHistory(Pageable pageable, SearchHistorySearch object);

    SearchHistory getSearchHistoryById(Long id);

    public String exportSearchHistoryToCSV(List<SearchHistory> searchHistory);

    

    SearchHistory createSearchHistory(SearchHistory searchHistory);

    SearchHistory updateSearchHistory(Long id, SearchHistory searchHistory);

    void deleteSearchHistory(Long id);
    

}
