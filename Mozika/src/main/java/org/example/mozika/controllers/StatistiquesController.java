package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.dto.StatistiquesArtisteResponse;
import org.example.mozika.services.ServiceStatistiques;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Statistiques de l'artiste connecte.
 *
 * Controleur a part, comme AppareilController : MeController porte deja
 * dix-sept routes. Aucun chevauchement possible, il n'y a pas de route a
 * segment variable sur /me qui pourrait avaler « statistiques ».
 */
@RestController
@RequestMapping("/me/statistiques")
@Tag(name = "Statistiques", description = "Statistiques de l'artiste connecte")
@SecurityRequirement(name = "bearerAuth")
public class StatistiquesController {

    private final ServiceStatistiques serviceStatistiques;

    public StatistiquesController(ServiceStatistiques serviceStatistiques) {
        this.serviceStatistiques = serviceStatistiques;
    }

    @Operation(
            summary = "Mes statistiques",
            description = "Ecoutes sur 30 jours et depuis le debut, auditeurs distincts, "
                    + "abonnes, nombre de chansons publiees, et les cinq titres les plus "
                    + "ecoutes. Un seul appel : l'ecran affiche un etat coherent. "
                    + "Renvoie 403 pour un compte qui n'est rattache a aucun profil artiste."
    )
    @GetMapping
    public ResponseEntity<RestResponse<StatistiquesArtisteResponse>> mesStatistiques(
            @AuthenticationPrincipal UserDetails userDetails) {

        StatistiquesArtisteResponse statistiques =
                serviceStatistiques.mesStatistiques(userDetails.getUsername());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "statistiques recuperees avec succes", statistiques));
    }
}
