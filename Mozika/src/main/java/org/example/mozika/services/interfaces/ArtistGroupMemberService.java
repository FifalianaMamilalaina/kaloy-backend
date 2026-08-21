package org.example.mozika.services.interfaces;

import org.example.mozika.models.ArtistGroupMember;
import org.example.mozika.models.dto.ArtistGroupMemberSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface ArtistGroupMemberService {
    Page<ArtistGroupMember> getAllArtistGroupMember(Pageable pageable);

    Page<ArtistGroupMember> getAllArtistGroupMember(Pageable pageable, ArtistGroupMemberSearch object);

    ArtistGroupMember getArtistGroupMemberById(Long id);

    public String exportArtistGroupMemberToCSV(List<ArtistGroupMember> artistGroupMember);

    

    ArtistGroupMember createArtistGroupMember(ArtistGroupMember artistGroupMember);

    ArtistGroupMember updateArtistGroupMember(Long id, ArtistGroupMember artistGroupMember);

    void deleteArtistGroupMember(Long id);
    

}
