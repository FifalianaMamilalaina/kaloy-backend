package org.example.mozika.dto.paroles;

/**
 * Le texte d'un candidat, pour apercu avant que l'artiste le retienne.
 *
 * L'apercu n'enregistre rien : l'artiste relit, corrige au besoin, et c'est
 * l'appel a /mes-chansons qui decide ce qui finit en base.
 *
 * La source est renvoyee pour que l'ecran puisse l'afficher. Un texte repris
 * d'ailleurs doit porter sa provenance.
 */
public class ParolesTrouvees {

    private final String titre;
    private final String artiste;
    private final String url;
    private final String texte;
    private final int lignes;

    public ParolesTrouvees(String titre, String artiste, String url, String texte, int lignes) {
        this.titre = titre;
        this.artiste = artiste;
        this.url = url;
        this.texte = texte;
        this.lignes = lignes;
    }

    public String getTitre() { return titre; }
    public String getArtiste() { return artiste; }
    public String getUrl() { return url; }
    public String getTexte() { return texte; }
    public int getLignes() { return lignes; }
}
