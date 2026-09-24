package org.example.kaloy.ingestion.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Comparaison tolerante de libelles saisis a la main.
 *
 * Un nom d'artiste, un titre de chanson ou un en-tete de colonne Excel
 * arrivent avec des accents, des majuscules et des separateurs variables.
 * Comparer les chaines brutes ferait echouer des rapprochements evidents
 * (« Samoela » et « samoela », « Date de sortie » et « date_de_sortie »).
 */
public final class Textes {

    private Textes() {
    }

    /** Minuscules, sans accents ni separateurs : « Tsy Very » -> « tsyvery ». */
    public static String normaliser(String valeur) {
        if (valeur == null) return "";
        String sansAccents = Normalizer.normalize(valeur, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sansAccents.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    /** « Rossy_TsyVery.mp3 » -> « Rossy_TsyVery ». */
    public static String sansExtension(String nom) {
        if (nom == null) return "";
        int point = nom.lastIndexOf('.');
        return point > 0 ? nom.substring(0, point) : nom;
    }
}
