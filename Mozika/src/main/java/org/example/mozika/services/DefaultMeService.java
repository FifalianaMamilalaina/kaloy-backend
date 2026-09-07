package org.example.mozika.services;

import org.example.mozika.auth.EmailService;
import org.example.mozika.auth.SmsService;
import org.example.mozika.dto.me.*;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.*;
import org.example.mozika.repositories.*;
import org.example.mozika.services.interfaces.ArtistService;
import org.example.mozika.services.interfaces.MeService;
import org.example.mozika.services.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class DefaultMeService implements MeService {

    private static final Logger log = LoggerFactory.getLogger(DefaultMeService.class);

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final UsersInfosRepository usersInfosRepository;
    private final ArtistGroupMemberRepository artistGroupMemberRepository;
    private final InstrumentRoleRepository instrumentRoleRepository;
    private final MemberStatuseRepository memberStatuseRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationChannelRepository verificationChannelRepository;
    private final VerificationStatuseRepository verificationStatuseRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final UserService userService;
    private final ArtistService artistService;

    public DefaultMeService(UserRepository userRepository,
            ArtistRepository artistRepository,
            UsersInfosRepository usersInfosRepository,
            ArtistGroupMemberRepository artistGroupMemberRepository,
            InstrumentRoleRepository instrumentRoleRepository,
            MemberStatuseRepository memberStatuseRepository,
            VerificationCodeRepository verificationCodeRepository,
            VerificationChannelRepository verificationChannelRepository,
            VerificationStatuseRepository verificationStatuseRepository,
            NotificationRepository notificationRepository,
            NotificationTypeRepository notificationTypeRepository,
            UserRoleRepository userRoleRepository,
            EmailService emailService,
            SmsService smsService,
            UserService userService,
            ArtistService artistService) {
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
        this.usersInfosRepository = usersInfosRepository;
        this.artistGroupMemberRepository = artistGroupMemberRepository;
        this.instrumentRoleRepository = instrumentRoleRepository;
        this.memberStatuseRepository = memberStatuseRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.verificationChannelRepository = verificationChannelRepository;
        this.verificationStatuseRepository = verificationStatuseRepository;
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
        this.userRoleRepository = userRoleRepository;
        this.emailService = emailService;
        this.smsService = smsService;
        this.userService = userService;
        this.artistService = artistService;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User loadUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));
    }

    private User assertRole(User user, String... expectedRoles) {
        String role = user.getRoleidUserRoles() != null ? user.getRoleidUserRoles().getName() : "";
        for (String expected : expectedRoles) {
            if (expected.equalsIgnoreCase(role))
                return user;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Accès refusé — cette action n'est pas disponible pour votre type de compte.");
    }

    private Artist loadArtist(User user) {
        return artistRepository.findByUseridUsers(user)
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Profil artiste introuvable."));
    }

    private Artist assertGroupArtist(User user) {
        Artist artist = loadArtist(user);
        if (!"GROUP".equalsIgnoreCase(artist.getArtisttypeidArtistTypes().getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Cette fonctionnalité est réservée aux artistes de type GROUPE.");
        }
        return artist;
    }

    private String resolveEmailVerifStatus(User user) {
        return user.getEmailVerifiedAt() != null ? "Vérifié" : "Non vérifié";
    }

    private String resolvePhoneVerifStatus(User user) {
        return user.getPhoneVerifiedAt() != null ? "Vérifié" : "Non vérifié";
    }

    private String resolveAccountStatus(User user) {
        if (user.getStatusidUserStatuses() == null)
            return null;
        String name = user.getStatusidUserStatuses().getName();
        if ("ACTIVE".equalsIgnoreCase(name))
            return null;
        return switch (name.toUpperCase()) {
            case "INACTIVE" -> "Inactif";
            case "BANNED" -> "Banni";
            case "SUSPENDED" -> "Suspendu";
            default -> name;
        };
    }

    private String resolveVerifStatus(VerificationStatuse vs) {
        if (vs == null)
            return null;
        return switch (vs.getName().toUpperCase()) {
            case "PENDING" -> "En attente";
            case "VALIDATED" -> "Validé";
            case "REJECTED" -> "Rejeté";
            default -> vs.getName();
        };
    }

    private GroupMemberResponse toGroupMemberResponse(ArtistGroupMember m) {
        String statusLabel = (m.getStatusidMemberStatuses() != null)
                ? (m.getStatusidMemberStatuses().getName().equalsIgnoreCase("FORMER")
                        ? "ANCIEN MEMBRE"
                        : "ACTIF")
                : null;
        Long roleId = (m.getRoleinstrumentidInstrumentRoles() != null)
                ? m.getRoleinstrumentidInstrumentRoles().getId()
                : null;
        String roleLabel = (m.getRoleinstrumentidInstrumentRoles() != null)
                ? m.getRoleinstrumentidInstrumentRoles().getLabel()
                : null;
        return new GroupMemberResponse(m.getId(), m.getFullName(), roleId, roleLabel,
                m.getPhotoUrl(), statusLabel, m.getCreatedAt());
    }

    // ── GET /me ──────────────────────────────────────────────────────────────

    @Override
    public Object getMyProfile(String email) {
        User user = loadUser(email);
        String role = user.getRoleidUserRoles() != null && user.getRoleidUserRoles().getName() != null
                ? user.getRoleidUserRoles().getName().trim()
                : "";
        log.info("Chargement du profil /me pour email={}, role={}", email, role);

        if ("CLIENT".equalsIgnoreCase(role)) {
            return buildClientProfile(user);
        } else if ("ARTIST".equalsIgnoreCase(role)) {
            return buildArtistProfile(user);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Type de compte non reconnu.");
    }

    private ClientProfileResponse buildClientProfile(User user) {
        UsersInfos info = usersInfosRepository.findByUser(user)
                .stream().findFirst().orElse(null);
        return new ClientProfileResponse(
                user.getId(),
                user.getEmail(),
                resolveEmailVerifStatus(user),
                user.getPhone(),
                resolvePhoneVerifStatus(user),
                resolveAccountStatus(user),
                user.getCreatedAt(),
                info != null ? info.getName() : null,
                info != null ? info.getLastName() : null,
                info != null ? info.getUserName() : null,
                info != null ? info.getPhotoUrl() : null);
    }

    private ArtistProfileResponse buildArtistProfile(User user) {
        Artist artist = loadArtist(user);
        ArtistProfileResponse resp = new ArtistProfileResponse();
        resp.setUserId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setEmailVerificationStatus(resolveEmailVerifStatus(user));
        resp.setPhone(user.getPhone());
        resp.setPhoneVerificationStatus(resolvePhoneVerifStatus(user));
        resp.setAccountStatus(resolveAccountStatus(user));
        resp.setMemberSince(user.getCreatedAt());
        resp.setArtistType(artist.getArtisttypeidArtistTypes().getName());
        resp.setStageName(artist.getStageName());
        resp.setActiveSinceYear(artist.getActiveSinceYear());
        resp.setPhotoUrl(artist.getPhotoUrl());
        resp.setBio(artist.getBio());
        resp.setVerificationStatus(resolveVerifStatus(artist.getVerificationstatusidVerificationStatuses()));
        resp.setIsCertified(artist.getIsCertified());

        if ("GROUP".equalsIgnoreCase(artist.getArtisttypeidArtistTypes().getName())) {
            List<ArtistGroupMember> all = artistGroupMemberRepository.findByGroupartistidArtists(artist);
            List<GroupMemberResponse> allDto = all.stream()
                    .map(this::toGroupMemberResponse).collect(Collectors.toList());
            List<GroupMemberResponse> activeDto = all.stream()
                    .filter(m -> m.getStatusidMemberStatuses() != null
                            && "ACTIVE".equalsIgnoreCase(m.getStatusidMemberStatuses().getName()))
                    .map(this::toGroupMemberResponse).collect(Collectors.toList());
            resp.setMembers(allDto);
            resp.setActiveMembers(activeDto);
        }
        return resp;
    }

    // ── PUT /me/personal-info ─────────────────────────────────────────────────

    @Override
    @Transactional
    public void updatePersonalInfo(String email, UpdatePersonalInfoRequest request) {
        User user = loadUser(email);
        assertRole(user, "CLIENT");

        List<UsersInfos> infoList = usersInfosRepository.findByUser(user);
        UsersInfos info;
        if (infoList.isEmpty()) {
            info = new UsersInfos();
            info.setUser(user);
        } else {
            info = infoList.get(0);
        }

        if (request.getFirstName() != null)
            info.setName(request.getFirstName());
        if (request.getLastName() != null)
            info.setLastName(request.getLastName());
        if (request.getUserName() != null)
            info.setUserName(request.getUserName());

        usersInfosRepository.save(info);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // ── PUT /me/artist-profile ────────────────────────────────────────────────

    @Override
    @Transactional
    public void updateArtistProfile(String email, UpdateArtistProfileRequest request) {
        User user = loadUser(email);
        assertRole(user, "ARTIST");
        Artist artist = loadArtist(user);

        if (request.getStageName() != null && !request.getStageName().isBlank()) {
            artist.setStageName(request.getStageName());
        }
        if (request.getBio() != null)
            artist.setBio(request.getBio());
        if (request.getActiveSinceYear() != null)
            artist.setActiveSinceYear(request.getActiveSinceYear());

        artistRepository.save(artist);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // ── PUT /me/photo ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void updatePhoto(String email, UpdatePhotoRequest request) {
        User user = loadUser(email);
        String role = user.getRoleidUserRoles() != null ? user.getRoleidUserRoles().getName() : "";

        if ("CLIENT".equalsIgnoreCase(role)) {
            List<UsersInfos> infoList = usersInfosRepository.findByUser(user);
            UsersInfos info;
            if (infoList.isEmpty()) {
                info = new UsersInfos();
                info.setUser(user);
            } else {
                info = infoList.get(0);
            }
            info.setPhotoUrl(request.getPhotoUrl());
            usersInfosRepository.save(info);
        } else if ("ARTIST".equalsIgnoreCase(role)) {
            Artist artist = loadArtist(user);
            artist.setPhotoUrl(request.getPhotoUrl());
            artistRepository.save(artist);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Type de compte non reconnu.");
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // ── POST /me/email ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void initiateEmailChange(String currentEmail, ChangeEmailRequest request) {
        String newEmail = request.getNewEmail();
        if (currentEmail.equalsIgnoreCase(newEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le nouvel email est identique à votre email actuel.");
        }
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cette adresse e-mail est déjà associée à un compte.");
        }

        User user = loadUser(currentEmail);
        sendContactOtp(user, "EMAIL", newEmail);
    }

    // ── POST /me/email/confirm ────────────────────────────────────────────────

    @Override
    @Transactional
    public void confirmEmailChange(String currentEmail, ConfirmEmailRequest request) {
        User user = loadUser(currentEmail);
        VerificationCode code = verificationCodeRepository
                .findTopByUseridUsersAndChannelidVerificationChannelsNameAndConsumedAtIsNullOrderByCreatedAtDesc(user,
                        "EMAIL")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Aucun code OTP en attente pour cet email. Veuillez refaire une demande de changement."));

        verifyOtpCode(code, request.getCode());

        String newEmail = code.getDestination();
        user.setEmail(newEmail);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // ── POST /me/phone ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void initiatePhoneChange(String currentEmail, ChangePhoneRequest request) {
        String newPhone = request.getNewPhone();
        if (userRepository.findByPhone(newPhone).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ce numéro de téléphone est déjà associé à un compte.");
        }

        User user = loadUser(currentEmail);
        if (newPhone.equals(user.getPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le nouveau numéro est identique à votre numéro actuel.");
        }

        sendContactOtp(user, "SMS", newPhone);
    }

    // ── POST /me/phone/confirm ────────────────────────────────────────────────

    @Override
    @Transactional
    public void confirmPhoneChange(String currentEmail, ConfirmPhoneRequest request) {
        User user = loadUser(currentEmail);
        VerificationCode code = verificationCodeRepository
                .findTopByUseridUsersAndChannelidVerificationChannelsNameAndConsumedAtIsNullOrderByCreatedAtDesc(user,
                        "SMS")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Aucun code OTP en attente pour ce numéro. Veuillez refaire une demande de changement."));

        verifyOtpCode(code, request.getCode());

        user.setPhone(code.getDestination());
        user.setPhoneVerifiedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // ── POST /me/verification-request ────────────────────────────────────────

    @Override
    @Transactional
    public void requestVerification(String email) {
        User user = loadUser(email);
        assertRole(user, "ARTIST");
        Artist artist = loadArtist(user);

        String currentStatus = artist.getVerificationstatusidVerificationStatuses() != null
                ? artist.getVerificationstatusidVerificationStatuses().getName().toUpperCase()
                : "PENDING";

        if ("VALIDATED".equals(currentStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Votre compte est déjà validé.");
        }

        // PENDING (nouvel artiste ou déjà soumis) et REJECTED : on (re-)notifie les
        // admins
        if ("REJECTED".equals(currentStatus)) {
            VerificationStatuse pendingStatus = verificationStatuseRepository.findByName("PENDING")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Statut PENDING introuvable."));
            artist.setVerificationstatusidVerificationStatuses(pendingStatus);
            artistRepository.save(artist);
        }

        notifyAdmins(artist);
    }

    // ── GET /me/group/members ─────────────────────────────────────────────────

    @Override
    public List<GroupMemberResponse> getGroupMembers(String email) {
        User user = loadUser(email);
        assertRole(user, "ARTIST");
        Artist artist = assertGroupArtist(user);
        return artistGroupMemberRepository.findByGroupartistidArtists(artist)
                .stream().map(this::toGroupMemberResponse).collect(Collectors.toList());
    }

    // ── POST /me/group/members ────────────────────────────────────────────────

    @Override
    @Transactional
    public GroupMemberResponse addGroupMember(String email, AddGroupMemberRequest request) {
        User user = loadUser(email);
        assertRole(user, "ARTIST");
        Artist artist = assertGroupArtist(user);

        InstrumentRole role = instrumentRoleRepository.findById(request.getRoleInstrumentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Le rôle/instrument demandé n'existe pas."));

        MemberStatuse activeStatus = memberStatuseRepository.findByName("ACTIVE")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Statut ACTIVE introuvable."));

        ArtistGroupMember member = new ArtistGroupMember();
        member.setGroupartistidArtists(artist);
        member.setFullName(request.getFullName());
        member.setRoleinstrumentidInstrumentRoles(role);
        member.setPhotoUrl(request.getPhotoUrl());
        member.setStatusidMemberStatuses(activeStatus);
        member.setCreatedAt(LocalDateTime.now());

        return toGroupMemberResponse(artistGroupMemberRepository.save(member));
    }

    // ── PUT /me/group/members/{id} ────────────────────────────────────────────

    @Override
    @Transactional
    public GroupMemberResponse updateGroupMember(String email, Long memberId, UpdateGroupMemberRequest request) {
        User user = loadUser(email);
        assertRole(user, "ARTIST");
        Artist artist = assertGroupArtist(user);
        ArtistGroupMember member = loadAndAssertMemberOwnership(memberId, artist);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            member.setFullName(request.getFullName());
        }
        if (request.getRoleInstrumentId() != null) {
            InstrumentRole role = instrumentRoleRepository.findById(request.getRoleInstrumentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Le rôle/instrument demandé n'existe pas."));
            member.setRoleinstrumentidInstrumentRoles(role);
        }
        if (request.getPhotoUrl() != null) {
            member.setPhotoUrl(request.getPhotoUrl());
        }

        return toGroupMemberResponse(artistGroupMemberRepository.save(member));
    }

    // ── PATCH /me/group/members/{id}/status ──────────────────────────────────

    @Override
    @Transactional
    public GroupMemberResponse updateGroupMemberStatus(String email, Long memberId, GroupMemberStatusRequest request) {
        User user = loadUser(email);
        assertRole(user, "ARTIST");
        Artist artist = assertGroupArtist(user);
        ArtistGroupMember member = loadAndAssertMemberOwnership(memberId, artist);

        MemberStatuse status = memberStatuseRepository.findById(request.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Le statut demandé n'existe pas."));

        member.setStatusidMemberStatuses(status);
        return toGroupMemberResponse(artistGroupMemberRepository.save(member));
    }

    // ── DELETE /me/group/members/{id} ─────────────────────────────────────────

    @Override
    @Transactional
    public void deleteGroupMember(String email, Long memberId) {
        User user = loadUser(email);
        assertRole(user, "ARTIST");
        Artist artist = assertGroupArtist(user);
        ArtistGroupMember member = loadAndAssertMemberOwnership(memberId, artist);
        artistGroupMemberRepository.delete(member);
    }

    // ── GET /me/instrument-roles ──────────────────────────────────────────────

    @Override
    public List<InstrumentRoleResponse> getInstrumentRoles() {
        return instrumentRoleRepository.findAll().stream()
                .map(r -> new InstrumentRoleResponse(r.getId(), r.getLabel()))
                .collect(Collectors.toList());
    }

    // ── DELETE /me ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteMyAccount(String email) {
        User user = loadUser(email);
        String role = user.getRoleidUserRoles() != null ? user.getRoleidUserRoles().getName() : "";

        if ("ARTIST".equalsIgnoreCase(role)) {
            artistRepository.findByUseridUsers(user).stream().findFirst()
                    .ifPresent(artist -> artistService.deleteFullArtist(artist.getId()));
        }

        userService.deleteFullUser(user.getId());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private ArtistGroupMember loadAndAssertMemberOwnership(Long memberId, Artist artist) {
        ArtistGroupMember member = artistGroupMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Le membre demandé n'existe pas."));
        if (!member.getGroupartistidArtists().getId().equals(artist.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ce membre n'appartient pas à votre groupe.");
        }
        return member;
    }

    private void sendContactOtp(User user, String channelName, String destination) {
        VerificationChannel channel = verificationChannelRepository.findByName(channelName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Canal " + channelName + " introuvable."));

        String otpCode = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUseridUsers(user);
        verificationCode.setChannelidVerificationChannels(channel);
        verificationCode.setDestination(destination);
        verificationCode.setCode(otpCode);
        verificationCode.setExpiresAt(expiresAt);
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCodeRepository.save(verificationCode);

        if ("SMS".equalsIgnoreCase(channelName)) {
            smsService.sendOtpSms(destination, otpCode);
        } else {
            emailService.sendOtpEmail(destination, otpCode, expiresAt);
        }
    }

    private void verifyOtpCode(VerificationCode code, String submittedCode) {
        if (code.getConsumedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce code a déjà été utilisé.");
        }
        if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le code de vérification a expiré. Veuillez refaire une demande.");
        }
        if (!code.getCode().equals(submittedCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le code de vérification est invalide.");
        }
        code.setConsumedAt(LocalDateTime.now());
        verificationCodeRepository.save(code);
    }

    private void notifyAdmins(Artist artist) {
        UserRole adminRole = userRoleRepository.findByName("ADMIN").orElse(null);
        if (adminRole == null)
            return;

        List<User> admins = userRepository.findByRoleidUserRoles(adminRole);
        if (admins.isEmpty())
            return;

        NotificationType notifType = notificationTypeRepository.findByName("VERIFICATION").orElse(null);
        if (notifType == null)
            return;

        String content = "L'artiste « " + artist.getStageName()
                + " » a soumis une demande de vérification de compte.";

        List<Notification> notifications = admins.stream().map(admin -> {
            Notification n = new Notification();
            n.setUseridUsers(admin);
            n.setTypeidNotificationTypes(notifType);
            n.setContent(content);
            n.setRelatedEntityId(artist.getId());
            n.setIsRead(false);
            n.setCreatedAt(LocalDateTime.now());
            return n;
        }).collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
    }
}
