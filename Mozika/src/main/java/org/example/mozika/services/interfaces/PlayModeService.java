package org.example.mozika.services.interfaces;

import org.example.mozika.models.PlayMode;
import org.example.mozika.models.dto.PlayModeSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface PlayModeService {
    Page<PlayMode> getAllPlayMode(Pageable pageable);

    Page<PlayMode> getAllPlayMode(Pageable pageable, PlayModeSearch object);

    PlayMode getPlayModeById(Long id);

    public String exportPlayModeToCSV(List<PlayMode> playMode);

    

    PlayMode createPlayMode(PlayMode playMode);

    PlayMode updatePlayMode(Long id, PlayMode playMode);

    void deletePlayMode(Long id);
    

}
