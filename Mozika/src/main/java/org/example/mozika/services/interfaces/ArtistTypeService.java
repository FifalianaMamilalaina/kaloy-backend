package org.example.mozika.services.interfaces;

import org.example.mozika.models.ArtistType;
import org.example.mozika.models.dto.ArtistTypeSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface ArtistTypeService {
    Page<ArtistType> getAllArtistType(Pageable pageable);

    Page<ArtistType> getAllArtistType(Pageable pageable, ArtistTypeSearch object);

    ArtistType getArtistTypeById(Long id);

    public String exportArtistTypeToCSV(List<ArtistType> artistType);

    

    ArtistType createArtistType(ArtistType artistType);

    ArtistType updateArtistType(Long id, ArtistType artistType);

    void deleteArtistType(Long id);
    

}
