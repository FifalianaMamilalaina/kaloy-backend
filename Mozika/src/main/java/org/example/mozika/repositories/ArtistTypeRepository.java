package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.ArtistType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArtistTypeRepository extends JpaRepository<ArtistType, Long>, JpaSpecificationExecutor<ArtistType> {

}
