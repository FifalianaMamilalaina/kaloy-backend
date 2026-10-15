package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.ListeningHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.example.mozika.models.User;
import org.example.mozika.models.Song;
import org.example.mozika.models.Artist;
import java.time.LocalDateTime;



public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long>, JpaSpecificationExecutor<ListeningHistory> {

List<ListeningHistory> findByUseridUsers(User useridUsers);
List<ListeningHistory> findBySongidSongs(Song songidSongs);

@Query("SELECT lh FROM ListeningHistory lh WHERE lh.useridUsers.id = :userId AND lh.songidSongs.id = :songId ORDER BY lh.listenedAt DESC")
List<ListeningHistory> findByUserIdAndSongId(@Param("userId") Long userId, @Param("songId") Long songId);


// Ecoutes de toutes les chansons d'un artiste. Le chemin traverse
// deux relations : ListeningHistory -> Song -> Artist. L'underscore
// leve l'ambiguite pour Spring Data, qui sinon chercherait une
// propriete nommee « songidSongsArtistidArtists ».
long countBySongidSongs_ArtistidArtists(Artist artiste);

long countBySongidSongs_ArtistidArtistsAndListenedAtAfter(Artist artiste, LocalDateTime depuis);

// Personnes distinctes, et non nombre d'ecoutes : un auditeur qui
// repasse le meme titre vingt fois reste un auditeur.
@Query("""
    SELECT COUNT(DISTINCT lh.useridUsers.id)
    FROM ListeningHistory lh
    WHERE lh.songidSongs.artistidArtists = :artiste
    """)
long compterAuditeursDistincts(@Param("artiste") Artist artiste);
}
