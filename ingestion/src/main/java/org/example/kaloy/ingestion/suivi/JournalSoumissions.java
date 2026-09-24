package org.example.kaloy.ingestion.suivi;

import org.example.kaloy.ingestion.api.ClientApiKaloy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Trace de chaque fichier traite, dans content_submissions.
 *
 * Cette table existait deja dans le schema, prevue pour ce pipeline : elle
 * porte le fichier source, son type, son statut et le motif d'echec. C'est
 * elle qui rend un import verifiable apres coup, et c'est la que le
 * developpeur web ira chercher l'etat d'un depot.
 *
 * Une ecriture de journal qui echoue ne doit jamais faire echouer l'import
 * lui-meme : les erreurs sont donc seulement tracees dans les logs.
 */
@Component
public class JournalSoumissions {

    private static final Logger log = LoggerFactory.getLogger(JournalSoumissions.class);

    private final ClientApiKaloy api;

    /** Statuts resolus par leur nom, l'ordre des insertions n'etant pas garanti. */
    private Map<String, Long> statuts;

    public JournalSoumissions(ClientApiKaloy api) {
        this.api = api;
    }

    public void consignerSucces(String nomFichier, String typeFichier, Long idArtiste) {
        consigner(nomFichier, typeFichier, "SUCCESS", null, idArtiste);
    }

    public void consignerEchec(String nomFichier, String typeFichier, String motif) {
        consigner(nomFichier, typeFichier, "ERROR", motif, null);
    }

    public void consignerEchec(String nomFichier, String typeFichier, String motif, Long idArtiste) {
        consigner(nomFichier, typeFichier, "ERROR", motif, idArtiste);
    }

    private void consigner(String nomFichier, String typeFichier,
                           String statut, String motif, Long idArtiste) {
        try {
            Long idStatut = idStatut(statut);
            if (idStatut == null) {
                log.warn("Statut de soumission « {} » introuvable, trace ignoree", statut);
                return;
            }

            // artist_id est obligatoire en base. Quand l'artiste n'a pas pu
            // etre identifie — cas frequent des echecs precoces — on ne peut
            // rien ecrire : la trace reste alors dans les logs.
            if (idArtiste == null) {
                log.warn("Trace non consignee ({} / {}) : artiste inconnu. Motif : {}",
                        nomFichier, statut, motif);
                return;
            }

            Map<String, Object> corps = new LinkedHashMap<>();
            corps.put("artistidArtists", Map.of("id", idArtiste));
            corps.put("sourceFilename", nomFichier);
            corps.put("fileType", typeFichier);
            corps.put("statusidSubmissionStatuses", Map.of("id", idStatut));
            if (motif != null) corps.put("errorMessage", motif);
            corps.put("submittedAt", ClientApiKaloy.maintenant());
            corps.put("processedAt", ClientApiKaloy.maintenant());

            api.post("/contentsubmissions", corps);

        } catch (Exception e) {
            log.warn("Ecriture du journal impossible pour {} : {}", nomFichier, e.getMessage());
        }
    }

    private synchronized Long idStatut(String nom) {
        if (statuts == null) {
            statuts = new LinkedHashMap<>();
            try {
                var reponse = api.get("/submissionstatuses?page=0&size=20");
                var contenu = reponse == null ? null : reponse.path("data").path("content");
                if (contenu != null && contenu.isArray()) {
                    contenu.forEach(s -> statuts.put(s.path("name").asText(), s.path("id").asLong()));
                }
            } catch (Exception e) {
                log.warn("Statuts de soumission illisibles : {}", e.getMessage());
            }
        }
        return statuts.get(nom);
    }
}
