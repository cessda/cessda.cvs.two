package eu.cessda.cvmanager.repository;

import eu.cessda.cvmanager.domain.User;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * The proxy between the concrete "users" table in the database and our system.
 * 
 * @author Karam
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("select distinct user from User user left join fetch user.roles")
//    List<User> findAllWithEagerRelationships();
//
//    @Query("select user from User user left join fetch user.roles where user.id =:id")
//    User findOneWithEagerRelationships(@Param("id") Long id);

    /**
	 * Retrieve the user with a given username.
	 * We use the following SQL query: "select u from User u where u.username = username"
	 * 
	 * @param username: the username of the user that we need to retrieve.
	 * @return the user of the given username if exists or null otherwise.
	 */
	@Query( "select u from User u where u.username = :un" )
	public User findByUsername(@Param("un") String username);
	
	/**
	 * Retrieve the user with a given random username.
	 * We use the following SQL query: "select u from User u where u.randomUsername = username"
	 * 
	 * @param username: the random username of the user that we need to retrieve.
	 * @return the user of the given random username if exists or null otherwise.
	 */
	@Query( "select u from User u where u.randomUsername = :un" )
	public User findByRandomUsername(@Param("un") String username);

	/**
	 * Update a particular user and make it enabled.
	 * We use the following SQL query: "update User u set u.enable=1 where u.username = username"
	 * 
	 * @param username: the username of the user that we need to update.
	 * @return the updated user if exists or null otherwise.
	 */
	@Modifying
	@Query( "update User u set u.enable=1 where u.username = :un" )
	public User enableByUsername(@Param("un") String username);
	
	/**
	 * Update a particular user and make it disabled.
	 * We use the following SQL query: "update User u set u.enable=0 where u.username = username"
	 * 
	 * @param username: the username of the user that we need to update.
	 * @return the updated user if exists or null otherwise.
	 */
	@Modifying
	@Query( "update User u set u.enable=0 where u.username = :un" )
	public User disableByUsername(@Param("un") String username);
	
	/**
	 * Update a particular user and make it locked.
	 * We use the following SQL query: "update User u set u.locked=1 where u.username = username"
	 * 
	 * @param username: the username of the user that we need to update.
	 * @return the updated user if exists or null otherwise.
	 */
	@Modifying
	@Query( "update User u set u.locked=1 where u.username = :un" )
	public User lockByUsername(@Param("un") String username);
	
	/**
	 * Update a particular user and make it unlocked.
	 * We use the following SQL query: "update User u set u.locked=0 where u.username = username"
	 * 
	 * @param username: the username of the user that we need to update.
	 * @return the updated user if exists or null otherwise.
	 */
	@Modifying
	@Query( "update User u set u.locked=0 where u.username = :un" )
	public User unlockByUsername(@Param("un") String username);
	
	/**
	 * Update a particular user and change its password.
	 * We use the following SQL query: "update User u set u.password=password where u.username = username"
	 * 
	 * @param username: the username of the user that we need to update.
	 * @param password: the new password
	 * @return the updated user if exists or null otherwise
	 */
	@Modifying
	@Query( "update User u set u.password=:pw where u.username = :un" )
	public User changeUserPassword(@Param("un") String username, @Param("pw") String password);


	@Query( "select u from User u where u.token = :token" )
	public List<User> findByToken( @Param( "token" ) String token );
	
	//forgot password token
	@Query( "select u from User u where u.forgottoken = :token" )
	public List<User> findByForgotPasswordToken( @Param( "token" ) String token );

	@Query( "select u from User u where u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword%" )
	public List<User> findAll(@Param("keyword") String keyword);
}
