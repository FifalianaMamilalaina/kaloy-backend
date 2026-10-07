package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.dto.organisation.CreerConcertRequest;
import org.example.mozika.models.Concert;
import org.example.mozika.services.ServiceInvitations;
import org.example.mozika.services.ServiceOrganisationEvenements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Calendrier de l'artiste connecte.
 *
 * Route separee de /invitations : un calendrier n'est pas une invitation, et
 * une URL doit dire ce qu'elle renvoie — elle sert aussi de documentation a qui
 * consomme l'API.
 *
 * Le service est partage avec les invitations : les deux listent les concerts
 * de l'artiste connecte, seul le statut retenu change — PENDING pour les
 * invitations, CONFIRMED pour le calendrier.
 */
@RestController
@RequestMapping("/mon-calendrier")
@Tag(name = "Mon calendrier", description = "Concerts confirmes de l'artiste connecte")
@SecurityRequirement(name = "bearerAuth")
public class MonCalendrierController {

    private final ServiceInvitations serviceInvitations;
    private final ServiceOrganisationEvenements serviceOrganisation;

    public MonCalendrierController(ServiceInvitations serviceInvitations,
                                   ServiceOrganisationEvenements serviceOrganisation) {
        this.serviceInvitations = serviceInvitations;
        this.serviceOrganisation = serviceOrganisation;
    }

    @Operation(
            summary = "Mes concerts confirmes",
            description = "Passes et a venir, du plus ancien au plus recent. Meme contenu que le "
                    + "calendrier de ma fiche publique, mais sans avoir a connaitre mon "
                    + "identifiant d'artiste : il est deduit du jeton."
    )
    @GetMapping
    public ResponseEntity<RestResponse<List<Concert>>> monCalendrier(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Concert> concerts = serviceInvitations.monAgenda(userDetails.getUsername());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "calendrier recupere avec succes", concerts));
    }

    @Operation(
            summary = "Declarer un concert",
            description = "Cree un concert sans evenement parent : l'artiste joue seul et declare "
                    + "simplement sa date. Le concert est confirme d'emblee, puisqu'il n'y a "
                    + "personne a inviter. La date doit etre a venir."
    )
    @PostMapping
    public ResponseEntity<RestResponse<Concert>> declarerConcert(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreerConcertRequest requete) {
        Concert concert = serviceOrganisation.creerConcertSeul(userDetails.getUsername(), requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RestResponse.buildSuccessResponse(HttpStatus.CREATED,
                        "concert cree avec succes", concert));
    }

    @Operation(
            summary = "Annuler un concert",
            description = "Supprime un concert a venir que j'ai declare seul. Un creneau "
                    + "appartenant a un evenement renvoie 409 : seul son organisateur peut "
                    + "le retirer. Un concert passe renvoie 409 egalement."
    )
    @DeleteMapping("/{idConcert}")
    public ResponseEntity<RestResponse<Void>> annulerConcert(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Identifiant du concert a annuler", required = true)
            @PathVariable Long idConcert) {
        serviceOrganisation.supprimerConcertSeul(userDetails.getUsername(), idConcert);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "concert annule avec succes", null));
    }
}
