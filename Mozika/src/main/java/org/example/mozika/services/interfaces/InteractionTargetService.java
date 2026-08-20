package org.example.mozika.services.interfaces;

import org.example.mozika.models.InteractionTarget;
import org.example.mozika.models.dto.InteractionTargetSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface InteractionTargetService {
    Page<InteractionTarget> getAllInteractionTarget(Pageable pageable);

    Page<InteractionTarget> getAllInteractionTarget(Pageable pageable, InteractionTargetSearch object);

    InteractionTarget getInteractionTargetById(Long id);

    public String exportInteractionTargetToCSV(List<InteractionTarget> interactionTarget);

    

    InteractionTarget createInteractionTarget(InteractionTarget interactionTarget);

    InteractionTarget updateInteractionTarget(Long id, InteractionTarget interactionTarget);

    void deleteInteractionTarget(Long id);
    

}
