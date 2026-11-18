package tn.iset.forum.entity;

import jakarta.persistence.*;

// Classe "Prof" du diagramme = un ENSEIGNANT (rôle ENSEIGNANT, user_type = ENSEIGNANT)
@Entity
@DiscriminatorValue("ENSEIGNANT")
public class Prof extends User {

    protected Prof() {}

    public Prof(String fullName, String email, String password, String registryId) {
        super(fullName, email, password, registryId, Role.ENSEIGNANT);
    }
}
