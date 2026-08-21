package org.example.mozika.services.interfaces;

import org.example.mozika.models.Download;
import org.example.mozika.models.dto.DownloadSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface DownloadService {
    Page<Download> getAllDownload(Pageable pageable);

    Page<Download> getAllDownload(Pageable pageable, DownloadSearch object);

    Download getDownloadById(Long id);

    public String exportDownloadToCSV(List<Download> download);

    

    Download createDownload(Download download);

    Download updateDownload(Long id, Download download);

    void deleteDownload(Long id);
    

}
