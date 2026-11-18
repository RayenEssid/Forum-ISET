package tn.iset.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tn.iset.forum.entity.Role;
import tn.iset.forum.entity.User;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByRoleAndRegistryIdIgnoreCase(Role role, String registryId);

    boolean existsByRole(Role role);
}
