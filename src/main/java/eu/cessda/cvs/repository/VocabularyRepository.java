package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data JPA repository for the Vocabulary entity.
 */
@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long>
{
    List<Vocabulary> findAllByAgencyId( Long agencyId );

    List<Vocabulary> findAllByNotation( String notation );

    boolean existsByNotation( String notation );

    boolean existsByAgencyId( Long agencyId );

    @Query( "select DISTINCT v from Vocabulary v where v.withdrawn IS true" )
    Page<Vocabulary> findAllWithdrawn(Pageable pageable );

    @Query( "select DISTINCT v from Vocabulary v where v.agencyId = :agencyId AND v.withdrawn IS true" )
    Page<Vocabulary> findAllWithdrawn(@Param( "agencyId" ) Long agencyId, Pageable pageable );
}
