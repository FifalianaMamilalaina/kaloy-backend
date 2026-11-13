package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.dto.chanson.ChansonAModifierResponse;
import org.example.mozika.dto.chanson.CreerChansonRequest;
import org.example.mozika.dto.chanson.ModifierChansonRequest;
import org.example.mozika.dto.chanson.OptionsChansonResponse;
import org.example.mozika.models.Song;
import org.example.mozika.services.ServiceMesChansons;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Chansons declarees par l'artiste connecte.
 *
 * Meme principe que /mes-evenements : l'artiste est deduit du jeton et
 * n'apparait jamais dans l'URL.
 *
 * Une chanson peut etre declaree avec seulement des paroles ou un solfa, puis
 * completee plus tard avec l'audio, l'album ou la date de sortie.
 */
@RestController
@RequestMapping("/mes-chansons")
@Tag(name = "Mes chansons", description = "Declaration et completion de ses propres chansons")
@SecurityRequirement(name = "bearerAuth")
public class MesChansonsController {

    private final ServiceMesChansons service;

    public MesChansonsController(ServiceMesChansons service) {
        this.service = service;
    }

    @Operation(
            summary = "Mes chansons",
            description = "De la plus recemment declaree a la plus ancienne."
    )
    @GetMapping
    public ResponseEntity<RestResponse<List<Song>>> mesChansons(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Song> chansons = service.mesChansons(userDetails.getUsername());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "chansons recuperees avec succes", chansons));
    }

    @Operation(
            summary = "Les listes du formulaire",
            description = "Mes albums, les genres, les types de stockage et les langues, "
                    + "en un seul appel. Les albums sont deja restreints a l'artiste "
                    + "connecte, ce que /albums ne fait pas."
    )
    @GetMapping("/options")
    public ResponseEntity<RestResponse<OptionsChansonResponse>> options(
            @AuthenticationPrincipal UserDetails userDetails) {
        OptionsChansonResponse options = service.options(userDetails.getUsername());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "options recuperees avec succes", options));
    }

    @Operation(
            summary = "Une de mes chansons, pour la modifier",
            description = "Tout ce que le formulaire de modification doit pre-remplir, "
                    + "genres compris. La liste ne suffit pas : elle renvoie des entites "
                    + "Song, qui ne portent pas leurs genres. Les adresses des medias sont "
                    + "celles de la base, non resolues en adresses de lecture, pour pouvoir "
                    + "etre renvoyees telles quelles."
    )
    @GetMapping("/{idChanson}")
    public ResponseEntity<RestResponse<ChansonAModifierResponse>> chansonAModifier(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Identifiant de la chanson", required = true)
            @PathVariable Long idChanson) {
        ChansonAModifierResponse chanson =
                service.chansonAModifier(userDetails.getUsername(), idChanson);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "chanson recuperee avec succes", chanson));
    }

    @Operation(
            summary = "Declarer une chanson",
            description = "Le titre est obligatoire, et il faut AU MOINS des paroles ou un solfa. "
                    + "Aucun fichier audio n'est requis : une chanson peut exister sur son seul "
                    + "texte, et etre completee ensuite. Les onze champs du formulaire "
                    + "sont acceptes des la creation : album, genres, langue, duree, date "
                    + "de sortie, auteur-compositeur, arrangeur, adresses audio et video."
    )
    @PostMapping
    public ResponseEntity<RestResponse<Song>> creer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreerChansonRequest requete) {
        Song chanson = service.creer(userDetails.getUsername(), requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RestResponse.buildSuccessResponse(HttpStatus.CREATED,
                        "chanson declaree avec succes", chanson));
    }

    @Operation(
            summary = "Completer une chanson",
            description = "Tous les champs sont facultatifs. Un champ absent du corps est laisse "
                    + "tel quel ; un champ envoye vide efface la donnee. La chanson doit "
                    + "conserver au moins des paroles, un solfa ou un media."
    )
    @PutMapping("/{idChanson}")
    public ResponseEntity<RestResponse<Song>> modifier(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Identifiant de la chanson", required = true)
            @PathVariable Long idChanson,
            @RequestBody @Valid ModifierChansonRequest requete) {
        Song chanson = service.modifier(userDetails.getUsername(), idChanson, requete);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "chanson mise a jour avec succes", chanson));
    }

    @Operation(
            summary = "Supprimer une chanson",
            description = "Uniquement une des siennes."
    )
    @DeleteMapping("/{idChanson}")
    public ResponseEntity<RestResponse<Void>> supprimer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Identifiant de la chanson", required = true)
            @PathVariable Long idChanson) {
        service.supprimer(userDetails.getUsername(), idChanson);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "chanson supprimee avec succes", null));
    }
}
