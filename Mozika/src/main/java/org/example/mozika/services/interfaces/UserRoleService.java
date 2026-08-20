package org.example.mozika.services.interfaces;

import org.example.mozika.models.UserRole;
import org.example.mozika.models.dto.UserRoleSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface UserRoleService {
    Page<UserRole> getAllUserRole(Pageable pageable);

    Page<UserRole> getAllUserRole(Pageable pageable, UserRoleSearch object);

    UserRole getUserRoleById(Long id);

    public String exportUserRoleToCSV(List<UserRole> userRole);

    

    UserRole createUserRole(UserRole userRole);

    UserRole updateUserRole(Long id, UserRole userRole);

    void deleteUserRole(Long id);
    

}
