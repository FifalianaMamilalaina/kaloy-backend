package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.VerificationStatuse;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;
public interface VerificationStatuseRepository extends JpaRepository<VerificationStatuse, Long>, JpaSpecificationExecutor<VerificationStatuse> {
    Optional<VerificationStatuse> findByName(String name);
}
