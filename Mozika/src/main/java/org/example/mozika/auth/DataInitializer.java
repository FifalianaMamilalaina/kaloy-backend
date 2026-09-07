package org.example.mozika.auth;

import org.example.mozika.models.VerificationStatusUser;
import org.example.mozika.models.InstrumentRole;
import org.example.mozika.models.MemberStatuse;
import org.example.mozika.repositories.InstrumentRoleRepository;
import org.example.mozika.repositories.MemberStatuseRepository;
import org.example.mozika.repositories.VerificationStatusUserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final VerificationStatusUserRepository verificationStatusUserRepository;
    private final InstrumentRoleRepository instrumentRoleRepository;
    private final MemberStatuseRepository memberStatuseRepository;

    public DataInitializer(VerificationStatusUserRepository verificationStatusUserRepository,
            InstrumentRoleRepository instrumentRoleRepository,
            MemberStatuseRepository memberStatuseRepository) {
        this.verificationStatusUserRepository = verificationStatusUserRepository;
        this.instrumentRoleRepository = instrumentRoleRepository;
        this.memberStatuseRepository = memberStatuseRepository;
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

        String[] defaultRoles = { "Guitare", "Piano", "Batterie", "Basse", "Chant" };
        for (String label : defaultRoles) {
            if (instrumentRoleRepository.findByLabel(label).isEmpty()) {
                InstrumentRole role = new InstrumentRole();
                role.setLabel(label);
                instrumentRoleRepository.save(role);
            }
        }

        String[] memberStatuses = { "ACTIVE", "FORMER" };
        for (String name : memberStatuses) {
            if (memberStatuseRepository.findByName(name).isEmpty()) {
                MemberStatuse status = new MemberStatuse();
                status.setName(name);
                memberStatuseRepository.save(status);
            }
        }
    }
}
