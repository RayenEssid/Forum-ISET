package tn.iset.forum.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.iset.forum.dto.AuthDtos.LoginRequest;
import tn.iset.forum.dto.AuthDtos.RegisterRequest;
import tn.iset.forum.dto.AuthDtos.TokenResponse;
import tn.iset.forum.dto.AuthDtos.UserView;
import tn.iset.forum.entity.Prof;
import tn.iset.forum.entity.Role;
import tn.iset.forum.entity.Student;
import tn.iset.forum.entity.User;
import tn.iset.forum.repository.UserRepository;
import tn.iset.forum.security.JwtService;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository users;
    private final RegistryService registry;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, RegistryService registry,
                       PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.registry = registry;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    @Transactional
    public UserView register(RegisterRequest req) {
        Role role = req.type();
        String id = RegistryService.normalize(req.registryId());
        String email = normalizeEmail(req.email());

        if (role == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type invalide : ETUDIANT ou ENSEIGNANT");
        }

        if (!registry.isEligible(role, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Identifiant introuvable dans le registre de l'ISET");
        }

        if (users.existsByRoleAndRegistryIdIgnoreCase(role, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte existe deja pour cet identifiant");
        }
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email deja utilise");
        }

        String hash = encoder.encode(req.password());
        String name = req.fullName().strip();

        User user = (role == Role.ETUDIANT)
                ? new Student(name, email, hash, id)
                : new Prof(name, email, hash, id);

        return toView(users.save(user));
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        User user = users.findByEmailIgnoreCase(normalizeEmail(req.email())).orElse(null);

        if (user == null || !encoder.matches(req.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
        }
        return new TokenResponse(jwt.createToken(user), "Bearer", jwt.expiresInSeconds());
    }

    @Transactional(readOnly = true)
    public User currentUser(String subject) {
        try {
            return users.findById(Long.parseLong(subject))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Compte introuvable"));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide");
        }
    }

    public UserView toView(User u) {
        return new UserView(u.getId(), u.getFullName(), u.getEmail(), u.getRole());
    }
}
