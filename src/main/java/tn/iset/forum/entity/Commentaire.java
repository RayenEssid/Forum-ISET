package tn.iset.forum.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "commentaires")
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publication_id")
    private Publication publication;


    private boolean reponseEnseignant;

    protected Commentaire() {}

    public Commentaire(String contenu, User author, Publication publication, boolean reponseEnseignant) {
        this.contenu = contenu;
        this.author = author;
        this.publication = publication;
        this.reponseEnseignant = reponseEnseignant;
    }

    public Long getId() { return id; }
    public String getContenu() { return contenu; }
    public Instant getCreatedAt() { return createdAt; }
    public User getAuthor() { return author; }
    public Publication getPublication() { return publication; }
    public boolean isReponseEnseignant() { return reponseEnseignant; }
}
