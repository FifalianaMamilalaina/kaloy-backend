package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.dto.organisation.CreerEvenementRequest;
import org.example.mozika.dto.organisation.CreneauRequest;
import org.example.mozika.models.Concert;
import org.example.mozika.models.Event;
import org.example.mozika.services.ServiceOrganisationEvenements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Evenements organises par l'artiste connecte.
 *
 * Comme pour les invitations, l'organisateur n'apparait jamais dans l'URL : il
 * est deduit du jeton. Un identifiant d'artiste en parametre aurait permis de
 * creer un evenement — et d'envoyer des invitations — au nom d'un autre.
 */
@RestController
@RequestMapping("/mes-evenements")
@Tag(name = "Mes evenements", description = "Creation d'evenements et invitations d'artistes")
@SecurityRequirement(name = "bearerAuth")
public class MesEvenementsController {

    private final ServiceOrganisationEvenements service;

    public MesEvenementsController(ServiceOrganisationEvenements service) {
        this.service = service;
    }

    @Operation(
            summary = "Les evenements que j'organise",
            description = "Du plus recent au plus ancien, quelle que soit leur date."
    )
    @GetMapping
    public ResponseEntity<RestResponse<List<Event>>> mesEvenements(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Event> evenements = service.mesEvenements(userDetails.getUsername());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "evenements recuperes avec succes", evenements));
    }

    @Operation(
            summary = "La programmation d'un de mes evenements",
            description = "Tous les creneaux et leur statut : accepte, refuse ou en attente de reponse."
    )
    @GetMapping("/{idEvenement}/programmation")
    public ResponseEntity<RestResponse<List<Concert>>> programmation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Identifiant de l'evenement", required = true)
            @PathVariable Long idEvenement) {
        List<Concert> creneaux = service.programmation(userDetails.getUsername(), idEvenement);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "programmation recuperee avec succes", creneaux));
    }

    @Operation(
            summary = "Creer un evenement",
            description = "Cree l'evenement et, si des creneaux sont fournis, les invitations "
                    + "correspondantes. Le creneau que l'organisateur se reserve est confirme "
                    + "d'emblee ; les autres partent en attente de reponse. L'operation est "
                    + "atomique : un creneau invalide annule toute la creation."
    )
    @PostMapping
    public ResponseEntity<RestResponse<Event>> creer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreerEvenementRequest requete) {
        Event evenement = service.creer(userDetails.getUsername(), requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RestResponse.buildSuccessResponse(HttpStatus.CREATED,
                        "evenement cree avec succes", evenement));
    }

    @Operation(
            summary = "Inviter d'autres artistes",
            description = "Ajoute des creneaux a un evenement deja cree, pour completer l'affiche "
                    + "ou remplacer un artiste qui a refuse."
    )
    @PostMapping("/{idEvenement}/invitations")
    public ResponseEntity<RestResponse<List<Concert>>> inviter(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Identifiant de l'evenement", required = true)
            @PathVariable Long idEvenement,
            @RequestBody @Valid List<CreneauRequest> creneaux) {
        List<Concert> crees = service.ajouterCreneaux(userDetails.getUsername(), idEvenement, creneaux);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RestResponse.buildSuccessResponse(HttpStatus.CREATED,
                        "invitations envoyees avec succes", crees));
    }
}
