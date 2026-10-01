package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.ParticipationStatuse;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ParticipationStatuseRepository extends JpaRepository<ParticipationStatuse, Long>, JpaSpecificationExecutor<ParticipationStatuse> {

    /**
     * Les trois statuts sont une table de reference. On les retrouve par leur
     * nom plutot que par un identifiant code en dur : rien ne garantit l'ordre
     * des insertions d'une base a l'autre.
     */
    Optional<ParticipationStatuse> findByName(String name);

}
