package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.ParticipationStatuse;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParticipationStatuseRepository extends JpaRepository<ParticipationStatuse, Long>, JpaSpecificationExecutor<ParticipationStatuse> {

}
