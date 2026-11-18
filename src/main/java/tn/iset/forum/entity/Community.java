package tn.iset.forum.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "communities")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "community_type")
public abstract class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "is_private", nullable = false)
    private boolean privateCommunity;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany
    @JoinTable(name = "community_members",
               joinColumns = @JoinColumn(name = "community_id"),
               inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members = new HashSet<>();

    protected Community() {}

    protected Community(String name, User owner, boolean privateCommunity) {
        this.name = name;
        this.owner = owner;
        this.privateCommunity = privateCommunity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public boolean isPrivate() { return privateCommunity; }
    public User getOwner() { return owner; }
    public Set<User> getMembers() { return members; }

    public String getAccessKey() { return null; }
}
