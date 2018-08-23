package eu.cessda.cvmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.cessda.cvmanager.domain.MetadataValue;

public interface MetadataValueRepository extends JpaRepository<MetadataValue, Long> {

}
