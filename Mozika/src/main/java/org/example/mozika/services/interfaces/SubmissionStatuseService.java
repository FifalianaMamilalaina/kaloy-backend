package org.example.mozika.services.interfaces;

import org.example.mozika.models.SubmissionStatuse;
import org.example.mozika.models.dto.SubmissionStatuseSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface SubmissionStatuseService {
    Page<SubmissionStatuse> getAllSubmissionStatuse(Pageable pageable);

    Page<SubmissionStatuse> getAllSubmissionStatuse(Pageable pageable, SubmissionStatuseSearch object);

    SubmissionStatuse getSubmissionStatuseById(Long id);

    public String exportSubmissionStatuseToCSV(List<SubmissionStatuse> submissionStatuse);

    

    SubmissionStatuse createSubmissionStatuse(SubmissionStatuse submissionStatuse);

    SubmissionStatuse updateSubmissionStatuse(Long id, SubmissionStatuse submissionStatuse);

    void deleteSubmissionStatuse(Long id);
    

}
