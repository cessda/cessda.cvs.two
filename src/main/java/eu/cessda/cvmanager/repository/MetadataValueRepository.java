package eu.cessda.cvmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import eu.cessda.cvmanager.domain.MetadataValue;
import eu.cessda.cvmanager.domain.enumeration.ObjectType;

public interface MetadataValueRepository extends JpaRepository<MetadataValue, Long> {

	@Query( "select mv from MetadataValue mv where mv.metadataField.metadataKey = :fieldKey and mv.metadataField.objectType = :objectType" )
	List<MetadataValue> findByMetadataField(@Param("fieldKey") String fieldKey, @Param("objectType") ObjectType objectType);

}
