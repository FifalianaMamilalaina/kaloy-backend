package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.ArtistGroupMember;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.Artist;



public interface ArtistGroupMemberRepository extends JpaRepository<ArtistGroupMember, Long>, JpaSpecificationExecutor<ArtistGroupMember> {

List<ArtistGroupMember> findByGroupartistidArtists(Artist groupartistidArtists);



}
