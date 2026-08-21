package org.example.mozika.services.interfaces;

import org.example.mozika.models.MemberStatuse;
import org.example.mozika.models.dto.MemberStatuseSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface MemberStatuseService {
    Page<MemberStatuse> getAllMemberStatuse(Pageable pageable);

    Page<MemberStatuse> getAllMemberStatuse(Pageable pageable, MemberStatuseSearch object);

    MemberStatuse getMemberStatuseById(Long id);

    public String exportMemberStatuseToCSV(List<MemberStatuse> memberStatuse);

    

    MemberStatuse createMemberStatuse(MemberStatuse memberStatuse);

    MemberStatuse updateMemberStatuse(Long id, MemberStatuse memberStatuse);

    void deleteMemberStatuse(Long id);
    

}
