package org.example.mozika.services.interfaces;

import org.example.mozika.models.ContentSubmission;
import org.example.mozika.models.dto.ContentSubmissionSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface ContentSubmissionService {
    Page<ContentSubmission> getAllContentSubmission(Pageable pageable);

    Page<ContentSubmission> getAllContentSubmission(Pageable pageable, ContentSubmissionSearch object);

    ContentSubmission getContentSubmissionById(Long id);

    public String exportContentSubmissionToCSV(List<ContentSubmission> contentSubmission);

    

    ContentSubmission createContentSubmission(ContentSubmission contentSubmission);

    ContentSubmission updateContentSubmission(Long id, ContentSubmission contentSubmission);

    void deleteContentSubmission(Long id);
    

}
