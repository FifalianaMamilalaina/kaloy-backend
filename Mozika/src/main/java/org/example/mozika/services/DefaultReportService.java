package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Report;
import org.example.mozika.models.dto.ReportSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.ReportRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.ReportService;
import org.example.mozika.specification.ReportSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultReportService implements ReportService {
	private final ReportRepository reportRepository;

	public DefaultReportService(ReportRepository reportRepository) {
	   this.reportRepository = reportRepository;
	}

	@Override
	public String exportReportToCSV(List<Report> report) {
	   return ExportUtils.generateCsv(report);
	}  

	@Override
	public Page<Report> getAllReport(Pageable pageable) {
	    try {
	        return reportRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving report", ex);
	    }
	}

	@Override
	public Page<Report> getAllReport(Pageable pageable, ReportSearch object) {
	    try {
	        Specification<Report> spec=ReportSpecification.filter(object);
	        return reportRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving report", ex);
	    }
	}

	@Override
	public Report getReportById(Long id) {
	    Optional<Report> report = reportRepository.findById(id);
	    if (report.isPresent()) {
	        return report.get();
	    } else {
	        throw new ResourceNotFoundException("Report not found with id : " + id);
	    }
	}

	@Override
	public Report createReport(Report report) {
	    try {
	        return reportRepository.save(report);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating report", ex);
	    }
	}

	@Override
	public Report updateReport(Long id, Report report) {
	    Optional<Report> existingReport = reportRepository.findById(id);
	    if (existingReport.isPresent()) {
	        report.setId(id);
	        try {
	            return reportRepository.save(report);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating report", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Report not found with id : " + id);
	    }
	}

	@Override
	public void deleteReport(Long id) {
	    try {
	        reportRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting report", ex);
	    }
	}



}
