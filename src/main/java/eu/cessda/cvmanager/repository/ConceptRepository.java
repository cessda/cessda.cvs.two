package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.Concept;
import eu.cessda.cvmanager.service.dto.ConceptDTO;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;


/**
 * Spring Data JPA repository for the Concept entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConceptRepository extends JpaRepository<Concept, Long> {

	@Query( "select c from Concept c where c.codeId = :codeId" )
	List<Concept> findAllByVocabulary(@Param("codeId") Long codeId);

}
