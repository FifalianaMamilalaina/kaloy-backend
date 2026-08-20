package org.example.mozika.services.interfaces;

import org.example.mozika.models.VerificationCode;
import org.example.mozika.models.dto.VerificationCodeSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface VerificationCodeService {
    Page<VerificationCode> getAllVerificationCode(Pageable pageable);

    Page<VerificationCode> getAllVerificationCode(Pageable pageable, VerificationCodeSearch object);

    VerificationCode getVerificationCodeById(Long id);

    public String exportVerificationCodeToCSV(List<VerificationCode> verificationCode);

    

    VerificationCode createVerificationCode(VerificationCode verificationCode);

    VerificationCode updateVerificationCode(Long id, VerificationCode verificationCode);

    void deleteVerificationCode(Long id);
    

}
