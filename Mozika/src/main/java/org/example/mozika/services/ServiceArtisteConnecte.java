package org.example.mozika.services;

import org.example.mozika.models.Artist;
import org.example.mozika.models.User;
import org.example.mozika.repositories.ArtistRepository;
import org.example.mozika.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Resolution de l'artiste rattache au compte connecte.
 *
 * Extrait en service a part parce que deux fonctionnalites en dependent —
 * repondre a une invitation et organiser un evenement — et qu'il ne doit
 * exister qu'une seule definition de « quel artiste est ce jeton ». Deux
 * implementations divergentes seraient une faille de securite en puissance.
 */
@Service
public class ServiceArtisteConnecte {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    public ServiceArtisteConnecte(UserRepository userRepository, ArtistRepository artistRepository) {
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
    }

    /**
     * Artiste du compte connecte, ou 403.
     *
     * Un compte de role ARTIST sans ligne dans artists existe en base (le compte
     * de developpement artist@dev.com) : on renvoie un refus explicite plutot
     * que de laisser remonter une erreur technique.
     */
    public Artist resoudre(String email) {
        User utilisateur = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Compte introuvable"));

        List<Artist> artistes = artistRepository.findByUseridUsers(utilisateur);
        if (artistes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ce compte n'est rattache a aucun profil artiste");
        }
        return artistes.get(0);
    }
}
