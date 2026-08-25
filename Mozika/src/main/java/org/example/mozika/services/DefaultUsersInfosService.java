package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.UsersInfos;
import org.example.mozika.models.dto.UsersInfosSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.UsersInfosRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.UsersInfosService;
import org.example.mozika.specification.UsersInfosSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

@Service
public class DefaultUsersInfosService implements UsersInfosService {

    private final UsersInfosRepository usersInfosRepository;

    public DefaultUsersInfosService(UsersInfosRepository usersInfosRepository) {
        this.usersInfosRepository = usersInfosRepository;
    }

    @Override
    public String exportUsersInfosToCSV(List<UsersInfos> usersInfos) {
        return ExportUtils.generateCsv(usersInfos);
    }

    @Override
    public Page<UsersInfos> getAllUsersInfos(Pageable pageable) {
        try {
            return usersInfosRepository.findAll(pageable);
        } catch (Exception ex) {
            throw new InternalServerErrorException("Error while retrieving users infos", ex);
        }
    }

    @Override
    public Page<UsersInfos> getAllUsersInfos(Pageable pageable, UsersInfosSearch object) {
        try {
            Specification<UsersInfos> spec = UsersInfosSpecification.filter(object);
            return usersInfosRepository.findAll(spec, pageable);
        } catch (Exception ex) {
            throw new InternalServerErrorException("Error while retrieving users infos", ex);
        }
    }

    @Override
    public UsersInfos getUsersInfosById(Long id) {
        Optional<UsersInfos> usersInfos = usersInfosRepository.findById(id);
        if (usersInfos.isPresent()) {
            return usersInfos.get();
        } else {
            throw new ResourceNotFoundException("UsersInfos not found with id : " + id);
        }
    }

    @Override
    public UsersInfos createUsersInfos(UsersInfos usersInfos) {
        try {
            return usersInfosRepository.save(usersInfos);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerErrorException("Error while creating users infos", ex);
        }
    }

    @Override
    public UsersInfos updateUsersInfos(Long id, UsersInfos usersInfos) {
        Optional<UsersInfos> existing = usersInfosRepository.findById(id);
        if (existing.isPresent()) {
            usersInfos.setId(id);
            try {
                return usersInfosRepository.save(usersInfos);
            } catch (DataIntegrityViolationException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new InternalServerErrorException("Error while updating users infos", ex);
            }
        } else {
            throw new ResourceNotFoundException("UsersInfos not found with id : " + id);
        }
    }

    @Override
    public void deleteUsersInfos(Long id) {
        try {
            usersInfosRepository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerErrorException("Error while deleting users infos", ex);
        }
    }
}
