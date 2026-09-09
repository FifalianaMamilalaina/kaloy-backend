package org.example.mozika.services;

import jakarta.transaction.Transactional;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.User;
import org.example.mozika.models.UserStatuse;
import org.example.mozika.models.VerificationChannel;
import org.example.mozika.models.VerificationCode;
import org.example.mozika.repositories.UserStatuseRepository;
import org.example.mozika.repositories.VerificationChannelRepository;
import org.example.mozika.repositories.VerificationCodeRepository;
import org.example.mozika.services.interfaces.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Service
public class DefaultOtpService implements OtpService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultOtpService.class);
    private static final int EXPIRATION_MINUTES = 10;
    private static final int RESEND_DELAY_SECONDS = 60;

    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationChannelRepository verificationChannelRepository;
    private final UserStatuseRepository userStatuseRepository;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public DefaultOtpService(VerificationCodeRepository verificationCodeRepository,
                              VerificationChannelRepository verificationChannelRepository,
                              UserStatuseRepository userStatuseRepository,
                              EmailService emailService) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.verificationChannelRepository = verificationChannelRepository;
        this.userStatuseRepository = userStatuseRepository;
        this.emailService = emailService;
    }

    private Optional<VerificationCode> findLatestActiveCode(User user) {
        return verificationCodeRepository.findByUseridUsers(user).stream()
                .filter(vc -> vc.getConsumedAt() == null)
                .max(Comparator.comparing(VerificationCode::getCreatedAt));
    }

    @Override
    @Transactional
    public VerificationCode generateAndSend(User user, String channel, String destination) {

        findLatestActiveCode(user).ifPresent(lastCode -> {
            long secondsSinceLastCode = Duration.between(lastCode.getCreatedAt(), LocalDateTime.now()).getSeconds();
            if (secondsSinceLastCode < RESEND_DELAY_SECONDS) {
                throw new IllegalStateException(
                        "Veuillez patienter avant de demander un nouveau code (" +
                        (RESEND_DELAY_SECONDS - secondsSinceLastCode) + "s restantes)."
                );
            }
        });

        VerificationChannel verificationChannel = verificationChannelRepository.findByName(channel)
                .orElseThrow(() -> new ResourceNotFoundException("Canal de vérification introuvable : " + channel));

        String code = generateSixDigitCode();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUseridUsers(user);
        verificationCode.setChannelidVerificationChannels(verificationChannel);
        verificationCode.setDestination(destination);
        verificationCode.setCode(code);
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
        verificationCode.setCreatedAt(LocalDateTime.now());

        verificationCode = verificationCodeRepository.save(verificationCode);

        if ("email".equals(channel)) {
            emailService.sendOtpEmail(destination, code);
            logger.info("Code OTP envoyé par email à {} (expire dans {} min)", destination, EXPIRATION_MINUTES);
        } else {
            logger.info("=== [SIMULATION SMS] Code envoyé par {} à {} : {} (expire dans {} min) ===",
                    channel, destination, code, EXPIRATION_MINUTES);
        }

        return verificationCode;
    }

    @Override
    public VerificationCode generateAndSend(User user) {
        String channel = user.getEmail() != null ? "email" : "sms";
        String destination = user.getEmail() != null ? user.getEmail() : user.getPhone();
        return generateAndSend(user, channel, destination);
    }

    @Override
    @Transactional
    public User validateCode(User user, String submittedCode) {

        VerificationCode verificationCode = findLatestActiveCode(user)
                .orElseThrow(() -> new IllegalStateException("Aucun code de vérification actif. Veuillez en demander un nouveau."));

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Ce code a expiré. Veuillez en demander un nouveau.");
        }

        if (!verificationCode.getCode().equals(submittedCode)) {
            throw new IllegalArgumentException("Code incorrect.");
        }

        verificationCode.setConsumedAt(LocalDateTime.now());
        verificationCodeRepository.save(verificationCode);

        String destination = verificationCode.getDestination();
        String channelName = verificationCode.getChannelidVerificationChannels().getName();

        if ("email".equals(channelName)) {
            user.setEmail(destination);
            user.setEmailVerifiedAt(LocalDateTime.now());
        } else {
            user.setPhone(destination);
            user.setPhoneVerifiedAt(LocalDateTime.now());
        }

        if ("pending".equals(user.getStatusidUserStatuses().getName())) {
            UserStatuse activeStatus = userStatuseRepository.findByName("active")
                    .orElseThrow(() -> new ResourceNotFoundException("Statut 'active' introuvable."));
            user.setStatusidUserStatuses(activeStatus);
        }

        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }

    private String generateSixDigitCode() {
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }
}