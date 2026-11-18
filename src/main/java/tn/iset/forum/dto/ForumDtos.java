package tn.iset.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tn.iset.forum.entity.Role;

import java.time.Instant;


public final class ForumDtos {

    private ForumDtos() {}

    //Communautés
    public record CreateCommunity(
        @NotBlank @Size(max = 120) String name,
        boolean privateCommunity
    ) {}

    public record JoinRequest(@NotBlank String key) {}

    // "key" n'est renvoyée qu'au propriétaire (et à l'admin) ; sinon null
    public record CommunityView(
        Long id, String name, boolean privateCommunity,
        Long ownerId, String ownerName, String key
    ) {}

    //Publications
    public record CreatePublication(
        @NotNull Long communityId,
        @NotBlank @Size(max = 180) String title,
        @NotBlank String content
    ) {}

    public record PublicationView(
        Long id, String title, String content, Long communityId,
        Long authorId, String authorName, Role authorRole,
        Instant createdAt, long nbCommentaires
    ) {}

    public record CreateCommentaire(@NotBlank String contenu) {}

    public record CommentaireView(
        Long id, Long publicationId, String contenu,
        String authorName, Role authorRole,
        boolean reponseEnseignant, Instant createdAt
    ) {}


    public record CreateResource(
        @NotNull Long communityId,
        @NotBlank @Size(max = 180) String title,
        String description,
        String module,
        @NotBlank String attachment
    ) {}

    public record ResourceView(
        Long id, Long communityId, String title, String description,
        String module, String attachment, String authorName, Instant createdAt
    ) {}
}
