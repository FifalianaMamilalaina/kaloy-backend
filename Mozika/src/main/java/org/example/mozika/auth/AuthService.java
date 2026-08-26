package org.example.mozika.auth;

import org.example.mozika.auth.dto.*;

public interface AuthService {
    RegisterResponse registerClient(RegisterClientRequest request);
    RegisterResponse registerArtist(RegisterArtistRequest request);
    AuthResponse verifyOtp(OtpVerifyRequest request);
    String resendOtp(ResendOtpRequest request);
    AuthResponse login(LoginRequest request);
}
