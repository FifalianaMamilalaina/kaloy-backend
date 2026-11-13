package org.example.mozika.services;

import org.example.mozika.dto.chanson.ChansonAModifierResponse;
import org.example.mozika.dto.chanson.CreerChansonRequest;
import org.example.mozika.dto.chanson.ModifierChansonRequest;
import org.example.mozika.dto.chanson.OptionsChansonResponse;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.Album;
import org.example.mozika.models.Artist;
import org.example.mozika.models.AudioStorageType;
import org.example.mozika.models.Genre;
import org.example.mozika.models.Song;
import org.example.mozika.models.SongGenre;
import org.example.mozika.repositories.AlbumRepository;
import org.example.mozika.repositories.AudioStorageTypeRepository;
import org.example.mozika.repositories.GenreRepository;
import org.example.mozika.repositories.SongGenreRepository;
import org.example.mozika.repositories.SongRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Chansons declarees par l'artiste connecte.
 *
 * Comme pour les evenements, l'artiste n'apparait jamais dans l'URL : il est
 * deduit du jeton. Un identifiant en parametre aurait permis de declarer une
 * chanson — ou d'en modifier une — au nom d'un autre.
 *
 * Une chanson peut exister sans aucun fichier audio : un texte ou un solfa
 * suffisent. C'est la raison pour laquelle storage_type_id est nullable depuis
 * le 30/10/2026.
 */
@Service
public class ServiceMesChansons {

    /** Les langues proposees. Fermee : voir OptionsChansonResponse. */
    private static final List<String> LANGUES = List.of("mg", "fr", "en");

    /** Le type porte par un fichier envoye puis servi par nous. */
    private static final String TYPE_FICHIER_ENVOYE = "EXTERNAL_LINK";

    private final SongRepository chansonRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final SongGenreRepository songGenreRepository;
    private final AudioStorageTypeRepository typeStockageRepository;
    private final ServiceArtisteConnecte serviceArtisteConnecte;

    public ServiceMesChansons(SongRepository chansonRepository,
                              AlbumRepository albumRepository,
                              GenreRepository genreRepository,
                              SongGenreRepository songGenreRepository,
                              AudioStorageTypeRepository typeStockageRepository,
                              ServiceArtisteConnecte serviceArtisteConnecte) {
        this.chansonRepository = chansonRepository;
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
        this.songGenreRepository = songGenreRepository;
        this.typeStockageRepository = typeStockageRepository;
        this.serviceArtisteConnecte = serviceArtisteConnecte;
    }

    /** Les listes que le formulaire de declaration doit proposer. */
    public OptionsChansonResponse options(String emailConnecte) {
        Artist artiste = serviceArtisteConnecte.resoudre(emailConnecte);
        return new OptionsChansonResponse(
                albumRepository.findByArtistidArtists(artiste).stream()
                        .map(album -> new OptionsChansonResponse.Option(
                                album.getId(), album.getTitle()))
                        .toList(),
                genreRepository.findAll().stream()
                        .map(genre -> new OptionsChansonResponse.Option(
                                genre.getId(), genre.getName()))
                        .toList(),
                typeStockageRepository.findAll().stream()
                        .map(type -> new OptionsChansonResponse.Option(
                                type.getId(), type.getName()))
                        .toList(),
                LANGUES,
                chansonRepository.listerAuteursCompositeurs(),
                chansonRepository.listerArrangeurs());
    }

    /** Mes chansons, de la plus recente a la plus ancienne. */
    public List<Song> mesChansons(String emailConnecte) {
        Artist artiste = serviceArtisteConnecte.resoudre(emailConnecte);
        return chansonRepository.findByArtistidArtists(artiste).stream()
                .sorted(Comparator.comparing(
                        Song::getId, Comparator.reverseOrder()))
                .toList();
    }

    @Transactional
    /**
     * L'etat d'une chanson tel que le formulaire de modification le
     * pre-remplit.
     *
     * Un appel a part, et non la liste : GET /mes-chansons renvoie des
     * entites Song, et Song NE PORTE PAS ses genres -- ils vivent dans
     * song_genres, sans relation declaree. Le formulaire se serait ouvert
     * en affichant « aucun genre », puis l'enregistrement aurait envoye une
     * liste vide, et remplacerGenres() efface TOUS les liens avant de
     * reinserer. Chaque modification aurait donc supprime les genres sans
     * la moindre erreur : une perte silencieuse.
     *
     * maChanson() verifie au passage que la chanson appartient bien a
     * l'artiste connecte, comme pour la modification et la suppression.
     */
    public ChansonAModifierResponse chansonAModifier(String emailConnecte, Long idChanson) {
        Song chanson = maChanson(emailConnecte, idChanson);

        List<Long> idsGenres = songGenreRepository.findBySongidSongs(chanson).stream()
                .map(lien -> lien.getGenreidGenres().getId())
                .toList();

        return new ChansonAModifierResponse(
                chanson.getId(),
                chanson.getTitle(),
                chanson.getLyrics(),
                chanson.getSolfa(),
                chanson.getLanguage(),
                chanson.getAudioUrl(),
                chanson.getVideoUrl(),
                chanson.getKaraokeAudioUrl(),
                chanson.getPlaybackUrl(),
                chanson.getAuthorComposer(),
                chanson.getMusicalArranger(),
                chanson.getDurationSeconds(),
                chanson.getReleaseDate(),
                chanson.getAlbumidAlbums() == null ? null : chanson.getAlbumidAlbums().getId(),
                idsGenres);
    }

    public Song creer(String emailConnecte, CreerChansonRequest requete) {
        Artist artiste = serviceArtisteConnecte.resoudre(emailConnecte);

        Song chanson = new Song();
        chanson.setArtistidArtists(artiste);
        chanson.setTitle(requete.getTitre().trim());
        chanson.setLanguage(langue(requete.getLangue()));
        chanson.setLyrics(normaliser(requete.getParoles()));
        // setSolfa et non setSolfaUrl : le solfa saisi est du texte, et
        // solfa_url est un varchar(255). Voir le commentaire dans Song.java.
        chanson.setSolfa(normaliser(requete.getSolfa()));
        chanson.setAudioUrl(normaliser(requete.getUrlAudio()));
        chanson.setVideoUrl(normaliser(requete.getUrlVideo()));
        chanson.setKaraokeAudioUrl(normaliser(requete.getUrlKaraoke()));
        chanson.setPlaybackUrl(normaliser(requete.getUrlPlayback()));
        chanson.setAuthorComposer(normaliser(requete.getAuteurCompositeur()));
        chanson.setMusicalArranger(normaliser(requete.getArrangeur()));
        chanson.setDurationSeconds(requete.getDureeSecondes());
        chanson.setReleaseDate(requete.getDateSortie());

        if (requete.getIdAlbum() != null) {
            chanson.setAlbumidAlbums(monAlbum(artiste, requete.getIdAlbum()));
        }

        // Deduit de l'audio, et non transmis par le client : le fichier ne
        // peut venir que d'un envoi, donc nous savons d'ou il vient.
        chanson.setStoragetypeidAudioStorageTypes(
                typeStockagePour(chanson.getAudioUrl()));
        chanson.setIsDownloadable(Boolean.FALSE);

        // Renseigne ici et non laisse au defaut SQL : Hibernate envoie la
        // colonne explicitement, donc DEFAULT NOW() ne joue jamais, et la
        // validation @NotNull de l'entite rejette un createdAt vide avant
        // meme l'insertion.
        chanson.setCreatedAt(java.time.LocalDateTime.now());

        // Les genres apres le save : la table de liaison a besoin de
        // l'identifiant de la chanson, qui n'existe pas avant l'insertion.
        Song enregistree = chansonRepository.save(chanson);
        remplacerGenres(enregistree, requete.getIdsGenres());
        return enregistree;
    }

    @Transactional
    public Song modifier(String emailConnecte, Long idChanson, ModifierChansonRequest requete) {
        Song chanson = maChanson(emailConnecte, idChanson);

        // null : le champ n'est pas dans le corps, on n'y touche pas.
        // chaine vide : l'artiste efface volontairement la donnee.
        if (requete.getTitre() != null) {
            if (requete.getTitre().isBlank()) {
                throw new IllegalArgumentException("Le titre ne peut pas etre efface");
            }
            chanson.setTitle(requete.getTitre().trim());
        }
        if (requete.getParoles() != null) chanson.setLyrics(normaliser(requete.getParoles()));
        if (requete.getSolfa() != null) chanson.setSolfa(normaliser(requete.getSolfa()));
        if (requete.getLangue() != null) chanson.setLanguage(langue(requete.getLangue()));
        if (requete.getUrlAudio() != null) chanson.setAudioUrl(normaliser(requete.getUrlAudio()));
        if (requete.getUrlVideo() != null) chanson.setVideoUrl(normaliser(requete.getUrlVideo()));
        if (requete.getUrlKaraoke() != null) {
            chanson.setKaraokeAudioUrl(normaliser(requete.getUrlKaraoke()));
        }
        if (requete.getUrlPlayback() != null) {
            chanson.setPlaybackUrl(normaliser(requete.getUrlPlayback()));
        }
        if (requete.getAuteurCompositeur() != null) {
            chanson.setAuthorComposer(normaliser(requete.getAuteurCompositeur()));
        }
        if (requete.getArrangeur() != null) {
            chanson.setMusicalArranger(normaliser(requete.getArrangeur()));
        }
        // Les trois champs non textuels. L'effacement l'emporte sur la
        // valeur : sans ces branches, l'album, la date et la duree pouvaient
        // etre poses mais jamais retires, faute de « chaine vide » pour dire
        // « enleve-le ». Un album choisi par erreur restait pour toujours.
        if (Boolean.TRUE.equals(requete.getEffacerDuree())) {
            chanson.setDurationSeconds(null);
        } else if (requete.getDureeSecondes() != null) {
            chanson.setDurationSeconds(requete.getDureeSecondes());
        }

        if (Boolean.TRUE.equals(requete.getEffacerDateSortie())) {
            chanson.setReleaseDate(null);
        } else if (requete.getDateSortie() != null) {
            chanson.setReleaseDate(requete.getDateSortie());
        }

        if (Boolean.TRUE.equals(requete.getEffacerAlbum())) {
            chanson.setAlbumidAlbums(null);
        } else if (requete.getIdAlbum() != null) {
            chanson.setAlbumidAlbums(monAlbum(chanson.getArtistidArtists(), requete.getIdAlbum()));
        }
        // Le type suit l'audio dans tous les cas : si l'artiste ajoute un
        // fichier, il est renseigne ; s'il l'efface, il repart a nul. Aucun
        // chemin ne laisse donc d'adresse audio sans type, ni l'inverse.
        if (requete.getUrlAudio() != null) {
            chanson.setStoragetypeidAudioStorageTypes(
                    typeStockagePour(chanson.getAudioUrl()));
        }

        // La regle de creation reste vraie apres modification : on ne doit pas
        // pouvoir vider une chanson de tout contenu en deux appels.
        //
        // Le karaoke et le playback comptent comme contenu : une chanson qui
        // n'a plus que son karaoke n'est pas vide, et refuser la suppression
        // des paroles dans ce cas serait arbitraire.
        if (vide(chanson.getLyrics()) && vide(chanson.getSolfa())
                && vide(chanson.getAudioUrl()) && vide(chanson.getVideoUrl())
                && vide(chanson.getKaraokeAudioUrl()) && vide(chanson.getPlaybackUrl())) {
            throw new IllegalArgumentException(
                    "Une chanson doit garder au moins des paroles, un solfa ou un media");
        }

        if (requete.getIdsGenres() != null) {
            remplacerGenres(chanson, requete.getIdsGenres());
        }

        return chansonRepository.save(chanson);
    }

    @Transactional
    public void supprimer(String emailConnecte, Long idChanson) {
        Song chanson = maChanson(emailConnecte, idChanson);
        // Les liens de genre d'abord : song_genres.song_id est une cle
        // etrangere en NO ACTION, donc PostgreSQL refuse d'effacer une
        // chanson qui en porte encore. Avant que la creation accepte les
        // genres, le cas ne se presentait pas.
        songGenreRepository.deleteAll(songGenreRepository.findBySongidSongs(chanson));
        chansonRepository.delete(chanson);
    }

    /**
     * La chanson, a condition qu'elle m'appartienne.
     *
     * Le controle de propriete est ici et non dans le controleur : toute
     * methode qui modifie passe par lui, et on ne peut pas l'oublier en
     * ajoutant un endpoint.
     */
    private Song maChanson(String emailConnecte, Long idChanson) {
        Artist artiste = serviceArtisteConnecte.resoudre(emailConnecte);
        Song chanson = chansonRepository.findById(idChanson)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chanson introuvable avec l'id : " + idChanson));

        Artist proprietaire = chanson.getArtistidArtists();
        if (proprietaire == null || !proprietaire.getId().equals(artiste.getId())) {
            // 403 et non 404 : la chanson existe, elle n'est simplement pas a
            // l'appelant. Un 404 serait plus discret mais trompeur en debogage.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Cette chanson ne vous appartient pas");
        }
        return chanson;
    }

    /** L'album vise, a condition qu'il soit du meme artiste. */
    private Album monAlbum(Artist artiste, Long idAlbum) {
        Album album = albumRepository.findById(idAlbum)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album introuvable avec l'id : " + idAlbum));
        if (album.getArtistidArtists() == null
                || !album.getArtistidArtists().getId().equals(artiste.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Cet album ne vous appartient pas");
        }
        return album;
    }

    /**
     * Les genres de la chanson, mis a jour PAR DIFFERENCE.
     *
     * On ne touche qu'a ce qui change : les liens retires sont supprimes,
     * les nouveaux ajoutes, et ceux qui restent ne sont pas reecrits.
     *
     * La version precedente effacait tout puis reinserait, au motif qu'une
     * difference aurait ete du code en plus pour la meme ecriture. Elle se
     * trompait sur deux points.
     *
     * D'abord song_genres PORTE bien une contrainte d'unicite sur le couple
     * (song_id, genre_id) : l'index uq_song_genres_song_genre, cree par le
     * script de reference. Le commentaire affirmait le contraire.
     *
     * Ensuite Hibernate n'execute pas les ordres dans celui ou on les ecrit :
     * sa file d'actions passe les INSERT AVANT les DELETE. L'insertion d'un
     * genre inchange arrivait donc alors que l'ancienne ligne etait encore
     * la, et l'index la refusait. Reenregistrer une chanson sans changer ses
     * genres echouait ainsi, precisement parce qu'ils n'avaient pas change.
     *
     * Travailler par difference supprime le probleme au lieu de le
     * contourner : suppressions et insertions portent sur des genres
     * disjoints, donc aucun ordre d'execution ne peut plus les faire entrer
     * en collision. Et le cas courant -- des genres inchanges -- n'ecrit
     * plus rien du tout.
     */
    private void remplacerGenres(Song chanson, List<Long> idsGenres) {
        List<SongGenre> existants = songGenreRepository.findBySongidSongs(chanson);
        Set<Long> actuels = existants.stream()
                .map(lien -> lien.getGenreidGenres().getId())
                .collect(Collectors.toSet());

        // Un Set, donc la meme valeur envoyee deux fois ne produit qu'un lien.
        Set<Long> voulus = idsGenres == null
                ? Set.of()
                : idsGenres.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        for (SongGenre lien : existants) {
            if (!voulus.contains(lien.getGenreidGenres().getId())) {
                songGenreRepository.delete(lien);
            }
        }

        for (Long idGenre : voulus) {
            if (actuels.contains(idGenre)) {
                continue;
            }
            Genre genre = genreRepository.findById(idGenre)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Genre introuvable avec l'id : " + idGenre));
            SongGenre lien = new SongGenre();
            lien.setSongidSongs(chanson);
            lien.setGenreidGenres(genre);
            songGenreRepository.save(lien);
        }
    }

    /**
     * Le type de stockage qui correspond a une adresse audio, ou null.
     *
     * EXTERNAL_LINK, et non DATABASE : le fichier est sur le disque du
     * serveur et relu par GET /uploads/{nom}, il n'est pas dans la base. Des
     * deux valeurs existantes c'est la seule qui ne soit pas fausse, et c'est
     * deja celle que porte la seule chanson du catalogue ayant un audio.
     *
     * Null quand il n'y a pas d'audio : renseigner un type sans fichier
     * annoncerait un contenu qui n'existe pas.
     */
    private AudioStorageType typeStockagePour(String urlAudio) {
        if (vide(urlAudio)) {
            return null;
        }
        return typeStockageRepository.findAll().stream()
                .filter(type -> TYPE_FICHIER_ENVOYE.equals(type.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Type de stockage " + TYPE_FICHIER_ENVOYE + " absent de la base"));
    }

    /**
     * La langue, en minuscules et restreinte aux valeurs proposees.
     *
     * La base contient « mg » et « MG », soit deux valeurs pour la meme
     * langue, qu'un filtre par langue compte separement. On normalise donc a
     * l'ecriture, et on refuse ce qui sort de la liste plutot que de laisser
     * s'ajouter une troisieme graphie.
     */
    private static String langue(String valeur) {
        if (vide(valeur)) {
            return "mg";
        }
        String propre = valeur.trim().toLowerCase();
        if (!LANGUES.contains(propre)) {
            throw new IllegalArgumentException(
                    "Langue non prise en charge : " + valeur);
        }
        return propre;
    }

    /** Une chaine vide vaut absence : on evite les champs « blancs » en base. */
    private static String normaliser(String valeur) {
        if (valeur == null) return null;
        String propre = valeur.trim();
        return propre.isEmpty() ? null : propre;
    }

    private static boolean vide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
