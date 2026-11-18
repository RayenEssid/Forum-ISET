package tn.iset.forum.entity;

import jakarta.persistence.*;

import java.time.Instant;
@Entity
@Table(name = "study_resources")
public class StudyResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String module;

     @Column(nullable = false, length = 1000)
    private String attachment;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "community_id")
    private Community community;

    protected StudyResource() {}

    public StudyResource(String title, String description, String module,
                         String attachment, User author, Community community) {
        this.title = title;
        this.description = description;
        this.module = module;
        this.attachment = attachment;
        this.author = author;
        this.community = community;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getModule() { return module; }
    public String getAttachment() { return attachment; }
    public Instant getCreatedAt() { return createdAt; }
    public User getAuthor() { return author; }
    public Community getCommunity() { return community; }
}
