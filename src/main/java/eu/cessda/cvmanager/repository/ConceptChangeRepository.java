package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.ConceptChange;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ConceptChange entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConceptChangeRepository extends JpaRepository<ConceptChange, Long> {

}
