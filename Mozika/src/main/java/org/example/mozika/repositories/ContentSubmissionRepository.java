package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.ContentSubmission;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.Artist;



public interface ContentSubmissionRepository extends JpaRepository<ContentSubmission, Long>, JpaSpecificationExecutor<ContentSubmission> {

List<ContentSubmission> findByArtistidArtists(Artist artistidArtists);



}
