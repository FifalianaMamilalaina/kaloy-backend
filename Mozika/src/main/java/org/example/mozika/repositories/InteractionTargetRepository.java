package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.InteractionTarget;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InteractionTargetRepository extends JpaRepository<InteractionTarget, Long>, JpaSpecificationExecutor<InteractionTarget> {

}
