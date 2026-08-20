package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Download;
import org.example.mozika.models.dto.DownloadSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.DownloadRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.DownloadService;
import org.example.mozika.specification.DownloadSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultDownloadService implements DownloadService {
	private final DownloadRepository downloadRepository;

	public DefaultDownloadService(DownloadRepository downloadRepository) {
	   this.downloadRepository = downloadRepository;
	}

	@Override
	public String exportDownloadToCSV(List<Download> download) {
	   return ExportUtils.generateCsv(download);
	}  

	@Override
	public Page<Download> getAllDownload(Pageable pageable) {
	    try {
	        return downloadRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving download", ex);
	    }
	}

	@Override
	public Page<Download> getAllDownload(Pageable pageable, DownloadSearch object) {
	    try {
	        Specification<Download> spec=DownloadSpecification.filter(object);
	        return downloadRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving download", ex);
	    }
	}

	@Override
	public Download getDownloadById(Long id) {
	    Optional<Download> download = downloadRepository.findById(id);
	    if (download.isPresent()) {
	        return download.get();
	    } else {
	        throw new ResourceNotFoundException("Download not found with id : " + id);
	    }
	}

	@Override
	public Download createDownload(Download download) {
	    try {
	        return downloadRepository.save(download);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating download", ex);
	    }
	}

	@Override
	public Download updateDownload(Long id, Download download) {
	    Optional<Download> existingDownload = downloadRepository.findById(id);
	    if (existingDownload.isPresent()) {
	        download.setId(id);
	        try {
	            return downloadRepository.save(download);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating download", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Download not found with id : " + id);
	    }
	}

	@Override
	public void deleteDownload(Long id) {
	    try {
	        downloadRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting download", ex);
	    }
	}



}
