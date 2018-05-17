package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.Concept;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Concept entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConceptRepository extends JpaRepository<Concept, Long> {

}
