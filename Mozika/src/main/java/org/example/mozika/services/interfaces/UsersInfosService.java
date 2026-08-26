package org.example.mozika.services.interfaces;

import org.example.mozika.models.UsersInfos;
import org.example.mozika.models.dto.UsersInfosSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsersInfosService {

    Page<UsersInfos> getAllUsersInfos(Pageable pageable);

    Page<UsersInfos> getAllUsersInfos(Pageable pageable, UsersInfosSearch object);

    UsersInfos getUsersInfosById(Long id);

    String exportUsersInfosToCSV(List<UsersInfos> usersInfos);

    UsersInfos createUsersInfos(UsersInfos usersInfos);

    UsersInfos updateUsersInfos(Long id, UsersInfos usersInfos);

    void deleteUsersInfos(Long id);
}
