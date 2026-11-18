package tn.iset.forum.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.iset.forum.dto.ForumDtos.CommentaireView;
import tn.iset.forum.dto.ForumDtos.CommunityView;
import tn.iset.forum.dto.ForumDtos.CreateCommentaire;
import tn.iset.forum.dto.ForumDtos.CreateCommunity;
import tn.iset.forum.dto.ForumDtos.CreatePublication;
import tn.iset.forum.dto.ForumDtos.CreateResource;
import tn.iset.forum.dto.ForumDtos.JoinRequest;
import tn.iset.forum.dto.ForumDtos.PublicationView;
import tn.iset.forum.dto.ForumDtos.ResourceView;
import tn.iset.forum.service.ForumService;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    private final ForumService service;

    public ForumController(ForumService service) {
        this.service = service;
    }

    @PostMapping("/communities")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityView createCommunity(@Valid @RequestBody CreateCommunity request,
                                         @AuthenticationPrincipal Jwt jwt) {
        return service.createCommunity(request, jwt.getSubject());
    }

    @GetMapping("/communities")
    public List<CommunityView> communities(@AuthenticationPrincipal Jwt jwt) {
        return service.listCommunities(jwt.getSubject());
    }

    @PostMapping("/communities/{id}/join")
    public CommunityView join(@PathVariable Long id,
                              @Valid @RequestBody JoinRequest request,
                              @AuthenticationPrincipal Jwt jwt) {
        return service.join(id, request, jwt.getSubject());
    }

    @PostMapping("/publications")
    @ResponseStatus(HttpStatus.CREATED)
    public PublicationView createPublication(@Valid @RequestBody CreatePublication request,
                                             @AuthenticationPrincipal Jwt jwt) {
        return service.createPublication(request, jwt.getSubject());
    }

    @GetMapping("/publications")
    public List<PublicationView> publications(@RequestParam(required = false) Long communityId,
                                              @AuthenticationPrincipal Jwt jwt) {
        return service.listPublications(communityId, jwt.getSubject());
    }

    @GetMapping("/publications/{id}")
    public PublicationView publication(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.getPublication(id, jwt.getSubject());
    }

    @DeleteMapping("/publications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePublication(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.deletePublication(id, jwt.getSubject());
    }

    @PostMapping("/publications/{id}/commentaires")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentaireView addCommentaire(@PathVariable Long id,
                                          @Valid @RequestBody CreateCommentaire request,
                                          @AuthenticationPrincipal Jwt jwt) {
        return service.addCommentaire(id, request, jwt.getSubject());
    }

    @GetMapping("/publications/{id}/commentaires")
    public List<CommentaireView> commentaires(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.listCommentaires(id, jwt.getSubject());
    }

    @PostMapping("/resources")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceView createResource(@Valid @RequestBody CreateResource request,
                                       @AuthenticationPrincipal Jwt jwt) {
        return service.createResource(request, jwt.getSubject());
    }

    @GetMapping("/communities/{id}/resources")
    public List<ResourceView> resources(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.listResources(id, jwt.getSubject());
    }

    @DeleteMapping("/resources/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.deleteResource(id, jwt.getSubject());
    }
}
