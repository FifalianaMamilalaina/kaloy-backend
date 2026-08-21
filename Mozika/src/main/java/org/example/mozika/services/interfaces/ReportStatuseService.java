package org.example.mozika.services.interfaces;

import org.example.mozika.models.ReportStatuse;
import org.example.mozika.models.dto.ReportStatuseSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface ReportStatuseService {
    Page<ReportStatuse> getAllReportStatuse(Pageable pageable);

    Page<ReportStatuse> getAllReportStatuse(Pageable pageable, ReportStatuseSearch object);

    ReportStatuse getReportStatuseById(Long id);

    public String exportReportStatuseToCSV(List<ReportStatuse> reportStatuse);

    

    ReportStatuse createReportStatuse(ReportStatuse reportStatuse);

    ReportStatuse updateReportStatuse(Long id, ReportStatuse reportStatuse);

    void deleteReportStatuse(Long id);
    

}
