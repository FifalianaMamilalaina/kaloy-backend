package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.UserStatuse;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserStatuseRepository extends JpaRepository<UserStatuse, Long>, JpaSpecificationExecutor<UserStatuse> {

}
