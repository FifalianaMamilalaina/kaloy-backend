package org.example.mozika.auth;

import org.example.mozika.auth.dto.*;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.*;
import org.example.mozika.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Random;

@Service("teammateDefaultAuthService")
public class DefaultAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserStatuseRepository userStatuseRepository;
    private final VerificationStatusUserRepository verificationStatusUserRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationChannelRepository verificationChannelRepository;
    private final ArtistTypeRepository artistTypeRepository;
    private final VerificationStatuseRepository verificationStatuseRepository;
    private final ClientRepository clientRepository;
    private final ArtistRepository artistRepository;
    private final UsersInfosRepository usersInfosRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final SmsService smsService;

    public DefaultAuthService(UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            UserStatuseRepository userStatuseRepository,
            VerificationStatusUserRepository verificationStatusUserRepository,
            VerificationCodeRepository verificationCodeRepository,
            VerificationChannelRepository verificationChannelRepository,
            ArtistTypeRepository artistTypeRepository,
            VerificationStatuseRepository verificationStatuseRepository,
            ClientRepository clientRepository,
            ArtistRepository artistRepository,
            UsersInfosRepository usersInfosRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService,
            SmsService smsService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.userStatuseRepository = userStatuseRepository;
        this.verificationStatusUserRepository = verificationStatusUserRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.verificationChannelRepository = verificationChannelRepository;
        this.artistTypeRepository = artistTypeRepository;
        this.verificationStatuseRepository = verificationStatuseRepository;
        this.clientRepository = clientRepository;
        this.artistRepository = artistRepository;
        this.usersInfosRepository = usersInfosRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    @Override
    @Transactional
    public RegisterResponse registerClient(RegisterClientRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cette adresse e-mail est déjà associée à un compte. Veuillez en utiliser une autre ou vous connecter à votre compte existant.");
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()
                && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ce numéro est déjà utilisé. Merci d'en choisir un autre ou de vous connecter..");
        }

        UserRole role = userRoleRepository.findByName("CLIENT")
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Rôle CLIENT introuvable"));
        UserStatuse status = userStatuseRepository.findByName("ACTIVE")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Statut ACTIVE introuvable"));
        VerificationStatusUser verificationStatus = verificationStatusUserRepository.findByName("NOT_VERIFIED")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Statut NOT_VERIFIED introuvable"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoleidUserRoles(role);
        user.setStatusidUserStatuses(status);
        user.setVerificationStatusUser(verificationStatus);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        Client client = new Client();
        client.setUseridUsers(savedUser);
        clientRepository.save(client);

        boolean hasInfos = (request.getFirstName() != null && !request.getFirstName().isBlank())
                || (request.getLastName() != null && !request.getLastName().isBlank())
                || (request.getUsername() != null && !request.getUsername().isBlank());
        if (hasInfos) {
            UsersInfos infos = new UsersInfos();
            infos.setUser(savedUser);
            infos.setName(request.getFirstName());
            infos.setLastName(request.getLastName());
            infos.setUserName(request.getUsername());
            usersInfosRepository.save(infos);
        }

        sendOtp(savedUser, request.getOtpChannel());

        String canal = "SMS".equalsIgnoreCase(request.getOtpChannel()) ? "SMS" : "email";
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                "Code OTP envoyé — vérifiez votre " + canal,
                "NOT_VERIFIED");
    }

    @Override
    @Transactional
    public RegisterResponse registerArtist(RegisterArtistRequest request) {
        String artistTypeValue = request.getArtistType() != null ? request.getArtistType().toUpperCase() : "";
        if (!artistTypeValue.equals("SOLO") && !artistTypeValue.equals("GROUP")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le type d'artiste doit être SOLO ou GROUP");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte avec cet email existe déjà");
        }

        UserRole role = userRoleRepository.findByName("ARTIST")
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Rôle ARTIST introuvable"));
        UserStatuse status = userStatuseRepository.findByName("ACTIVE")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Statut ACTIVE introuvable"));
        VerificationStatusUser verificationStatus = verificationStatusUserRepository.findByName("NOT_VERIFIED")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Statut NOT_VERIFIED introuvable"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoleidUserRoles(role);
        user.setStatusidUserStatuses(status);
        user.setVerificationStatusUser(verificationStatus);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        ArtistType artistType = artistTypeRepository.findByName(artistTypeValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Type d'artiste introuvable"));
        VerificationStatuse artistVerifStatus = verificationStatuseRepository.findByName("PENDING")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Statut PENDING introuvable"));

        Artist artist = new Artist();
        artist.setUseridUsers(savedUser);
        artist.setArtisttypeidArtistTypes(artistType);
        artist.setStageName(request.getStageName());
        artist.setActiveSinceYear(request.getActiveSinceYear());
        artist.setBio(request.getBio());
        artist.setPhotoUrl(request.getPhotoUrl());
        artist.setVerificationstatusidVerificationStatuses(artistVerifStatus);
        artist.setIsCertified(false);
        artist.setCreatedAt(LocalDateTime.now());
        artistRepository.save(artist);

        sendOtp(savedUser, request.getOtpChannel());

        String canal = "SMS".equalsIgnoreCase(request.getOtpChannel()) ? "SMS" : "email";
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                "Code OTP envoyé — vérifiez votre " + canal,
                "NOT_VERIFIED");
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        VerificationCode code = verificationCodeRepository
                .findTopByUseridUsersOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Aucun code OTP trouvé pour cet utilisateur"));

        if (code.getConsumedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce code a déjà été utilisé");
        }
        if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code expiré — demandez un nouveau code");
        }
        if (!code.getCode().equals(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code invalide");
        }

        code.setConsumedAt(LocalDateTime.now());
        verificationCodeRepository.save(code);

        VerificationStatusUser verified = verificationStatusUserRepository.findByName("VERIFIED")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Statut VERIFIED introuvable"));
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setVerificationStatusUser(verified);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        String role = user.getRoleidUserRoles() != null ? user.getRoleidUserRoles().getName() : "CLIENT";
        return new AuthResponse(token, user.getId(), user.getEmail(), role);
    }

    @Override
    @Transactional
    public String resendOtp(ResendOtpRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        VerificationCode lastCode = verificationCodeRepository
                .findTopByUseridUsersOrderByCreatedAtDesc(user)
                .orElse(null);

        if (lastCode != null) {
            LocalDateTime antiAbuseLimit = LocalDateTime.now().minusSeconds(60);
            if (lastCode.getCreatedAt().isAfter(antiAbuseLimit)) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "Attendez 60 secondes avant de renvoyer le code");
            }
        }

        String channel = "EMAIL";
        if (lastCode != null && lastCode.getChannelidVerificationChannels() != null
                && "SMS".equals(lastCode.getChannelidVerificationChannels().getName())) {
            channel = "SMS";
        }

        sendOtp(user, channel);
        return "Code OTP renvoyé avec succès";
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
        }

        if (user.getVerificationStatusUser() == null
                || !"VERIFIED".equals(user.getVerificationStatusUser().getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Compte non vérifié — vérifiez votre email avec le code OTP");
        }

        String token = jwtService.generateToken(user);
        String role = user.getRoleidUserRoles() != null ? user.getRoleidUserRoles().getName() : "CLIENT";
        return new AuthResponse(token, user.getId(), user.getEmail(), role);
    }

    private void sendOtp(User user, String otpChannel) {
        boolean viaSms = "SMS".equalsIgnoreCase(otpChannel);

        if (viaSms && (user.getPhone() == null || user.getPhone().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Numéro de téléphone requis pour l'envoi OTP par SMS");
        }

        String channelName = viaSms ? "SMS" : "EMAIL";
        VerificationChannel channel = verificationChannelRepository.findByName(channelName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Canal " + channelName + " introuvable"));

        String otpCode = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        String destination = viaSms ? user.getPhone() : user.getEmail();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUseridUsers(user);
        verificationCode.setChannelidVerificationChannels(channel);
        verificationCode.setDestination(destination);
        verificationCode.setCode(otpCode);
        verificationCode.setExpiresAt(expiresAt);
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCodeRepository.save(verificationCode);

        if (viaSms) {
            smsService.sendOtpSms(user.getPhone(), otpCode);
        } else {
            emailService.sendOtpEmail(user.getEmail(), otpCode, expiresAt);
        }
    }
}
