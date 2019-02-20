package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.Resolver;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Resolver entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResolverRepository extends JpaRepository<Resolver, Long> {
	
	Optional<Resolver> findByResolverURI( String resolverUri );
}
