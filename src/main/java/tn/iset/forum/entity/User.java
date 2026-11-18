package tn.iset.forum.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "users",
       uniqueConstraints = @UniqueConstraint(name = "uk_role_registry",
                                             columnNames = {"role", "registry_id"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "registry_id", length = 100)
    private String registryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected User() {}

    protected User(String fullName, String email, String password,
                   String registryId, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.registryId = registryId;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRegistryId() { return registryId; }
    public Role getRole() { return role; }
}
