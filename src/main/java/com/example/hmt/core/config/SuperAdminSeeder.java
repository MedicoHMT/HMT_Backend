package com.example.hmt.core.config;


import com.example.hmt.core.auth.model.SuperAdmin;
import com.example.hmt.core.auth.repository.SuperAdminRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.util.Optional;

@Component
public class SuperAdminSeeder implements ApplicationRunner {
    private final SuperAdminRepository superAdminRepo;
    private final Environment env;

    public SuperAdminSeeder(SuperAdminRepository superAdminRepo, Environment env) {
        this.superAdminRepo = superAdminRepo;
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (superAdminRepo.count() > 0) {
            System.out.println("SuperAdmin already present, skipping seed.");
            return;
        }

        boolean useAws = Boolean.parseBoolean(env.getProperty("USE_AWS_SECRETS", "false"));
        String email = env.getProperty("SUPERADMIN_EMAIL");

        // For local we read SUPERADMIN_EMAIL from properties; AWS support is omitted here.
        if (email == null || email.isBlank()) {
            System.out.println("No SUPERADMIN_EMAIL set; skipping seeder.");
            return;
        }

        Optional<SuperAdmin> existing = superAdminRepo.findByEmail(email);
        if (existing.isPresent()) {
            System.out.println("SuperAdmin email already exists; skipping: " + email);
            return;
        }

        superAdminRepo.save(new SuperAdmin(email));
        System.out.println("Seeded initial SuperAdmin: " + email);
    }
}
