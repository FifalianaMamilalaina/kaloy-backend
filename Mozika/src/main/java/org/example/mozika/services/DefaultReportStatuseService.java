package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.ReportStatuse;
import org.example.mozika.models.dto.ReportStatuseSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.ReportStatuseRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.ReportStatuseService;
import org.example.mozika.specification.ReportStatuseSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultReportStatuseService implements ReportStatuseService {
	private final ReportStatuseRepository reportStatuseRepository;

	public DefaultReportStatuseService(ReportStatuseRepository reportStatuseRepository) {
	   this.reportStatuseRepository = reportStatuseRepository;
	}

	@Override
	public String exportReportStatuseToCSV(List<ReportStatuse> reportStatuse) {
	   return ExportUtils.generateCsv(reportStatuse);
	}  

	@Override
	public Page<ReportStatuse> getAllReportStatuse(Pageable pageable) {
	    try {
	        return reportStatuseRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving report statuse", ex);
	    }
	}

	@Override
	public Page<ReportStatuse> getAllReportStatuse(Pageable pageable, ReportStatuseSearch object) {
	    try {
	        Specification<ReportStatuse> spec=ReportStatuseSpecification.filter(object);
	        return reportStatuseRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving report statuse", ex);
	    }
	}

	@Override
	public ReportStatuse getReportStatuseById(Long id) {
	    Optional<ReportStatuse> reportStatuse = reportStatuseRepository.findById(id);
	    if (reportStatuse.isPresent()) {
	        return reportStatuse.get();
	    } else {
	        throw new ResourceNotFoundException("ReportStatuse not found with id : " + id);
	    }
	}

	@Override
	public ReportStatuse createReportStatuse(ReportStatuse reportStatuse) {
	    try {
	        return reportStatuseRepository.save(reportStatuse);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating report statuse", ex);
	    }
	}

	@Override
	public ReportStatuse updateReportStatuse(Long id, ReportStatuse reportStatuse) {
	    Optional<ReportStatuse> existingReportStatuse = reportStatuseRepository.findById(id);
	    if (existingReportStatuse.isPresent()) {
	        reportStatuse.setId(id);
	        try {
	            return reportStatuseRepository.save(reportStatuse);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating report statuse", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("ReportStatuse not found with id : " + id);
	    }
	}

	@Override
	public void deleteReportStatuse(Long id) {
	    try {
	        reportStatuseRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting report statuse", ex);
	    }
	}



}
