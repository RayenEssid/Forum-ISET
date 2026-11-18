package tn.iset.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tn.iset.forum.entity.Publication;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface PublicationRepository extends JpaRepository<Publication, Long> {


    List<Publication> findAllByOrderByCreatedAtDesc();

    List<Publication> findByCommunity_IdOrderByCreatedAtDesc(Long communityId);
}
