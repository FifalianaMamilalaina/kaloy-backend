package org.example.mozika.auth;

import org.example.mozika.models.VerificationStatusUser;
import org.example.mozika.repositories.VerificationStatusUserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final VerificationStatusUserRepository verificationStatusUserRepository;

    public DataInitializer(VerificationStatusUserRepository verificationStatusUserRepository) {
        this.verificationStatusUserRepository = verificationStatusUserRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (verificationStatusUserRepository.count() == 0) {
            VerificationStatusUser notVerified = new VerificationStatusUser();
            notVerified.setName("NOT_VERIFIED");
            verificationStatusUserRepository.save(notVerified);

            VerificationStatusUser verified = new VerificationStatusUser();
            verified.setName("VERIFIED");
            verificationStatusUserRepository.save(verified);
        }
    }
}
