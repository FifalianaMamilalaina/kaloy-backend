package org.example.mozika.services;

import jakarta.transaction.Transactional;
import org.example.mozika.dto.ChangeContactRequest;
import org.example.mozika.dto.LoginRequest;
import org.example.mozika.dto.RegisterRequest;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.*;
import org.example.mozika.repositories.*;
import org.example.mozika.services.interfaces.AuthService;
import org.example.mozika.services.interfaces.OtpService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DefaultAuthService implements AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ArtistRepository artistRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserStatuseRepository userStatuseRepository;
    private final ArtistTypeRepository artistTypeRepository;
    private final VerificationStatuseRepository verificationStatuseRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public DefaultAuthService(UserRepository userRepository,
                               ClientRepository clientRepository,
                               ArtistRepository artistRepository,
                               UserRoleRepository userRoleRepository,
                               UserStatuseRepository userStatuseRepository,
                               ArtistTypeRepository artistTypeRepository,
                               VerificationStatuseRepository verificationStatuseRepository,
                               BCryptPasswordEncoder passwordEncoder,
                               OtpService otpService) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.artistRepository = artistRepository;
        this.userRoleRepository = userRoleRepository;
        this.userStatuseRepository = userStatuseRepository;
        this.artistTypeRepository = artistTypeRepository;
        this.verificationStatuseRepository = verificationStatuseRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {

        if (!"CLIENT".equals(request.role()) && !"ARTIST".equals(request.role())) {
            throw new IllegalArgumentException("Le type de compte doit être CLIENT ou ARTIST.");
        }

        boolean hasEmail = request.email() != null && !request.email().isBlank();
        boolean hasPhone = request.phone() != null && !request.phone().isBlank();
        if (!hasEmail && !hasPhone) {
            throw new IllegalArgumentException("Un email ou un numéro de téléphone est requis.");
        }

        if (hasEmail && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte.");
        }
        if (hasPhone && userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Ce numéro est déjà associé à un compte.");
        }

        UserRole role = userRoleRepository.findByName(request.role())
                .orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable : " + request.role()));
        UserStatuse pendingStatus = userStatuseRepository.findByName("pending")
                .orElseThrow(() -> new ResourceNotFoundException("Statut 'pending' introuvable."));

        User user = new User();
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRoleidUserRoles(role);
        user.setStatusidUserStatuses(pendingStatus);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        if ("CLIENT".equals(request.role())) {
            Client client = new Client();
            client.setUseridUsers(user);
            clientRepository.save(client);
        } else {
            if (!"solo".equals(request.artistType()) && !"group".equals(request.artistType())) {
                throw new IllegalArgumentException("Le type d'artiste doit être 'solo' ou 'group'.");
            }
            if (request.stageName() == null || request.stageName().isBlank()) {
                throw new IllegalArgumentException("Le nom de scène est requis.");
            }

            ArtistType artistType = artistTypeRepository.findByName(request.artistType())
                    .orElseThrow(() -> new ResourceNotFoundException("Type d'artiste introuvable : " + request.artistType()));
            VerificationStatuse pendingVerification = verificationStatuseRepository.findByName("pending")
                    .orElseThrow(() -> new ResourceNotFoundException("Statut de vérification 'pending' introuvable."));

            Artist artist = new Artist();
            artist.setUseridUsers(user);
            artist.setArtisttypeidArtistTypes(artistType);
            artist.setStageName(request.stageName());
            artist.setVerificationstatusidVerificationStatuses(pendingVerification);
            artist.setIsCertified(false);
            artist.setCreatedAt(LocalDateTime.now());
            artistRepository.save(artist);
        }

        return user;
    }

    @Override
    public User login(LoginRequest request) {

        User user;
        if (request.email() != null && !request.email().isBlank()) {
            user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new IllegalArgumentException("Identifiant introuvable."));
        } else if (request.phone() != null && !request.phone().isBlank()) {
            user = userRepository.findByPhone(request.phone())
                    .orElseThrow(() -> new IllegalArgumentException("Identifiant introuvable."));
        } else {
            throw new IllegalArgumentException("Un email ou un numéro de téléphone est requis.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Mot de passe incorrect.");
        }

        String status = user.getStatusidUserStatuses().getName();
        if ("pending".equals(status)) {
            throw new IllegalStateException("Votre compte n'est pas encore vérifié. Veuillez valider votre code de vérification.");
        }
        if ("suspended".equals(status)) {
            throw new IllegalStateException("Votre compte a été suspendu.");
        }

        return user;
    }

    @Override
    @Transactional
    public VerificationCode requestContactChange(User user, ChangeContactRequest request) {

        boolean hasNewEmail = request.newEmail() != null && !request.newEmail().isBlank();
        boolean hasNewPhone = request.newPhone() != null && !request.newPhone().isBlank();

        if (!hasNewEmail && !hasNewPhone) {
            throw new IllegalArgumentException("Veuillez fournir un nouvel email ou un nouveau numéro.");
        }
        if (hasNewEmail && hasNewPhone) {
            throw new IllegalArgumentException("Veuillez modifier un seul contact à la fois.");
        }

        String channel = hasNewEmail ? "email" : "sms";
        String destination = hasNewEmail ? request.newEmail() : request.newPhone();

        if (hasNewEmail && userRepository.existsByEmail(destination)) {
            throw new IllegalArgumentException("Cet email est déjà associé à un compte.");
        }
        if (hasNewPhone && userRepository.existsByPhone(destination)) {
            throw new IllegalArgumentException("Ce numéro est déjà associé à un compte.");
        }

        return otpService.generateAndSend(user, channel, destination);
    }
}