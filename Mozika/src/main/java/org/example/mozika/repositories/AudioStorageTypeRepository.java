package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.AudioStorageType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AudioStorageTypeRepository extends JpaRepository<AudioStorageType, Long>, JpaSpecificationExecutor<AudioStorageType> {

}
