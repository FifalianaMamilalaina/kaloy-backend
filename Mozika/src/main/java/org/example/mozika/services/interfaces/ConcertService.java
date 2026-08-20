package org.example.mozika.services.interfaces;

import org.example.mozika.models.Concert;
import org.example.mozika.models.dto.ConcertSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface ConcertService {
    Page<Concert> getAllConcert(Pageable pageable);

    Page<Concert> getAllConcert(Pageable pageable, ConcertSearch object);

    Concert getConcertById(Long id);

    public String exportConcertToCSV(List<Concert> concert);

    

    Concert createConcert(Concert concert);

    Concert updateConcert(Long id, Concert concert);

    void deleteConcert(Long id);
    

}
