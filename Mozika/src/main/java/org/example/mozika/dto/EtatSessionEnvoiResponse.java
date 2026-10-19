package org.example.mozika.dto;

import java.util.List;

/**
 * Etat d'une session d'envoi : c'est ce qui permet la reprise.
 *
 * Le client interroge cette route avant de reprendre un envoi interrompu, et
 * saute les morceaux deja recus. L'etat n'est pas tenu en memoire : il est
 * deduit des fichiers presents sur le disque, ce qui le fait survivre a un
 * redemarrage du serveur — sans quoi une reprise apres redemarrage
 * recommencerait de zero.
 */
public class EtatSessionEnvoiResponse {

    private String idSession;
    private long tailleTotale;
    private int tailleMorceau;
    private int morceauxAttendus;

    /** Indices deja recus. Le client envoie les autres, dans n'importe quel ordre. */
    private List<Integer> morceauxRecus;

    private boolean complet;

    public EtatSessionEnvoiResponse(String idSession, long tailleTotale, int tailleMorceau,
                                    int morceauxAttendus, List<Integer> morceauxRecus,
                                    boolean complet) {
        this.idSession = idSession;
        this.tailleTotale = tailleTotale;
        this.tailleMorceau = tailleMorceau;
        this.morceauxAttendus = morceauxAttendus;
        this.morceauxRecus = morceauxRecus;
        this.complet = complet;
    }

    public String getIdSession() {
        return idSession;
    }

    public long getTailleTotale() {
        return tailleTotale;
    }

    public int getTailleMorceau() {
        return tailleMorceau;
    }

    public int getMorceauxAttendus() {
        return morceauxAttendus;
    }

    public List<Integer> getMorceauxRecus() {
        return morceauxRecus;
    }

    public boolean isComplet() {
        return complet;
    }
}
