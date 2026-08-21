package org.example.mozika.services.interfaces;

import org.example.mozika.models.UserStatusHistory;
import org.example.mozika.models.dto.UserStatusHistorySearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface UserStatusHistoryService {
    Page<UserStatusHistory> getAllUserStatusHistory(Pageable pageable);

    Page<UserStatusHistory> getAllUserStatusHistory(Pageable pageable, UserStatusHistorySearch object);

    UserStatusHistory getUserStatusHistoryById(Long id);

    public String exportUserStatusHistoryToCSV(List<UserStatusHistory> userStatusHistory);

    

    UserStatusHistory createUserStatusHistory(UserStatusHistory userStatusHistory);

    UserStatusHistory updateUserStatusHistory(Long id, UserStatusHistory userStatusHistory);

    void deleteUserStatusHistory(Long id);
    

}
