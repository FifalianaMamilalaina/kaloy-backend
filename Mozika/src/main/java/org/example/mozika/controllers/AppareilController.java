package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.mozika.dto.EnregistrerAppareilRequest;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.services.EnvoiPush;
import org.example.mozika.services.ServiceAppareils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Appareils du compte connecte, pour les notifications push.
 *
 * Controleur a part et non une route de plus sur MeController, qui en porte
 * deja dix-sept. Les chemins ne se chevauchent pas : MeController n'a aucune
 * route a segment variable qui pourrait avaler « devices ».
 */
@RestController
@RequestMapping("/me/devices")
@Tag(name = "Appareils", description = "Enregistrement des appareils pour les notifications push")
@SecurityRequirement(name = "bearerAuth")
public class AppareilController {

    private final ServiceAppareils serviceAppareils;
    private final EnvoiPush envoiPush;

    public AppareilController(ServiceAppareils serviceAppareils, EnvoiPush envoiPush) {
        this.serviceAppareils = serviceAppareils;
        this.envoiPush = envoiPush;
    }

    @Operation(
            summary = "Enregistrer cet appareil",
            description = "A appeler apres la connexion, et a chaque renouvellement du jeton "
                    + "par Firebase. Idempotent : reenregistrer le meme jeton met a jour sa "
                    + "date de derniere vue sans creer de doublon. Si le jeton etait attache "
                    + "a un autre compte — meme telephone, autre utilisateur — il change de "
                    + "proprietaire."
    )
    @PostMapping
    public ResponseEntity<RestResponse<Void>> enregistrer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid EnregistrerAppareilRequest requete) {

        serviceAppareils.enregistrer(
                userDetails.getUsername(), requete.getToken(), requete.getPlatform());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "appareil enregistre", null));
    }

    @Operation(
            summary = "Retirer cet appareil",
            description = "A appeler a la deconnexion, pour que le telephone ne recoive plus "
                    + "les notifications du compte quitte. Un jeton inconnu renvoie 200 : la "
                    + "deconnexion ne doit pas echouer parce que l'appareil n'etait pas "
                    + "enregistre."
    )
    @DeleteMapping("/{token}")
    public ResponseEntity<RestResponse<Void>> retirer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Jeton de l'appareil a retirer", required = true)
            @PathVariable String token) {

        serviceAppareils.retirer(userDetails.getUsername(), token);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "appareil retire", null));
    }

    @Operation(
            summary = "Mes appareils enregistres",
            description = "Diagnostic. Renvoie le nombre d'appareils, leur plateforme et un "
                    + "extrait du jeton — jamais le jeton complet, qui permettrait d'envoyer "
                    + "des notifications a cet appareil. Indique aussi si l'envoi reel est "
                    + "actif cote serveur."
    )
    @GetMapping
    public ResponseEntity<RestResponse<Map<String, Object>>> mesAppareils(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<Map<String, Object>> appareils = serviceAppareils
                .mesAppareils(userDetails.getUsername()).stream()
                .map(a -> Map.<String, Object>of(
                        "platform", a.getPlatform(),
                        "token", extrait(a.getToken()),
                        "lastSeenAt", a.getLastSeenAt().toString()))
                .toList();

        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "appareils recuperes avec succes",
                Map.of("pushActif", envoiPush.estActif(), "appareils", appareils)));
    }

    /** Assez pour reconnaitre un appareil dans un journal, pas pour s'en servir. */
    private String extrait(String token) {
        return token.length() <= 12 ? token : token.substring(0, 12) + "...";
    }
}
