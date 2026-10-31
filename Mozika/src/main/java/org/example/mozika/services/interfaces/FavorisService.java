package org.example.mozika.services.interfaces;

import org.example.mozika.models.dto.FavorisAlbumResponse;
import org.example.mozika.models.dto.FavorisArtisteResponse;
import org.example.mozika.models.dto.FavorisSongResponse;

import java.util.List;

public interface FavorisService {

    // ── Chansons ──────────────────────────────────────────────────────────────

    List<FavorisSongResponse> getMesFavorisChansons(String email);

    FavorisSongResponse aimerChanson(String email, Long songId);

    void retirerChansonDesFavoris(String email, Long songId);

    boolean estFavoriChanson(String email, Long songId);

    // ── Albums ────────────────────────────────────────────────────────────────

    List<FavorisAlbumResponse> getMesFavorisAlbums(String email);

    FavorisAlbumResponse aimerAlbum(String email, Long albumId);

    void retirerAlbumDesFavoris(String email, Long albumId);

    boolean estFavoriAlbum(String email, Long albumId);

    // ── Artistes ──────────────────────────────────────────────────────────────

    List<FavorisArtisteResponse> getMesFavorisArtistes(String email);

    FavorisArtisteResponse aimerArtiste(String email, Long artistId);

    void retirerArtisteDesFavoris(String email, Long artistId);

    boolean estFavoriArtiste(String email, Long artistId);
}
