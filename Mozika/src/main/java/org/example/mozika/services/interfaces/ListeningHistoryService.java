package org.example.mozika.services.interfaces;

import org.example.mozika.models.ListeningHistory;
import org.example.mozika.models.dto.ListeningHistorySearch;
import org.example.mozika.models.dto.ListeningHistoryItemResponse;
import org.example.mozika.models.dto.CreateMyListeningHistoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;
import java.time.LocalDateTime;


public interface ListeningHistoryService {
    Page<ListeningHistory> getAllListeningHistory(Pageable pageable);

    Page<ListeningHistory> getAllListeningHistory(Pageable pageable, ListeningHistorySearch object);

    Page<ListeningHistoryItemResponse> getMyListeningHistory(
            String email,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String search,
            Pageable pageable
    );

    ListeningHistory getListeningHistoryById(Long id);

    public String exportListeningHistoryToCSV(List<ListeningHistory> listeningHistory);

    

    ListeningHistory createListeningHistory(ListeningHistory listeningHistory);

    ListeningHistory createMyListeningHistory(String email, CreateMyListeningHistoryRequest request);

    void deleteMyListeningHistory(String email, Long id);

    ListeningHistory updateListeningHistory(Long id, ListeningHistory listeningHistory);

    void deleteListeningHistory(Long id);
    

}
