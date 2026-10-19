package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.mozika.dto.CreerSessionEnvoiRequest;
import org.example.mozika.dto.EtatSessionEnvoiResponse;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.services.ServiceEnvoiParMorceaux;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Envoi d'un fichier volumineux par morceaux, avec reprise.
 *
 * POST /uploads reste en place pour les petits fichiers — une photo de 3 Mo n'a
 * aucun besoin d'etre decoupee. Ces routes servent les videos, ou le fichier
 * entier ne tient ni dans la memoire du telephone ni dans une seule requete.
 *
 * Le parcours :
 *   1. POST   /uploads/sessions                   ouvre la session
 *   2. PUT    /uploads/sessions/{id}/morceaux/{n} une fois par morceau
 *   3. GET    /uploads/sessions/{id}              pour reprendre apres coupure
 *   4. POST   /uploads/sessions/{id}/finir        assemble et rend l'adresse
 */
@RestController
@RequestMapping("/uploads/sessions")
@Tag(name = "Envoi par morceaux", description = "Envoi de fichiers volumineux, avec reprise")
@SecurityRequirement(name = "bearerAuth")
public class SessionEnvoiController {

    private final ServiceEnvoiParMorceaux serviceEnvoi;

    public SessionEnvoiController(ServiceEnvoiParMorceaux serviceEnvoi) {
        this.serviceEnvoi = serviceEnvoi;
    }

    @Operation(
            summary = "Ouvrir une session d'envoi",
            description = "La taille totale est annoncee d'avance : un fichier trop "
                    + "volumineux ou d'un format refuse est rejete ici, avant le premier "
                    + "octet, plutot qu'apres plusieurs minutes d'envoi. La reponse donne "
                    + "la taille de morceau a utiliser."
    )
    @PostMapping
    public ResponseEntity<RestResponse<EtatSessionEnvoiResponse>> ouvrir(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreerSessionEnvoiRequest requete) {

        EtatSessionEnvoiResponse etat = serviceEnvoi.creer(userDetails.getUsername(), requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RestResponse.buildSuccessResponse(HttpStatus.CREATED,
                        "session d'envoi ouverte", etat));
    }

    @Operation(
            summary = "Envoyer un morceau",
            description = "Le morceau est transmis en corps brut, pas en multipart : il n'y "
                    + "a qu'une donnee et l'enveloppe multipart n'apporterait rien. "
                    + "Idempotent, renvoyer le meme indice ecrase le precedent — c'est ce "
                    + "qui rend le reessai sans risque."
    )
    @PutMapping(value = "/{id}/morceaux/{indice}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<RestResponse<Void>> envoyerMorceau(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Identifiant de la session", required = true)
            @PathVariable String id,
            @Parameter(description = "Indice du morceau, a partir de 0", required = true)
            @PathVariable int indice,
            @RequestBody byte[] contenu) {

        serviceEnvoi.enregistrerMorceau(userDetails.getUsername(), id, indice, contenu);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "morceau recu", null));
    }

    @Operation(
            summary = "Etat de la session",
            description = "Liste les morceaux deja recus. C'est ce qui permet la reprise : "
                    + "le client saute ceux qui sont deja la. L'etat est deduit des fichiers "
                    + "sur le disque, donc il survit a un redemarrage du serveur."
    )
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<EtatSessionEnvoiResponse>> etat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {

        EtatSessionEnvoiResponse etat = serviceEnvoi.etat(userDetails.getUsername(), id);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "etat de la session", etat));
    }

    @Operation(
            summary = "Terminer l'envoi",
            description = "Assemble les morceaux dans l'ordre et renvoie l'adresse publique "
                    + "du fichier, a stocker telle quelle dans event_media.url. Renvoie 409 "
                    + "si des morceaux manquent, ou si la taille assemblee ne correspond pas "
                    + "a celle annoncee. La session est supprimee ensuite."
    )
    @PostMapping("/{id}/finir")
    public ResponseEntity<RestResponse<Map<String, String>>> finir(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {

        String url = serviceEnvoi.finir(userDetails.getUsername(), id);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "fichier assemble avec succes", Map.of("url", url)));
    }
}
