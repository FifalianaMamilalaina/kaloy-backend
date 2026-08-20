package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.MediaType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MediaTypeRepository extends JpaRepository<MediaType, Long>, JpaSpecificationExecutor<MediaType> {

}
