package org.example.mozika.services;

import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.Album;
import org.example.mozika.models.Artist;
import org.example.mozika.models.InteractionTarget;
import org.example.mozika.models.Like;
import org.example.mozika.models.Song;
import org.example.mozika.models.User;
import org.example.mozika.models.dto.FavorisAlbumResponse;
import org.example.mozika.models.dto.FavorisArtisteResponse;
import org.example.mozika.models.dto.FavorisSongResponse;
import org.example.mozika.repositories.AlbumRepository;
import org.example.mozika.repositories.ArtistRepository;
import org.example.mozika.repositories.InteractionTargetRepository;
import org.example.mozika.repositories.LikeRepository;
import org.example.mozika.repositories.SongRepository;
import org.example.mozika.repositories.UserRepository;
import org.example.mozika.services.interfaces.FavorisService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultFavorisService implements FavorisService {

    private static final String TARGET_SONG   = "SONG";
    private static final String TARGET_ALBUM  = "ALBUM";
    private static final String TARGET_ARTIST = "ARTIST";

    private final LikeRepository               likeRepository;
    private final InteractionTargetRepository  interactionTargetRepository;
    private final UserRepository               userRepository;
    private final SongRepository               songRepository;
    private final AlbumRepository              albumRepository;
    private final ArtistRepository             artistRepository;

    public DefaultFavorisService(
            LikeRepository likeRepository,
            InteractionTargetRepository interactionTargetRepository,
            UserRepository userRepository,
            SongRepository songRepository,
            AlbumRepository albumRepository,
            ArtistRepository artistRepository) {
        this.likeRepository              = likeRepository;
        this.interactionTargetRepository = interactionTargetRepository;
        this.userRepository              = userRepository;
        this.songRepository              = songRepository;
        this.albumRepository             = albumRepository;
        this.artistRepository            = artistRepository;
    }

    // ── Utilitaires privés ────────────────────────────────────────────────────

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
    }

    private InteractionTarget resolveTarget(String name) {
        return interactionTargetRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Type de cible introuvable : " + name +
                        ". Exécutez MAJ_interaction_targets_favoris.sql."));
    }

    // ── Chansons ──────────────────────────────────────────────────────────────

    @Override
    public List<FavorisSongResponse> getMesFavorisChansons(String email) {
        User user   = resolveUser(email);
        InteractionTarget target = resolveTarget(TARGET_SONG);
        return likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargets(user, target)
                .stream()
                .map(like -> songRepository.findById(like.getTargetId())
                        .map(song -> new FavorisSongResponse(like.getId(), song, like.getCreatedAt()))
                        .orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    @Override
    public FavorisSongResponse aimerChanson(String email, Long songId) {
        User user   = resolveUser(email);
        Song song   = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Chanson introuvable : " + songId));
        InteractionTarget target = resolveTarget(TARGET_SONG);

        Optional<Like> existing = likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargetsAndTargetId(user, target, songId);
        if (existing.isPresent()) {
            Like like = existing.get();
            return new FavorisSongResponse(like.getId(), song, like.getCreatedAt());
        }

        Like like = new Like();
        like.setUseridUsers(user);
        like.setTargettypeidInteractionTargets(target);
        like.setTargetId(songId);
        like.setCreatedAt(LocalDateTime.now());
        try {
            like = likeRepository.save(like);
        } catch (Exception ex) {
            throw new InternalServerErrorException("Erreur lors de l'ajout aux favoris.", ex);
        }
        return new FavorisSongResponse(like.getId(), song, like.getCreatedAt());
    }

    @Override
    public void retirerChansonDesFavoris(String email, Long songId) {
        User user   = resolveUser(email);
        InteractionTarget target = resolveTarget(TARGET_SONG);
        likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargetsAndTargetId(user, target, songId)
                .ifPresent(like -> likeRepository.deleteById(like.getId()));
    }

    @Override
    public boolean estFavoriChanson(String email, Long songId) {
        User user   = resolveUser(email);
        InteractionTarget target = resolveTarget(TARGET_SONG);
        return likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargetsAndTargetId(user, target, songId)
                .isPresent();
    }

    // ── Albums ────────────────────────────────────────────────────────────────

    @Override
    public List<FavorisAlbumResponse> getMesFavorisAlbums(String email) {
        User user   = resolveUser(email);
        InteractionTarget target = resolveTarget(TARGET_ALBUM);
        return likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargets(user, target)
                .stream()
                .map(like -> albumRepository.findById(like.getTargetId())
                        .map(album -> new FavorisAlbumResponse(like.getId(), album, like.getCreatedAt()))
                        .orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    @Override
    public FavorisAlbumResponse aimerAlbum(String email, Long albumId) {
        User user   = resolveUser(email);
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album introuvable : " + albumId));
        InteractionTarget target = resolveTarget(TARGET_ALBUM);

        Optional<Like> existing = likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargetsAndTargetId(user, target, albumId);
        if (existing.isPresent()) {
            Like like = existing.get();
            return new FavorisAlbumResponse(like.getId(), album, like.getCreatedAt());
        }

        Like like = new Like();
        like.setUseridUsers(user);
        like.setTargettypeidInteractionTargets(target);
        like.setTargetId(albumId);
        like.setCreatedAt(LocalDateTime.now());
        try {
            like = likeRepository.save(like);
        } catch (Exception ex) {
            throw new InternalServerErrorException("Erreur lors de l'ajout aux favoris.", ex);
        }
        return new FavorisAlbumResponse(like.getId(), album, like.getCreatedAt());
    }

    @Override
    public void retirerAlbumDesFavoris(String email, Long albumId) {
        User user   = resolveUser(email);
        InteractionTarget target = resolveTarget(TARGET_ALBUM);
        likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargetsAndTargetId(user, target, albumId)
                .ifPresent(like -> likeRepository.deleteById(like.getId()));
    }

    @Override
    public boolean estFavoriAlbum(String email, Long albumId) {
        User user   = resolveUser(email);
        InteractionTarget target = resolveTarget(TARGET_ALBUM);
        return likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargetsAndTargetId(user, target, albumId)
                .isPresent();
    }

    // ── Artistes ──────────────────────────────────────────────────────────────

    @Override
    public List<FavorisArtisteResponse> getMesFavorisArtistes(String email) {
        User user   = resolveUser(email);
        InteractionTarget target = resolveTarget(TARGET_ARTIST);
        return likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargets(user, target)
                .stream()
                .map(like -> artistRepository.findById(like.getTargetId())
                        .map(artist -> new FavorisArtisteResponse(like.getId(), artist, like.getCreatedAt()))
                        .orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    @Override
    public FavorisArtisteResponse aimerArtiste(String email, Long artistId) {
        User user     = resolveUser(email);
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artiste introuvable : " + artistId));
        InteractionTarget target = resolveTarget(TARGET_ARTIST);

        Optional<Like> existing = likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargetsAndTargetId(user, target, artistId);
        if (existing.isPresent()) {
            Like like = existing.get();
            return new FavorisArtisteResponse(like.getId(), artist, like.getCreatedAt());
        }

        Like like = new Like();
        like.setUseridUsers(user);
        like.setTargettypeidInteractionTargets(target);
        like.setTargetId(artistId);
        like.setCreatedAt(LocalDateTime.now());
        try {
            like = likeRepository.save(like);
        } catch (Exception ex) {
            throw new InternalServerErrorException("Erreur lors de l'ajout aux favoris.", ex);
        }
        return new FavorisArtisteResponse(like.getId(), artist, like.getCreatedAt());
    }

    @Override
    public void retirerArtisteDesFavoris(String email, Long artistId) {
        User user   = resolveUser(email);
        InteractionTarget target = resolveTarget(TARGET_ARTIST);
        likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargetsAndTargetId(user, target, artistId)
                .ifPresent(like -> likeRepository.deleteById(like.getId()));
    }

    @Override
    public boolean estFavoriArtiste(String email, Long artistId) {
        User user   = resolveUser(email);
        InteractionTarget target = resolveTarget(TARGET_ARTIST);
        return likeRepository
                .findByUseridUsersAndTargettypeidInteractionTargetsAndTargetId(user, target, artistId)
                .isPresent();
    }
}
