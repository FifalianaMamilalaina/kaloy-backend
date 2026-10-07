package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.mozika.dto.ReponseInvitationRequest;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.models.Concert;
import org.example.mozika.services.ServiceInvitations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Invitations a participer a un evenement, cote artiste invite.
 *
 * L'artiste n'est jamais passe en parametre : il est deduit du jeton. Un
 * identifiant d'artiste dans l'URL aurait permis de lister — ou pire, de
 * repondre a — les invitations d'un autre.
 */
@RestController
@RequestMapping("/invitations")
@Tag(name = "Invitations", description = "Invitations recues par un artiste")
@SecurityRequirement(name = "bearerAuth")
public class InvitationController {

    private final ServiceInvitations serviceInvitations;

    public InvitationController(ServiceInvitations serviceInvitations) {
        this.serviceInvitations = serviceInvitations;
    }

    @Operation(
            summary = "Mes invitations en attente",
            description = "Invitations non encore repondues dont la date n'est pas passee, "
                    + "de la plus proche a la plus lointaine."
    )
    @GetMapping
    public ResponseEntity<RestResponse<List<Concert>>> mesInvitations(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Concert> invitations = serviceInvitations.mesInvitationsEnAttente(userDetails.getUsername());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "invitations recuperees avec succes", invitations));
    }

    @Operation(
            summary = "Accepter ou refuser une invitation",
            description = "Met le statut a CONFIRMED ou DECLINED et horodate la reponse. "
                    + "Accepter fait entrer le concert dans le calendrier de l'artiste ; "
                    + "refuser ne produit aucun autre effet. Une invitation deja repondue "
                    + "renvoie 409."
    )
    @PatchMapping("/{idConcert}")
    public ResponseEntity<RestResponse<Concert>> repondre(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Identifiant du concert correspondant a l'invitation", required = true)
            @PathVariable Long idConcert,
            @RequestBody @Valid ReponseInvitationRequest requete) {

        Concert concert = serviceInvitations.repondre(
                userDetails.getUsername(), idConcert, requete.getStatut());

        String message = ServiceInvitations.STATUT_ACCEPTE.equals(requete.getStatut())
                ? "invitation acceptee"
                : "invitation refusee";
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(HttpStatus.OK, message, concert));
    }
}
