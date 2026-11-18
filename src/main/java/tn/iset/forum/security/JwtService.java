package tn.iset.forum.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import tn.iset.forum.entity.User;

import java.time.Duration;
import java.time.Instant;

/** Fabrique le JWT apres un login reussi (la verification est faite par Spring Security). */
@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final String issuer;
    private final long minutes;

    public JwtService(JwtEncoder encoder,
                      @Value("${app.jwt.issuer}") String issuer,
                      @Value("${app.jwt.minutes}") long minutes) {
        this.encoder = encoder;
        this.issuer = issuer;
        this.minutes = minutes;
    }

    public String createToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.getId().toString())          // identifiant du compte
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofMinutes(minutes)))
                .claim("role", user.getRole().name())      // ETUDIANT / ENSEIGNANT / ADMIN
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long expiresInSeconds() {
        return minutes * 60;
    }
}
