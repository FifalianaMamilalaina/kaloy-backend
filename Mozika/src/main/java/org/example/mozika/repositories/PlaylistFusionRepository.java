package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.example.mozika.models.PlaylistFusion;

public interface PlaylistFusionRepository
		extends JpaRepository<PlaylistFusion, Long>, JpaSpecificationExecutor<PlaylistFusion> {
}
