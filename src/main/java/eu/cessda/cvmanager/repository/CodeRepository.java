package eu.cessda.cvmanager.repository;

import org.springframework.stereotype.Repository;

import eu.cessda.cvmanager.domain.Code;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Code entity.
 */
@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

}
