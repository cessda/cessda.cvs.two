package eu.cessda.cvs.repository;

import eu.cessda.cvs.domain.MetadataValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the MetadataValue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MetadataValueRepository extends JpaRepository<MetadataValue, Long> {

}
