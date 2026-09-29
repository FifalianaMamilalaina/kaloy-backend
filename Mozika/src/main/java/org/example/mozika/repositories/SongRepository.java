package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Song;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.example.mozika.models.Artist;
import org.example.mozika.models.Album;



public interface SongRepository extends JpaRepository<Song, Long>, JpaSpecificationExecutor<Song> {

List<Song> findByArtistidArtists(Artist artistidArtists);
List<Song> findByAlbumidAlbums(Album albumidAlbums);

/**
 * Chansons d'un artiste, des plus ecoutees aux moins ecoutees.
 *
 * Le comptage est fait en base : sans cette requete, l'application devrait
 * interroger l'historique une fois par chanson, ce qui ne tient pas des que
 * le catalogue grandit.
 *
 * LEFT JOIN, et non JOIN : une chanson jamais ecoutee doit tout de meme
 * apparaitre, en fin de liste. Le tri secondaire par date de sortie fait que,
 * pour un artiste sans aucune ecoute, on obtient naturellement ses titres les
 * plus recents plutot qu'un ordre arbitraire.
 */
@Query(value = """
    SELECT s.* FROM songs s
    LEFT JOIN listening_history lh ON lh.song_id = s.id
    WHERE s.artist_id = :idArtiste
    GROUP BY s.id
    ORDER BY COUNT(lh.id) DESC, s.release_date DESC NULLS LAST, s.id DESC
    LIMIT :limite
    """, nativeQuery = true)
List<Song> trouverLesPlusEcoutees(@Param("idArtiste") Long idArtiste, @Param("limite") int limite);

}
