package org.example.mozika.dto.chanson;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * Declaration d'une chanson par l'artiste connecte.
 *
 * L'artiste n'est pas transmis : il est deduit du jeton. Le laisser au client
 * aurait permis de declarer une chanson au nom d'un autre.
 *
 * Une chanson n'a pas besoin de fichier audio pour exister : un texte ou un
 * solfa suffisent. La colonne storage_type_id a ete rendue nullable le
 * 30/10/2026 pour cette raison, et le @NotNull correspondant retire de
 * l'entite.
 *
 * Les champs du formulaire sont tous ici, et non repartis entre un POST puis
 * un PUT : la creation est transactionnelle, donc un echec a mi-chemin ne
 * laisse pas une chanson a moitie declaree. Seuls le titre et un contenu sont
 * exiges ; tout le reste peut etre complete plus tard par le PUT.
 *
 * Le type de stockage n'est PAS transmis. L'audio ne peut venir que d'un
 * envoi de fichier, donc le serveur sait d'ou vient le fichier : le laisser
 * au client, c'etait lui permettre d'annoncer un stockage faux.
 */
public class CreerChansonRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 150, message = "Le titre ne peut pas depasser 150 caracteres")
    private String titre;

    /** Les paroles. Facultatives SI un solfa est fourni, voir auMoinsUnContenu. */
    private String paroles;

    /** Le solfa. Facultatif SI des paroles sont fournies. */
    private String solfa;

    /**
     * Pre-rempli a « mg » : le catalogue est malgache. La colonne a « fr »
     * pour defaut en base, ce qui ne correspond pas a l'usage.
     */
    @Size(max = 10, message = "Le code de langue ne peut pas depasser 10 caracteres")
    private String langue = "mg";

    /**
     * Le chemin rendu par l'envoi de fichier, de la forme /uploads/xxx.mp3.
     *
     * Un chemin et non une adresse absolue : le nom d'hote dependrait du
     * reseau du moment, et c'est exactement le defaut qui avait rendu
     * injoignables tous les medias a chaque changement d'adresse Wi-Fi
     * (annexe § 1.10). L'application prefixe elle-meme avec sa base.
     *
     * Le @Size reste utile : la colonne est un varchar(255), et sans lui une
     * valeur plus longue sortirait en 500 au lieu d'un 400 lisible.
     */
    @Size(max = 255, message = "L'adresse audio ne peut pas depasser 255 caracteres")
    private String urlAudio;

    @Size(max = 255, message = "L'adresse video ne peut pas depasser 255 caracteres")
    private String urlVideo;

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

    /** Un album de l'artiste connecte. La propriete est verifiee dans le service. */
    private Long idAlbum;

    /**
     * Plusieurs genres par chanson : la table song_genres est une table de
     * liaison, et le catalogue en compte deja plus que de chansons. La liste
     * peut rester vide, pour ne pas empecher une declaration rapide.
     */
    private List<Long> idsGenres;

    /**
     * Au moins un contenu, sinon la chanson est une coquille vide.
     *
     * @AssertTrue plutot qu'un test dans le service : la regle est une
     * propriete de la requete, et Spring rend alors un 400 avec un message
     * clair au lieu d'une exception metier a traduire.
     */
    @AssertTrue(message = "Une chanson doit avoir au moins des paroles ou un solfa")
    public boolean isAuMoinsUnContenu() {
        return (paroles != null && !paroles.isBlank())
                || (solfa != null && !solfa.isBlank());
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getParoles() {
        return paroles;
    }

    public void setParoles(String paroles) {
        this.paroles = paroles;
    }

    public String getSolfa() {
        return solfa;
    }

    public void setSolfa(String solfa) {
        this.solfa = solfa;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public String getUrlAudio() {
        return urlAudio;
    }

    public void setUrlAudio(String urlAudio) {
        this.urlAudio = urlAudio;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public String getAuteurCompositeur() {
        return auteurCompositeur;
    }

    public void setAuteurCompositeur(String auteurCompositeur) {
        this.auteurCompositeur = auteurCompositeur;
    }

    public String getArrangeur() {
        return arrangeur;
    }

    public void setArrangeur(String arrangeur) {
        this.arrangeur = arrangeur;
    }

    public Integer getDureeSecondes() {
        return dureeSecondes;
    }

    public void setDureeSecondes(Integer dureeSecondes) {
        this.dureeSecondes = dureeSecondes;
    }

    public LocalDate getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(LocalDate dateSortie) {
        this.dateSortie = dateSortie;
    }

    public Long getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(Long idAlbum) {
        this.idAlbum = idAlbum;
    }

    public List<Long> getIdsGenres() {
        return idsGenres;
    }

    public void setIdsGenres(List<Long> idsGenres) {
        this.idsGenres = idsGenres;
    }
}
