package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.dto.paroles.CandidatParoles;
import org.example.mozika.dto.paroles.ParolesTrouvees;
import org.example.mozika.dto.paroles.RechercheParolesRequest;
import org.example.mozika.services.ServiceParoles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Aide a la saisie des paroles, depuis tononkira.serasera.org.
 *
 * Deux temps, et c'est volontaire : on cherche d'abord des references, puis on
 * telecharge le texte du seul candidat retenu. Une recherche sur un mot courant
 * remonte une vingtaine de chansons d'artistes sans rapport ; les telecharger
 * toutes pour n'en garder qu'une serait vingt pages demandees pour rien.
 *
 * Rien n'est enregistre ici. L'artiste relit l'apercu, le corrige au besoin, et
 * c'est /mes-chansons qui decide de ce qui finit en base.
 */
@RestController
@RequestMapping("/paroles")
@Tag(name = "Paroles", description = "Recherche d'aide a la saisie des paroles")
@SecurityRequirement(name = "bearerAuth")
public class ParolesController {

    private final ServiceParoles service;

    public ParolesController(ServiceParoles service) {
        this.service = service;
    }

    @Operation(
            summary = "Chercher des paroles",
            description = "Renvoie des references classees par ressemblance, SANS le texte. "
                    + "Le nom d'artiste est facultatif : il ameliore le classement mais ne "
                    + "filtre pas, les deux catalogues ne nommant pas toujours les artistes "
                    + "de la meme facon."
    )
    @PostMapping("/recherche")
    public ResponseEntity<RestResponse<List<CandidatParoles>>> rechercher(
            @RequestBody @Valid RechercheParolesRequest requete) {
        List<CandidatParoles> candidats =
                service.rechercher(requete.getTitre(), requete.getArtiste());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "recherche effectuee avec succes", candidats));
    }

    @Operation(
            summary = "Lire les paroles d'un candidat",
            description = "Telecharge le texte pour apercu. L'adresse doit provenir de la "
                    + "recherche : seules celles de tononkira.serasera.org sont acceptees."
    )
    @GetMapping
    public ResponseEntity<RestResponse<ParolesTrouvees>> texte(
            @Parameter(description = "Adresse de la page, telle que renvoyee par la recherche",
                    required = true)
            @RequestParam String url) {
        ParolesTrouvees trouvees = service.texte(url);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "paroles recuperees avec succes", trouvees));
    }
}
