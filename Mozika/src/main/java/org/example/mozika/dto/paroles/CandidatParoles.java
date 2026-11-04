package org.example.mozika.dto.paroles;

/**
 * Une chanson trouvee sur le site de paroles.
 *
 * Volontairement SANS le texte : la recherche renvoie des references, et le
 * texte n'est telecharge qu'une fois le candidat choisi. Une recherche large
 * remonte une vingtaine de resultats ; les telecharger tous pour n'en garder
 * qu'un serait vingt pages demandees pour rien au site.
 */
public class CandidatParoles {

    private final String titre;
    private final String artiste;
    private final String url;

    /**
     * Ressemblance entre ce que l'artiste a saisi et ce qui a ete trouve,
     * entre 0 et 1.
     *
     * Le tri seul ne suffit pas a decider : « Malagasy » ou « Veloma » sont des
     * mots courants en malgache et remontent une vingtaine de chansons
     * d'artistes sans rapport. Le score aide a presenter les plus probables en
     * premier, mais c'est l'artiste qui tranche.
     */
    private final double score;

    public CandidatParoles(String titre, String artiste, String url, double score) {
        this.titre = titre;
        this.artiste = artiste;
        this.url = url;
        this.score = score;
    }

    public String getTitre() { return titre; }
    public String getArtiste() { return artiste; }
    public String getUrl() { return url; }
    public double getScore() { return score; }
}
