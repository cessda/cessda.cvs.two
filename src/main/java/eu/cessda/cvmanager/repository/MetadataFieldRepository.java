package eu.cessda.cvmanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.cessda.cvmanager.domain.MetadataField;

public interface MetadataFieldRepository extends JpaRepository<MetadataField, Long>
{

	boolean existsByMetadataKey( String metadataKey );

	Optional<MetadataField> findByMetadataKey( String metadataKey );
}
