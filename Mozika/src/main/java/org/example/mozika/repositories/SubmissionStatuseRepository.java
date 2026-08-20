package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.SubmissionStatuse;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubmissionStatuseRepository extends JpaRepository<SubmissionStatuse, Long>, JpaSpecificationExecutor<SubmissionStatuse> {

}
