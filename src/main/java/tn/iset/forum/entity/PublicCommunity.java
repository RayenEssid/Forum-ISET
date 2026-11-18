package tn.iset.forum.entity;

import jakarta.persistence.*;

// Boîte "public" du diagramme : visible et accessible par tous les comptes connectés
@Entity
@DiscriminatorValue("PUBLIC")
public class PublicCommunity extends Community {

    protected PublicCommunity() {}

    public PublicCommunity(String name, User owner) {
        super(name, owner, false);
    }
}
