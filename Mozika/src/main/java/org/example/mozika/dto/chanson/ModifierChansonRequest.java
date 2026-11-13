package org.example.mozika.dto.chanson;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * Completion d'une chanson deja declaree.
 *
 * Tous les champs sont facultatifs, et c'est le point : l'artiste declare sa
 * chanson avec ce qu'il a, puis ajoute l'audio, l'album ou la date de sortie
 * quand ils existent.
 *
 * Un champ absent du corps est laisse tel quel ; un champ present a la valeur
 * vide efface la donnee. Sans cette distinction, completer un seul champ
 * effacerait tous les autres.
 */
public class ModifierChansonRequest {

    @Size(max = 150, message = "Le titre ne peut pas depasser 150 caracteres")
    private String titre;

    private String paroles;
    private String solfa;

    @Size(max = 10)
    private String langue;

    private String urlAudio;
    private String urlVideo;

    /** Chemins rendus par l'envoi de fichier, comme urlAudio. */
    @Size(max = 255)
    private String urlKaraoke;

    @Size(max = 255)
    private String urlPlayback;

    @Size(max = 255)
    private String auteurCompositeur;

    @Size(max = 255)
    private String arrangeur;

    /**
     * La base refuse une duree negative ou nulle : contrainte ck_songs_duration.
     * On la rejette ici pour rendre un 400 lisible plutot qu'une erreur SQL.
     */
    @Positive(message = "La duree doit etre superieure a zero")
    private Integer dureeSecondes;

    private LocalDate dateSortie;
    private Long idAlbum;

    /**
     * Les genres, avec la meme convention que les autres champs : absent du
     * corps, on n'y touche pas ; liste vide, l'artiste retire tous les genres.
     */
    private List<Long> idsGenres;

    /**
     * Les trois effacements explicites.
     *
     * L'album, la date de sortie et la duree ne sont pas du texte : il
     * n'existe pas de « chaine vide » pour dire « enleve-le ». Avec la
     * seule convention null = inchange, ces trois champs pouvaient etre
     * renseignes mais JAMAIS retires : un album choisi par erreur restait
     * pour toujours, et la chanson figurait definitivement dedans.
     *
     * Un drapeau plutot qu'une valeur sentinelle comme 0 ou -1 : une
     * sentinelle se confond avec une vraie valeur le jour ou le domaine
     * change, et elle ne se lit pas dans le corps de la requete.
     *
     * L'effacement l'emporte sur la valeur si les deux arrivent. Le
     * mobile n'envoie jamais les deux.
     */
    private Boolean effacerAlbum;
    private Boolean effacerDateSortie;
    private Boolean effacerDuree;

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getParoles() { return paroles; }
    public void setParoles(String paroles) { this.paroles = paroles; }

    public String getSolfa() { return solfa; }
    public void setSolfa(String solfa) { this.solfa = solfa; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public String getUrlAudio() { return urlAudio; }
    public void setUrlAudio(String urlAudio) { this.urlAudio = urlAudio; }

    public String getUrlVideo() { return urlVideo; }
    public void setUrlVideo(String urlVideo) { this.urlVideo = urlVideo; }

    public String getUrlKaraoke() { return urlKaraoke; }
    public void setUrlKaraoke(String urlKaraoke) { this.urlKaraoke = urlKaraoke; }

    public String getUrlPlayback() { return urlPlayback; }
    public void setUrlPlayback(String urlPlayback) { this.urlPlayback = urlPlayback; }

    public String getAuteurCompositeur() { return auteurCompositeur; }
    public void setAuteurCompositeur(String auteurCompositeur) { this.auteurCompositeur = auteurCompositeur; }

    public String getArrangeur() { return arrangeur; }
    public void setArrangeur(String arrangeur) { this.arrangeur = arrangeur; }

    public Integer getDureeSecondes() { return dureeSecondes; }
    public void setDureeSecondes(Integer dureeSecondes) { this.dureeSecondes = dureeSecondes; }

    public LocalDate getDateSortie() { return dateSortie; }
    public void setDateSortie(LocalDate dateSortie) { this.dateSortie = dateSortie; }

    public Long getIdAlbum() { return idAlbum; }
    public void setIdAlbum(Long idAlbum) { this.idAlbum = idAlbum; }

    public List<Long> getIdsGenres() { return idsGenres; }
    public void setIdsGenres(List<Long> idsGenres) { this.idsGenres = idsGenres; }

    public Boolean getEffacerAlbum() { return effacerAlbum; }
    public void setEffacerAlbum(Boolean effacerAlbum) { this.effacerAlbum = effacerAlbum; }

    public Boolean getEffacerDateSortie() { return effacerDateSortie; }
    public void setEffacerDateSortie(Boolean effacerDateSortie) {
        this.effacerDateSortie = effacerDateSortie;
    }

    public Boolean getEffacerDuree() { return effacerDuree; }
    public void setEffacerDuree(Boolean effacerDuree) { this.effacerDuree = effacerDuree; }
}
