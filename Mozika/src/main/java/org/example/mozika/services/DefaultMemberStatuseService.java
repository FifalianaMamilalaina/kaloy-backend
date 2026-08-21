package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.MemberStatuse;
import org.example.mozika.models.dto.MemberStatuseSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.MemberStatuseRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.MemberStatuseService;
import org.example.mozika.specification.MemberStatuseSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultMemberStatuseService implements MemberStatuseService {
	private final MemberStatuseRepository memberStatuseRepository;

	public DefaultMemberStatuseService(MemberStatuseRepository memberStatuseRepository) {
	   this.memberStatuseRepository = memberStatuseRepository;
	}

	@Override
	public String exportMemberStatuseToCSV(List<MemberStatuse> memberStatuse) {
	   return ExportUtils.generateCsv(memberStatuse);
	}  

	@Override
	public Page<MemberStatuse> getAllMemberStatuse(Pageable pageable) {
	    try {
	        return memberStatuseRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving member statuse", ex);
	    }
	}

	@Override
	public Page<MemberStatuse> getAllMemberStatuse(Pageable pageable, MemberStatuseSearch object) {
	    try {
	        Specification<MemberStatuse> spec=MemberStatuseSpecification.filter(object);
	        return memberStatuseRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving member statuse", ex);
	    }
	}

	@Override
	public MemberStatuse getMemberStatuseById(Long id) {
	    Optional<MemberStatuse> memberStatuse = memberStatuseRepository.findById(id);
	    if (memberStatuse.isPresent()) {
	        return memberStatuse.get();
	    } else {
	        throw new ResourceNotFoundException("MemberStatuse not found with id : " + id);
	    }
	}

	@Override
	public MemberStatuse createMemberStatuse(MemberStatuse memberStatuse) {
	    try {
	        return memberStatuseRepository.save(memberStatuse);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating member statuse", ex);
	    }
	}

	@Override
	public MemberStatuse updateMemberStatuse(Long id, MemberStatuse memberStatuse) {
	    Optional<MemberStatuse> existingMemberStatuse = memberStatuseRepository.findById(id);
	    if (existingMemberStatuse.isPresent()) {
	        memberStatuse.setId(id);
	        try {
	            return memberStatuseRepository.save(memberStatuse);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating member statuse", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("MemberStatuse not found with id : " + id);
	    }
	}

	@Override
	public void deleteMemberStatuse(Long id) {
	    try {
	        memberStatuseRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting member statuse", ex);
	    }
	}



}
