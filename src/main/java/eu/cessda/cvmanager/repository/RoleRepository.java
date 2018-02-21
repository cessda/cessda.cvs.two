package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.Role;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

/**
 * The proxy between the concrete "roles" table in the database and our system.
 * 
 * @author Karam
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	/**
	 * Retrieve the role with a given name.
	 * We use the following SQL query: "select r from Role r where r.name = name"
	 * 
	 * @param name: the name of the role that we need to retrieve.
	 * @return the role of the given name if exists or null otherwise.
	 */
	@Query( "select r from Role r where r.name = :n" )
	public Role findByName(@Param("n") String name);
}
