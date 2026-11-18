package tn.iset.forum.dto;

import jakarta.validation.constraints.*;
import tn.iset.forum.entity.Role;

public final class AuthDtos {

    private AuthDtos() {}


    public record RegisterRequest(
        @NotNull(message = "type obligatoire : ETUDIANT ou ENSEIGNANT") Role type,
        @NotBlank(message = "identifiant obligatoire") String registryId,
        @NotBlank(message = "nom complet obligatoire") String fullName,
        @NotBlank @Email(message = "email invalide") String email,
        @NotBlank @Size(min = 8, message = "8 caracteres minimum") String password
    ) {}

    public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
    ) {}

    public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds
    ) {}


    public record UserView(
        Long id,
        String fullName,
        String email,
        Role role
    ) {}
}
