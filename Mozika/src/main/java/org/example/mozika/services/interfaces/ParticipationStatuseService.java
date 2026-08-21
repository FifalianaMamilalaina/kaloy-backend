package org.example.mozika.services.interfaces;

import org.example.mozika.models.ParticipationStatuse;
import org.example.mozika.models.dto.ParticipationStatuseSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface ParticipationStatuseService {
    Page<ParticipationStatuse> getAllParticipationStatuse(Pageable pageable);

    Page<ParticipationStatuse> getAllParticipationStatuse(Pageable pageable, ParticipationStatuseSearch object);

    ParticipationStatuse getParticipationStatuseById(Long id);

    public String exportParticipationStatuseToCSV(List<ParticipationStatuse> participationStatuse);

    

    ParticipationStatuse createParticipationStatuse(ParticipationStatuse participationStatuse);

    ParticipationStatuse updateParticipationStatuse(Long id, ParticipationStatuse participationStatuse);

    void deleteParticipationStatuse(Long id);
    

}
