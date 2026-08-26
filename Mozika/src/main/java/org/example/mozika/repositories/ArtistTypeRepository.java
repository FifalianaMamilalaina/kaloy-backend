package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.ArtistType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ArtistTypeRepository extends JpaRepository<ArtistType, Long>, JpaSpecificationExecutor<ArtistType> {
    Optional<ArtistType> findByName(String name);
}
