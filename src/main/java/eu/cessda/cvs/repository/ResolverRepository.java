package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.Resolver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Resolver entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResolverRepository extends JpaRepository<Resolver, Long> {

}
