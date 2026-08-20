package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.ReportStatuse;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReportStatuseRepository extends JpaRepository<ReportStatuse, Long>, JpaSpecificationExecutor<ReportStatuse> {

}
