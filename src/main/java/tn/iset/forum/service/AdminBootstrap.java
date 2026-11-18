package tn.iset.forum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tn.iset.forum.entity.Admin;
import tn.iset.forum.entity.Role;
import tn.iset.forum.repository.UserRepository;

import java.util.Locale;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final String email;
    private final String password;

    public AdminBootstrap(UserRepository users, PasswordEncoder encoder,
                          @Value("${app.admin.email}") String email,
                          @Value("${app.admin.password}") String password) {
        this.users = users;
        this.encoder = encoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (users.existsByRole(Role.ADMIN)) return;

        if (email.isBlank() || password.length() < 8) {
            throw new IllegalStateException("Configure app.admin.email et app.admin.password (8 caracteres min)");
        }

        users.save(new Admin("Administrateur ISET",
                email.strip().toLowerCase(Locale.ROOT),
                encoder.encode(password)));

        System.out.println(">>> Compte admin initial cree : " + email);
    }
}
