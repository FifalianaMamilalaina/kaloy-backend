package org.example.mozika.services.interfaces;

import org.example.mozika.models.AudioStorageType;
import org.example.mozika.models.dto.AudioStorageTypeSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface AudioStorageTypeService {
    Page<AudioStorageType> getAllAudioStorageType(Pageable pageable);

    Page<AudioStorageType> getAllAudioStorageType(Pageable pageable, AudioStorageTypeSearch object);

    AudioStorageType getAudioStorageTypeById(Long id);

    public String exportAudioStorageTypeToCSV(List<AudioStorageType> audioStorageType);

    

    AudioStorageType createAudioStorageType(AudioStorageType audioStorageType);

    AudioStorageType updateAudioStorageType(Long id, AudioStorageType audioStorageType);

    void deleteAudioStorageType(Long id);
    

}
