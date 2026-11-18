package tn.iset.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tn.iset.forum.entity.Commentaire;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    List<Commentaire> findByPublication_IdOrderByCreatedAtAsc(Long publicationId);

    long countByPublication_Id(Long publicationId);
}
