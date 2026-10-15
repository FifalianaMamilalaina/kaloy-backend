package org.example.mozika.dto;

import java.util.List;

/**
 * Statistiques d'un artiste, telles que l'ecran mobile les affiche.
 *
 * Tout est renvoye en un seul objet, et donc en un seul appel : l'ecran montre
 * un etat coherent. Quatre appels separes donneraient quatre chargements et un
 * ecran a moitie rempli, pour aucun gain.
 *
 * Ce qui n'y figure pas, et pourquoi :
 *   - les likes et les commentaires : six et sept lignes dans toute la base au
 *     15 octobre, soit environ une par artiste. Une tuile a 1 n'apprend rien ;
 *   - les modes de lecture : 100 % des 802 ecoutes sont en mode AUDIO, la
 *     repartition serait une barre unique ;
 *   - une fenetre de 7 jours : 3 ecoutes seulement sur cette periode, contre
 *     567 sur 30 jours. La fenetre de 30 jours est la seule exploitable.
 */
public class StatistiquesArtisteResponse {

    private long ecoutes30Jours;
    private long ecoutesTotal;
    private long auditeursDistincts;
    private long abonnes;
    private long chansonsPubliees;
    private List<ChansonEcouteeResponse> topChansons;

    public StatistiquesArtisteResponse(long ecoutes30Jours, long ecoutesTotal,
                                       long auditeursDistincts, long abonnes,
                                       long chansonsPubliees,
                                       List<ChansonEcouteeResponse> topChansons) {
        this.ecoutes30Jours = ecoutes30Jours;
        this.ecoutesTotal = ecoutesTotal;
        this.auditeursDistincts = auditeursDistincts;
        this.abonnes = abonnes;
        this.chansonsPubliees = chansonsPubliees;
        this.topChansons = topChansons;
    }

    public long getEcoutes30Jours() {
        return ecoutes30Jours;
    }

    public long getEcoutesTotal() {
        return ecoutesTotal;
    }

    public long getAuditeursDistincts() {
        return auditeursDistincts;
    }

    public long getAbonnes() {
        return abonnes;
    }

    public long getChansonsPubliees() {
        return chansonsPubliees;
    }

    public List<ChansonEcouteeResponse> getTopChansons() {
        return topChansons;
    }
}
