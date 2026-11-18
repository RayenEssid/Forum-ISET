package tn.iset.forum.entity;

import jakarta.persistence.*;

// Classe "Student" du diagramme = un ETUDIANT (rôle ETUDIANT, user_type = ETUDIANT)
@Entity
@DiscriminatorValue("ETUDIANT")
public class Student extends User {

    protected Student() {}

    public Student(String fullName, String email, String password, String registryId) {
        super(fullName, email, password, registryId, Role.ETUDIANT);
    }
}
