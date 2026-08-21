package org.example.mozika.services.interfaces;

import org.example.mozika.models.VerificationStatuse;
import org.example.mozika.models.dto.VerificationStatuseSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface VerificationStatuseService {
    Page<VerificationStatuse> getAllVerificationStatuse(Pageable pageable);

    Page<VerificationStatuse> getAllVerificationStatuse(Pageable pageable, VerificationStatuseSearch object);

    VerificationStatuse getVerificationStatuseById(Long id);

    public String exportVerificationStatuseToCSV(List<VerificationStatuse> verificationStatuse);

    

    VerificationStatuse createVerificationStatuse(VerificationStatuse verificationStatuse);

    VerificationStatuse updateVerificationStatuse(Long id, VerificationStatuse verificationStatuse);

    void deleteVerificationStatuse(Long id);
    

}
