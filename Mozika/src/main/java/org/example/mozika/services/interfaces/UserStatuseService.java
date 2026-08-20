package org.example.mozika.services.interfaces;

import org.example.mozika.models.UserStatuse;
import org.example.mozika.models.dto.UserStatuseSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface UserStatuseService {
    Page<UserStatuse> getAllUserStatuse(Pageable pageable);

    Page<UserStatuse> getAllUserStatuse(Pageable pageable, UserStatuseSearch object);

    UserStatuse getUserStatuseById(Long id);

    public String exportUserStatuseToCSV(List<UserStatuse> userStatuse);

    

    UserStatuse createUserStatuse(UserStatuse userStatuse);

    UserStatuse updateUserStatuse(Long id, UserStatuse userStatuse);

    void deleteUserStatuse(Long id);
    

}
