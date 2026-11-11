package org.example.mozika.dto.chanson;

import java.util.List;

/**
 * Tout ce que le formulaire de declaration doit proposer en liste.
 *
 * Un seul appel et non trois : les endpoints generes /albums, /genres et
 * /audiostoragetypes existent, mais /albums renvoie les albums de TOUS les
 * artistes. Filtrer cote mobile serait faux — rien n'empeche alors de
 * rattacher sa chanson a l'album d'un autre, et le refus n'arriverait qu'a
 * l'enregistrement. Ici la liste est deja restreinte a l'artiste connecte.
 *
 * Les langues ne viennent d'aucune table : language est une colonne texte. La
 * liste est donc ecrite en dur, et fermee volontairement — la base contient
 * aujourd'hui « mg » trente fois et « MG » une fois, soit deux valeurs pour la
 * meme langue, qu'un filtre par langue compte separement.
 *
 * Les auteurs-compositeurs et les arrangeurs sont relevees de la meme facon,
 * mais la liste n'est PAS fermee : elle propose ce qui existe deja et le
 * formulaire autorise un nom nouveau. Une liste stricte serait restee vide
 * pour l'arrangeur, nul sur les 31 chansons au 03/11/2026, et personne
 * n'aurait jamais pu y ajouter le premier nom.
 *
 * typesStockage n'est plus propose a l'artiste : l'audio ne peut venir que
 * d'un envoi de fichier, donc le serveur sait d'ou vient le fichier et fixe
 * la valeur lui-meme. La liste reste renvoyee parce qu'elle est bon marche et
 * qu'un ecran d'administration en aura l'usage.
 */
public record OptionsChansonResponse(
        List<Option> albums,
        List<Option> genres,
        List<Option> typesStockage,
        List<String> langues,
        List<String> auteursCompositeurs,
        List<String> arrangeurs) {

    /** Un element de liste : ce qui part en base, et ce qui s'affiche. */
    public record Option(Long id, String libelle) {
    }
}
