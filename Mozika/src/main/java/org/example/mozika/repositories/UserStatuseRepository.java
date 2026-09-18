package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.UserStatuse;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface UserStatuseRepository extends JpaRepository<UserStatuse, Long>, JpaSpecificationExecutor<UserStatuse> {
    Optional<UserStatuse> findByName(String name);
}
