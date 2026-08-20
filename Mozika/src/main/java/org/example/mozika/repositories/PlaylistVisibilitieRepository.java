package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.PlaylistVisibilitie;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlaylistVisibilitieRepository extends JpaRepository<PlaylistVisibilitie, Long>, JpaSpecificationExecutor<PlaylistVisibilitie> {

}
