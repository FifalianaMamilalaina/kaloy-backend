package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.VerificationCode;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import org.example.mozika.models.User;



public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long>, JpaSpecificationExecutor<VerificationCode> {

List<VerificationCode> findByUseridUsers(User useridUsers);

    Optional<VerificationCode> findTopByUseridUsersOrderByCreatedAtDesc(User useridUsers);

}
