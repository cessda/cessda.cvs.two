package eu.cessda.cvmanager.service.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The proxy between the concrete "users" table in the database and our system.
 * 
 * @author Karam
 */
@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {

	/**
	 * Retrieve the user with a given username. We use the following SQL query:
	 * "select u from User u where u.username = username"
	 * 
	 * @param username:
	 *            the username of the user that we need to retrieve.
	 * @return the user of the given username if exists or null otherwise.
	 */
	@Query("select u from UserData u where u.username = :un")
	public UserData findByUsername(@Param("un") String username);

	@Query("select u from UserData u where u.email = :email")
	public List<UserData> findByEmail(@Param("email") String email);

	@Query("select u from UserData u where u.token = :token")
	public List<UserData> findByToken(@Param("token") String token);
}
