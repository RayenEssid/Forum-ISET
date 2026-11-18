package tn.iset.forum.entity;

import jakarta.persistence.*;

// L'admin n'a pas d'identifiant de registre (registryId = null)
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    protected Admin() {}

    public Admin(String fullName, String email, String password) {
        super(fullName, email, password, null, Role.ADMIN);
    }
}
