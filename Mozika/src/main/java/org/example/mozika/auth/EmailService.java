package org.example.mozika.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public void sendOtpEmail(String toEmail, String otpCode, LocalDateTime expiresAt) {
        String expiry = expiresAt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Kaloy — Votre code de vérification");
        message.setText(
                "Bonjour,\n\n" +
                "Votre code de vérification Kaloy est :\n\n" +
                "  " + otpCode + "\n\n" +
                "Ce code est valable 10 minutes (jusqu'à " + expiry + ").\n\n" +
                "Si vous n'avez pas demandé ce code, ignorez cet email.\n\n" +
                "— L'équipe Kaloy"
        );

        mailSender.send(message);
    }
}
