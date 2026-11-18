package tn.iset.forum.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.iset.forum.dto.ForumDtos.CommentaireView;
import tn.iset.forum.dto.ForumDtos.CommunityView;
import tn.iset.forum.dto.ForumDtos.CreateCommentaire;
import tn.iset.forum.dto.ForumDtos.CreateCommunity;
import tn.iset.forum.dto.ForumDtos.CreatePublication;
import tn.iset.forum.dto.ForumDtos.CreateResource;
import tn.iset.forum.dto.ForumDtos.JoinRequest;
import tn.iset.forum.dto.ForumDtos.PublicationView;
import tn.iset.forum.dto.ForumDtos.ResourceView;
import tn.iset.forum.entity.Commentaire;
import tn.iset.forum.entity.Community;
import tn.iset.forum.entity.PrivateCommunity;
import tn.iset.forum.entity.PublicCommunity;
import tn.iset.forum.entity.Publication;
import tn.iset.forum.entity.Role;
import tn.iset.forum.entity.StudyResource;
import tn.iset.forum.entity.User;
import tn.iset.forum.repository.CommentaireRepository;
import tn.iset.forum.repository.CommunityRepository;
import tn.iset.forum.repository.PublicationRepository;
import tn.iset.forum.repository.StudyResourceRepository;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ForumService {

    private static final String KEY_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final SecureRandom random = new SecureRandom();

    private final AuthService auth;
    private final CommunityRepository communities;
    private final PublicationRepository publications;
    private final CommentaireRepository commentaires;
    private final StudyResourceRepository resources;

    public ForumService(AuthService auth, CommunityRepository communities,
                        PublicationRepository publications, CommentaireRepository commentaires,
                        StudyResourceRepository resources) {
        this.auth = auth;
        this.communities = communities;
        this.publications = publications;
        this.commentaires = commentaires;
        this.resources = resources;
    }

    private Community findCommunity(Long id) {
        return communities.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Communauté introuvable"));
    }

    private Publication findPublication(Long id) {
        return publications.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Publication introuvable"));
    }

    private boolean canAccess(Community c, User user) {
        if (!c.isPrivate()) return true;
        if (user.getRole() == Role.ADMIN) return true;
        if (c.getOwner().getId().equals(user.getId())) return true;
        return communities.existsByIdAndMembers_Id(c.getId(), user.getId());
    }

    private void requireAccess(Community c, User user) {
        if (!canAccess(c, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé à cette communauté privée");
        }
    }

    private void requireAuthorOrAdmin(User author, User me) {
        if (!author.getId().equals(me.getId()) && me.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas l'auteur");
        }
    }

    private String generateKey() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(KEY_ALPHABET.charAt(random.nextInt(KEY_ALPHABET.length())));
        }
        return sb.toString();
    }

    private CommunityView communityView(Community c, User me) {
        boolean ownerOrAdmin = me.getRole() == Role.ADMIN || c.getOwner().getId().equals(me.getId());
        String key = (c.isPrivate() && ownerOrAdmin) ? c.getAccessKey() : null;
        return new CommunityView(c.getId(), c.getName(), c.isPrivate(),
                c.getOwner().getId(), c.getOwner().getFullName(), key);
    }

    private PublicationView publicationView(Publication p) {
        return new PublicationView(p.getId(), p.getTitle(), p.getContent(), p.getCommunity().getId(),
                p.getAuthor().getId(), p.getAuthor().getFullName(), p.getAuthor().getRole(),
                p.getCreatedAt(), commentaires.countByPublication_Id(p.getId()));
    }

    private CommentaireView commentaireView(Commentaire c) {
        return new CommentaireView(c.getId(), c.getPublication().getId(), c.getContenu(),
                c.getAuthor().getFullName(), c.getAuthor().getRole(),
                c.isReponseEnseignant(), c.getCreatedAt());
    }

    private ResourceView resourceView(StudyResource r) {
        return new ResourceView(r.getId(), r.getCommunity().getId(), r.getTitle(), r.getDescription(),
                r.getModule(), r.getAttachment(), r.getAuthor().getFullName(), r.getCreatedAt());
    }

    @Transactional
    public CommunityView createCommunity(CreateCommunity req, String subject) {
        User me = auth.currentUser(subject);
        String name = req.name().strip();

        Community c = req.privateCommunity()
                ? new PrivateCommunity(name, me, generateKey())
                : new PublicCommunity(name, me);

        c.getMembers().add(me);
        return communityView(communities.save(c), me);
    }

    @Transactional(readOnly = true)
    public List<CommunityView> listCommunities(String subject) {
        User me = auth.currentUser(subject);
        return communities.findAll().stream()
                .filter(c -> canAccess(c, me))
                .map(c -> communityView(c, me))
                .toList();
    }

    @Transactional
    public CommunityView join(Long communityId, JoinRequest req, String subject) {
        User me = auth.currentUser(subject);
        Community c = findCommunity(communityId);

        if (!c.isPrivate()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Communauté publique : aucune clé nécessaire");
        }

        byte[] expected = c.getAccessKey().toUpperCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8);
        byte[] given = req.key().strip().toUpperCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8);

        if (!MessageDigest.isEqual(expected, given)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Clé incorrecte");
        }

        c.getMembers().add(me);
        return communityView(c, me);
    }

    @Transactional
    public PublicationView createPublication(CreatePublication req, String subject) {
        User me = auth.currentUser(subject);
        Community c = findCommunity(req.communityId());
        requireAccess(c, me);

        Publication p = new Publication(req.title().strip(), req.content().strip(), me, c);
        return publicationView(publications.save(p));
    }

    @Transactional(readOnly = true)
    public List<PublicationView> listPublications(Long communityId, String subject) {
        User me = auth.currentUser(subject);

        if (communityId != null) {
            requireAccess(findCommunity(communityId), me);
            return publications.findByCommunity_IdOrderByCreatedAtDesc(communityId).stream()
                    .map(this::publicationView).toList();
        }

        Map<Long, Boolean> cache = new HashMap<>();
        return publications.findAllByOrderByCreatedAtDesc().stream()
                .filter(p -> cache.computeIfAbsent(p.getCommunity().getId(),
                        id -> canAccess(p.getCommunity(), me)))
                .map(this::publicationView)
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicationView getPublication(Long id, String subject) {
        User me = auth.currentUser(subject);
        Publication p = findPublication(id);
        requireAccess(p.getCommunity(), me);
        return publicationView(p);
    }

    @Transactional
    public void deletePublication(Long id, String subject) {
        User me = auth.currentUser(subject);
        Publication p = findPublication(id);
        requireAuthorOrAdmin(p.getAuthor(), me);
        publications.delete(p);
    }

    @Transactional
    public CommentaireView addCommentaire(Long publicationId, CreateCommentaire req, String subject) {
        User me = auth.currentUser(subject);
        Publication p = findPublication(publicationId);
        requireAccess(p.getCommunity(), me);

        boolean reponseEnseignant = me.getRole() == Role.ENSEIGNANT;

        Commentaire c = new Commentaire(req.contenu().strip(), me, p, reponseEnseignant);
        return commentaireView(commentaires.save(c));
    }

    @Transactional(readOnly = true)
    public List<CommentaireView> listCommentaires(Long publicationId, String subject) {
        User me = auth.currentUser(subject);
        Publication p = findPublication(publicationId);
        requireAccess(p.getCommunity(), me);

        return commentaires.findByPublication_IdOrderByCreatedAtAsc(publicationId).stream()
                .map(this::commentaireView).toList();
    }

    @Transactional
    public ResourceView createResource(CreateResource req, String subject) {
        User me = auth.currentUser(subject);
        Community c = findCommunity(req.communityId());
        requireAccess(c, me);

        if (!isHttpUrl(req.attachment())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lien http(s) invalide");
        }

        StudyResource r = new StudyResource(req.title().strip(), req.description(), req.module(),
                req.attachment().strip(), me, c);
        return resourceView(resources.save(r));
    }

    @Transactional(readOnly = true)
    public List<ResourceView> listResources(Long communityId, String subject) {
        User me = auth.currentUser(subject);
        requireAccess(findCommunity(communityId), me);

        return resources.findByCommunity_IdOrderByCreatedAtDesc(communityId).stream()
                .map(this::resourceView).toList();
    }

    @Transactional
    public void deleteResource(Long id, String subject) {
        User me = auth.currentUser(subject);
        StudyResource r = resources.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Ressource introuvable"));
        requireAuthorOrAdmin(r.getAuthor(), me);
        resources.delete(r);
    }

    private boolean isHttpUrl(String value) {
        try {
            URI uri = URI.create(value.strip());
            String scheme = uri.getScheme();
            return uri.getHost() != null
                    && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
