package org.example.mozika.services.interfaces;

import org.example.mozika.models.ListeningHistory;
import org.example.mozika.models.dto.ListeningHistorySearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface ListeningHistoryService {
    Page<ListeningHistory> getAllListeningHistory(Pageable pageable);

    Page<ListeningHistory> getAllListeningHistory(Pageable pageable, ListeningHistorySearch object);

    ListeningHistory getListeningHistoryById(Long id);

    public String exportListeningHistoryToCSV(List<ListeningHistory> listeningHistory);

    

    ListeningHistory createListeningHistory(ListeningHistory listeningHistory);

    ListeningHistory updateListeningHistory(Long id, ListeningHistory listeningHistory);

    void deleteListeningHistory(Long id);
    

}
