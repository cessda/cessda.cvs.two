package eu.cessda.cvmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.cessda.cvmanager.domain.MetadataField;

public interface MetadataFieldRepository extends JpaRepository<MetadataField, Long> {

}
