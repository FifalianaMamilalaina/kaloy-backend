package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.PlayMode;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlayModeRepository extends JpaRepository<PlayMode, Long>, JpaSpecificationExecutor<PlayMode> {

}
