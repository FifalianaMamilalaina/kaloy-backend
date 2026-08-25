package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.VerificationChannel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VerificationChannelRepository extends JpaRepository<VerificationChannel, Long>, JpaSpecificationExecutor<VerificationChannel> {
    Optional<VerificationChannel> findByName(String name);
}
