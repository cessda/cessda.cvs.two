package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.VocabularyChange;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the VocabularyChange entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VocabularyChangeRepository extends JpaRepository<VocabularyChange, Long> {

}
