package org.example.mozika.services.interfaces;

import org.example.mozika.models.MediaType;
import org.example.mozika.models.dto.MediaTypeSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface MediaTypeService {
    Page<MediaType> getAllMediaType(Pageable pageable);

    Page<MediaType> getAllMediaType(Pageable pageable, MediaTypeSearch object);

    MediaType getMediaTypeById(Long id);

    public String exportMediaTypeToCSV(List<MediaType> mediaType);

    

    MediaType createMediaType(MediaType mediaType);

    MediaType updateMediaType(Long id, MediaType mediaType);

    void deleteMediaType(Long id);
    

}
