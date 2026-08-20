package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Report;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;



public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

List<Report> findByReporteruseridUsers(User reporteruseridUsers);



}
