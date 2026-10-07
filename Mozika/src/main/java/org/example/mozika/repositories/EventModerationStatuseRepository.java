package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.EventModerationStatuse;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EventModerationStatuseRepository extends JpaRepository<EventModerationStatuse, Long>, JpaSpecificationExecutor<EventModerationStatuse> {

    /** Retrouve PENDING, APPROVED ou REJECTED par son nom plutot que par un id code en dur. */
    Optional<EventModerationStatuse> findByName(String name);

}
