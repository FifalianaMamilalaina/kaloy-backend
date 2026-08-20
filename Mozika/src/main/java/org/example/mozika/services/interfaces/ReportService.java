package org.example.mozika.services.interfaces;

import org.example.mozika.models.Report;
import org.example.mozika.models.dto.ReportSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface ReportService {
    Page<Report> getAllReport(Pageable pageable);

    Page<Report> getAllReport(Pageable pageable, ReportSearch object);

    Report getReportById(Long id);

    public String exportReportToCSV(List<Report> report);

    

    Report createReport(Report report);

    Report updateReport(Long id, Report report);

    void deleteReport(Long id);
    

}
