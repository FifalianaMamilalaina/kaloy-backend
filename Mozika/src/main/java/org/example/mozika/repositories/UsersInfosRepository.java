package org.example.mozika.repositories;

import org.example.mozika.models.UsersInfos;
import org.example.mozika.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UsersInfosRepository
        extends JpaRepository<UsersInfos, Long>, JpaSpecificationExecutor<UsersInfos> {

    List<UsersInfos> findByUser(User user);
}
