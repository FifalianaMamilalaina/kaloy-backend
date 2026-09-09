package org.example.mozika.services.interfaces;

import org.example.mozika.dto.ChangeContactRequest;
import org.example.mozika.dto.LoginRequest;
import org.example.mozika.dto.RegisterRequest;
import org.example.mozika.models.User;
import org.example.mozika.models.VerificationCode;

public interface AuthService {
    User register(RegisterRequest request);
    User login(LoginRequest request);
    VerificationCode requestContactChange(User user, ChangeContactRequest request);
}