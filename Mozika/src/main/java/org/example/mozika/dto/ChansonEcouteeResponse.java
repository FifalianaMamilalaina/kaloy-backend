package org.example.mozika.dto;

/**
 * Une chanson et son nombre d'ecoutes, pour le classement d'un artiste.
 *
 * Construite directement par la requete JPQL, via une expression
 * « SELECT new ... ». C'est ce qui evite de renvoyer des Object[] que l'appelant
 * devrait dépaqueter par position — une erreur d'indice y passerait la
 * compilation sans bruit.
 *
 * ecoutes est un Long et non un long : COUNT renvoie un Long en JPQL, et
 * declarer un primitif ferait echouer la construction.
 */
public class ChansonEcouteeResponse {

    private Long id;
    private String titre;
    private Long ecoutes;

    public ChansonEcouteeResponse(Long id, String titre, Long ecoutes) {
        this.id = id;
        this.titre = titre;
        this.ecoutes = ecoutes;
    }

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public Long getEcoutes() {
        return ecoutes;
    }
}
