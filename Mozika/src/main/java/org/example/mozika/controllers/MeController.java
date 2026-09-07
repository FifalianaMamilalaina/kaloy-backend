package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.dto.me.*;
import org.example.mozika.services.interfaces.MeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/me")
@Tag(name = "Moi", description = "Profil personnel — consultation et mise à jour")
@SecurityRequirement(name = "bearerAuth")
public class MeController {

        private static final Logger log = LoggerFactory.getLogger(MeController.class);

        private final MeService meService;

        public MeController(MeService meService) {
                this.meService = meService;
        }

        // ── GET /me ───────────────────────────────────────────────────────────────

        @Operation(summary = "Consulter mon profil (adapté selon le type de compte)")
        @GetMapping
        public ResponseEntity<RestResponse<Object>> getMyProfile(
                        @AuthenticationPrincipal UserDetails userDetails) {
                log.info("GET /me atteint, principal={}",
                                userDetails != null ? userDetails.getUsername() : "null");
                Object profile = meService.getMyProfile(userDetails.getUsername());
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK, "Profil récupéré avec succès.",
                                                profile));
        }

        // ── PUT /me/personal-info ─────────────────────────────────────────────────

        @Operation(summary = "Mettre à jour mes informations personnelles (CLIENT uniquement)")
        @PutMapping("/personal-info")
        public ResponseEntity<RestResponse<Void>> updatePersonalInfo(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody @Valid UpdatePersonalInfoRequest request) {
                meService.updatePersonalInfo(userDetails.getUsername(), request);
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK,
                                                "Informations mises à jour avec succès.", null));
        }

        // ── PUT /me/artist-profile ────────────────────────────────────────────────

        @Operation(summary = "Mettre à jour mon profil artiste (ARTIST uniquement)")
        @PutMapping("/artist-profile")
        public ResponseEntity<RestResponse<Void>> updateArtistProfile(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody @Valid UpdateArtistProfileRequest request) {
                meService.updateArtistProfile(userDetails.getUsername(), request);
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK,
                                                "Profil artiste mis à jour avec succès.", null));
        }

        // ── PUT /me/photo ─────────────────────────────────────────────────────────

        @Operation(summary = "Mettre à jour ma photo de profil")
        @PutMapping("/photo")
        public ResponseEntity<RestResponse<Void>> updatePhoto(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody @Valid UpdatePhotoRequest request) {
                meService.updatePhoto(userDetails.getUsername(), request);
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK, "Photo mise à jour avec succès.",
                                                null));
        }

        // ── POST /me/email ────────────────────────────────────────────────────────

        @Operation(summary = "Demander un changement d'adresse email (envoi OTP au nouvel email)")
        @PostMapping("/email")
        public ResponseEntity<RestResponse<Void>> initiateEmailChange(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody @Valid ChangeEmailRequest request) {
                meService.initiateEmailChange(userDetails.getUsername(), request);
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK,
                                                "Un code de vérification a été envoyé à votre nouvel email. Vérifiez votre boîte de réception.",
                                                null));
        }

        // ── POST /me/email/confirm ────────────────────────────────────────────────

        @Operation(summary = "Confirmer le changement d'email avec le code OTP")
        @PostMapping("/email/confirm")
        public ResponseEntity<RestResponse<Void>> confirmEmailChange(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody @Valid ConfirmEmailRequest request) {
                meService.confirmEmailChange(userDetails.getUsername(), request);
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK,
                                                "Email mis à jour avec succès. Veuillez vous reconnecter avec votre nouvel email.",
                                                null));
        }

        // ── POST /me/phone ────────────────────────────────────────────────────────

        @Operation(summary = "Demander un changement de numéro de téléphone (envoi OTP par SMS)")
        @PostMapping("/phone")
        public ResponseEntity<RestResponse<Void>> initiatePhoneChange(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody @Valid ChangePhoneRequest request) {
                meService.initiatePhoneChange(userDetails.getUsername(), request);
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK,
                                                "Un code de vérification a été envoyé à votre nouveau numéro de téléphone.",
                                                null));
        }

        // ── POST /me/phone/confirm ────────────────────────────────────────────────

        @Operation(summary = "Confirmer le changement de téléphone avec le code OTP")
        @PostMapping("/phone/confirm")
        public ResponseEntity<RestResponse<Void>> confirmPhoneChange(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody @Valid ConfirmPhoneRequest request) {
                meService.confirmPhoneChange(userDetails.getUsername(), request);
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK,
                                                "Numéro de téléphone mis à jour avec succès.", null));
        }

        // ── POST /me/verification-request ────────────────────────────────────────

        @Operation(summary = "Soumettre une demande de vérification de compte (ARTIST uniquement)")
        @PostMapping("/verification-request")
        public ResponseEntity<RestResponse<Void>> requestVerification(
                        @AuthenticationPrincipal UserDetails userDetails) {
                meService.requestVerification(userDetails.getUsername());
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK,
                                                "Votre demande de vérification a été soumise. L'équipe Kaloy vous contactera.",
                                                null));
        }

        // ── GET /me/group/members ─────────────────────────────────────────────────

        @Operation(summary = "Lister tous les membres du groupe — vue gestion (ARTIST GROUPE uniquement)")
        @GetMapping("/group/members")
        public ResponseEntity<RestResponse<List<GroupMemberResponse>>> getGroupMembers(
                        @AuthenticationPrincipal UserDetails userDetails) {
                List<GroupMemberResponse> members = meService.getGroupMembers(userDetails.getUsername());
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK, "Membres récupérés avec succès.",
                                                members));
        }

        // ── POST /me/group/members ────────────────────────────────────────────────

        @Operation(summary = "Ajouter un membre au groupe")
        @PostMapping("/group/members")
        public ResponseEntity<RestResponse<GroupMemberResponse>> addGroupMember(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody @Valid AddGroupMemberRequest request) {
                GroupMemberResponse created = meService.addGroupMember(userDetails.getUsername(), request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(RestResponse.buildSuccessResponse(HttpStatus.CREATED,
                                                "Membre ajouté avec succès.", created));
        }

        // ── PUT /me/group/members/{id} ────────────────────────────────────────────

        @Operation(summary = "Modifier les informations d'un membre du groupe")
        @PutMapping("/group/members/{id}")
        public ResponseEntity<RestResponse<GroupMemberResponse>> updateGroupMember(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable Long id,
                        @RequestBody @Valid UpdateGroupMemberRequest request) {
                GroupMemberResponse updated = meService.updateGroupMember(userDetails.getUsername(), id, request);
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK, "Membre mis à jour avec succès.",
                                                updated));
        }

        // ── PATCH /me/group/members/{id}/status ──────────────────────────────────

        @Operation(summary = "Changer le statut d'un membre (ACTIF / ANCIEN MEMBRE)")
        @PatchMapping("/group/members/{id}/status")
        public ResponseEntity<RestResponse<GroupMemberResponse>> updateGroupMemberStatus(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable Long id,
                        @RequestBody @Valid GroupMemberStatusRequest request) {
                GroupMemberResponse updated = meService.updateGroupMemberStatus(userDetails.getUsername(), id, request);
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK, "Statut du membre mis à jour.",
                                                updated));
        }

        // ── DELETE /me/group/members/{id} ─────────────────────────────────────────

        @Operation(summary = "Supprimer un membre du groupe")
        @DeleteMapping("/group/members/{id}")
        public ResponseEntity<RestResponse<Void>> deleteGroupMember(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable Long id) {
                meService.deleteGroupMember(userDetails.getUsername(), id);
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK, "Membre supprimé avec succès.", null));
        }

        // ── GET /me/instrument-roles ──────────────────────────────────────────────

        @Operation(summary = "Lister les rôles/instruments disponibles pour les membres de groupe")
        @GetMapping("/instrument-roles")
        public ResponseEntity<RestResponse<List<InstrumentRoleResponse>>> getInstrumentRoles() {
                List<InstrumentRoleResponse> roles = meService.getInstrumentRoles();
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK, "Rôles récupérés avec succès.",
                                                roles));
        }

        // ── POST /me/logout ───────────────────────────────────────────────────────

        @Operation(summary = "Déconnexion (le client doit invalider son token JWT côté local)")
        @PostMapping("/logout")
        public ResponseEntity<RestResponse<Void>> logout() {
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK, "Déconnexion réussie.", null));
        }

        // ── DELETE /me ────────────────────────────────────────────────────────────

        @Operation(summary = "Supprimer définitivement mon compte (action irréversible)")
        @DeleteMapping
        public ResponseEntity<RestResponse<Void>> deleteMyAccount(
                        @AuthenticationPrincipal UserDetails userDetails) {
                meService.deleteMyAccount(userDetails.getUsername());
                return ResponseEntity.ok(
                                RestResponse.buildSuccessResponse(HttpStatus.OK,
                                                "Votre compte a été supprimé avec succès.", null));
        }
}
