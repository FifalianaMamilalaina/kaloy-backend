package org.example.mozika.services.interfaces;

import org.example.mozika.models.VerificationChannel;
import org.example.mozika.models.dto.VerificationChannelSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface VerificationChannelService {
    Page<VerificationChannel> getAllVerificationChannel(Pageable pageable);

    Page<VerificationChannel> getAllVerificationChannel(Pageable pageable, VerificationChannelSearch object);

    VerificationChannel getVerificationChannelById(Long id);

    public String exportVerificationChannelToCSV(List<VerificationChannel> verificationChannel);

    

    VerificationChannel createVerificationChannel(VerificationChannel verificationChannel);

    VerificationChannel updateVerificationChannel(Long id, VerificationChannel verificationChannel);

    void deleteVerificationChannel(Long id);
    

}
