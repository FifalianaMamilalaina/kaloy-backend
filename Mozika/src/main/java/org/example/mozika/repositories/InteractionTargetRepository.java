package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.InteractionTarget;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface InteractionTargetRepository extends JpaRepository<InteractionTarget, Long>, JpaSpecificationExecutor<InteractionTarget> {

// Les cibles vivent en base (EVENT, CONCERT, MEDIA, SONG, COMMENT) :
// on les resout par nom plutot que par un identifiant code en dur.
Optional<InteractionTarget> findByName(String name);

}
