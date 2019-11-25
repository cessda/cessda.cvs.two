package eu.cessda.cvmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cessda.cvmanager.domain.Concept;

/**
 * Spring Data JPA repository for the Concept entity.
 */
@SuppressWarnings( "unused" )
@Repository
public interface ConceptRepository extends JpaRepository<Concept, Long>
{

	@Query( "select c from Concept c where c.codeId = :codeId" )
	List<Concept> findAllByCode( @Param( "codeId" ) Long codeId );

	@Query( "select c from Concept c where c.codeId = :codeId and c.notation = :notation" )
	Concept findOneByCodeNotationAndId( @Param( "notation" ) String notation, @Param( "codeId" ) Long codeId );

	@Query( "select c from Concept c where c.version.id = :versionId" )
	List<Concept> findByVersion( @Param( "versionId" ) Long versionId );

}
