package tn.iset.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tn.iset.forum.entity.StudyResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface StudyResourceRepository extends JpaRepository<StudyResource, Long> {

    List<StudyResource> findByCommunity_IdOrderByCreatedAtDesc(Long communityId);
}
