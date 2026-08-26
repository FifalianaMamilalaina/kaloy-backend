package org.example.mozika.repositories;

import org.example.mozika.models.VerificationStatusUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationStatusUserRepository extends JpaRepository<VerificationStatusUser, Long> {
    Optional<VerificationStatusUser> findByName(String name);
}
