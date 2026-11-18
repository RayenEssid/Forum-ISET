package tn.iset.forum.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("PRIVATE")
public class PrivateCommunity extends Community {

    @Column(name = "access_key", length = 64)
    private String key;

    protected PrivateCommunity() {}

    public PrivateCommunity(String name, User owner, String key) {
        super(name, owner, true);
        this.key = key;
    }

    @Override
    public String getAccessKey() { return key; }
}
