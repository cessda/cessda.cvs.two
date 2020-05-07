package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.MetadataField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the MetadataField entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MetadataFieldRepository extends JpaRepository<MetadataField, Long> {

}
