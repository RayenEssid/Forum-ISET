package tn.iset.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tn.iset.forum.entity.Community;

@RepositoryRestResource(exported = false)
public interface CommunityRepository extends JpaRepository<Community, Long> {

    boolean existsByIdAndMembers_Id(Long communityId, Long userId);
}
