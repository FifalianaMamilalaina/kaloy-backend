package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.VerificationChannel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VerificationChannelRepository extends JpaRepository<VerificationChannel, Long>, JpaSpecificationExecutor<VerificationChannel> {

}
