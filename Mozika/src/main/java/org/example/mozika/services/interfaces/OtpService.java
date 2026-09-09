package org.example.mozika.services.interfaces;

import org.example.mozika.models.User;
import org.example.mozika.models.VerificationCode;

public interface OtpService {
    VerificationCode generateAndSend(User user, String channel, String destination);
    VerificationCode generateAndSend(User user);
    User validateCode(User user, String submittedCode);
}