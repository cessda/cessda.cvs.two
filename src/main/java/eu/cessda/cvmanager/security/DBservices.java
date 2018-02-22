package eu.cessda.cvmanager.security;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cessda.cvmanager.domain.Role;
import eu.cessda.cvmanager.domain.User;
import eu.cessda.cvmanager.repository.RoleRepository;
import eu.cessda.cvmanager.repository.UserRepository;

/**
 * This is a service class that contains all operations or functions that require communicating with the database.
 * More precisely, this class is the high-level version of "RoleReprository" and "UserRepository" classes.
 * The user, who needs any database related functions, should find it in this class.
 * It is highly recommended that you use this class and avoid using the two low-level classes "RoleReprository" and "UserRepository".
 * 
 * @author Karam
 */
@Service
public class DBservices{
	
	@Autowired
	private UserRepository userRep;
	
	@Autowired
	private RoleRepository roleRep;
	
	@Autowired
	private BCryptPasswordEncoder encrypt;
	
	/**
	 * Given a username, get the corresponding user from the database.
	 * @param username: the username of the required user.
	 * @return the corresponding user if exists or null otherwise.
	 */
	public User getUserByUsername(String username){
		return userRep.findByUsername(username);
	}
	
	/**
	 * Given a random username, get the corresponding user from the database.
	 * @param randomUsername: the random username of the required user.
	 * @return the corresponding user if exists or null otherwise.
	 */
	public User getUserByRandomUsername(String randomUsername){
		return userRep.findByRandomUsername(randomUsername);
	}
	
	/**
	 * Given a token, get the corresponding user from the database.
	 * @param token: the random username of the required user.
	 * @return the corresponding user if exists or null otherwise.
	 */
	public User getUserByToken(String token){
		List<User> users = userRep.findByToken(token);
		if(users.isEmpty())
			return null;
		else
			return users.get(0); 
	}
	
	/**
	 * Given a token, get the corresponding user from the database.
	 * @param token: the random token for password reset.
	 * @return the corresponding user if exists or null otherwise.
	 */
	public User getUserByResetToken(String token){
		List<User> users = userRep.findByForgotPasswordToken(token);
		if(users.isEmpty())
			return null;
		else
			return users.get(0); 
	}
	
	/**
	 * Given a username, check if a corresponding user exists.
	 * @param username: the username.
	 * @return true if the user exists or false otherwise.
	 */
	public boolean userExists(String username){
		return ( this.getUserByUsername(username) != null );
	}
	
	/**
	 * Add a new user to the database. This is a transaction method, so either it is fully executed or nothing is happened.
	 * In this method, the user's password is encrypted before insertion, 
	 * so in the input parameter 'u' the password should be a plain-text to avoid encrypting it twice.
	 * @param u: the user to be added.
	 */
	@Transactional
	public void addUser(User u){
		//Encrypt the password
		u.setPassword(encrypt.encode(u.getPassword()));
		//check if the user is already exists
		if(!this.userExists(u.getUsername())){
//			HashSet<Role> rr = new HashSet<>();
//			//for each role of the current user
//			for(Role r : u.getRoles()) {
//				Role r1 =  this.getRoleByName(RoleType.valueOf(r.getName()));
//				rr.add(r1);
//			}
//			u.setRoles(rr);
			
			//save in the database
			userRep.save(u);
		}
	}
	
	@Transactional
	public void persist(User u) {
		userRep.save(u);
	}
	
	/**
	 * Change the password of a given user
	 * @param username: the username of the user who want to change his password
	 * @param newPassword: the new password
	 */
	@Transactional
	public void changePassword(String username, String newPassword){
		//find the user
		User u = this.getUserByUsername(username);
		if(u != null){
			//change its password
			u.setPassword(encrypt.encode(newPassword));
		}
	}
	
	/**
	 * Change the username of a given user
	 * @param oldUsername: the username of the user who want to change his username
	 * @param newUsername: the new username
	 */
	@Transactional
	public void changeUsername(String oldUsername, String newUsername){
		//find the user
		User u = this.getUserByUsername(oldUsername);
		if(u != null){
			//change its password
			u.setUsername(newUsername);
		}
	}
	
	/**
	 * Remove the user that has the given username from the database.
	 * This is a transaction method, so either it is fully executed or nothing is happened.
	 * @param username: the username of the user to be deleted.
	 */
	@Transactional
	public void deleteUser(String username){
		//find the user using its username
		User u = this.getUserByUsername(username);
		if(u != null){
			//remove the user from the database
			userRep.delete(u.getId());
		}
	}
	
	/**
	 * Enable the given user.
	 * This is a transaction method, so either it is fully executed or nothing is happened.
	 * @param username: the username of the user to be enabled..
	 */
	@Transactional
	public void enableUser(String username){
		//find the user
		User u = this.getUserByUsername(username);
		if(u != null){
			//enable the user
			u.setEnable(true);
		}
	}
	
	/**
	 * Disable a given user.
	 * This is a transaction method, so either it is fully executed or nothing is happened.
	 * @param username: the username of the user to be disabled.
	 */
	@Transactional
	public void disableUser(String username){
		//find the user
		User u = this.getUserByUsername(username);
		if(u != null){
			//disable the user
			u.setEnable(false);
		}
	}
	
	/**
	 * Lock the given user.
	 * This is a transaction method, so either it is fully executed or nothing is happened.
	 * @param username: the username of the user to be locked.
	 */
	@Transactional
	public void lockUser(String username){
		//find the user
		User u = this.getUserByUsername(username);
		if(u != null){
			//lock the user
			u.setLocked(true);
		}
	}
	
	/**
	 * Unlock the given user.
	 * This is a transaction method, so either it is fully executed or nothing is happened.
	 * @param username: the username of the user to be unlocked.
	 */
	@Transactional
	public void unlockUser(String username){
		//find the user
		User u = this.getUserByUsername(username);
		if(u != null){
			//unlock the user
			u.setLocked(false);
		}
	}
	
	/**
	 * @return a list of all users exist in the database, namely all rows in the "users" table.
	 */
	public List<User> getAllUsers(){
		return userRep.findAll();
	}
	
	/**
	 * Get the corresponding role of the given RoleType from the database.
	 * @param r: the RoleType that we need to retrieve.
	 * @return the corresponding role if the given RoleType exists in the database or null otherwise.
	 */
	public Role getRoleByName(RoleType r){
		return roleRep.findByName(r.name());
	}
	
	/**
	 * Check if a role of the given RoleType exists in the database.
	 * @param r: the RoleType to be checked.
	 * @return true if a corresponding role exists or false otherwise.
	 */
	public boolean roleExists(RoleType r){
		return ( this.getRoleByName(r) != null );
	}
	
	/**
	 * Add a new role to the database.
	 * @param r: the role to be added.
	 */
	public void addRole(Role r){
		//check if the role exist
		if(!this.roleExists(RoleType.valueOf(r.getName()))){
			roleRep.save(r);
		}
	}
	
	/**
	 * @return a list of all roles in the database, namely all rows of the "roles" table.
	 */
	public List<Role> getAllRoles(){
		return roleRep.findAll();
	}
	
	/**
	 * It is a test method (a greetin method like classical HelloWorld method).
	 * We added this method to check how the GlobalMethodSecurity is working [YOU MUST NOT USE THIS METHOD]
	 * This is a secured method, namely it is tagged with @Secured annotation. 
	 * @param username: the username of the user to whom this greeting is. 
	 * @return a greeting phrase in terms of the given username.
	 */
	//"ROLE_" prefix is mandatory
	//@Secured({"ROLE_USER", "ROLE_ADMIN"})
	/*we re-implemented the AccessDecisionVoter so instead of putting inside the @Secured annotation a list of roles, 
	you could simply put the corresponding property name in the "security.properties" file.
	By this way:
		1- we do not need to fix the roles of a particular method in advance
		2- we have more flexibility and control on the GlobalMethodSecurity mechanism*/
	@Secured("org.gesis.db.hello")
	public String hello(String username){
		User u = this.getUserByUsername(username);
		if(u != null)
			return "Hello $$ " + username + " $$ ...";
		return "Non-identified user ...";
	}
	
	/**
	 * This is a secured method, namely it is tagged with @Secured annotation.
	 * @return a formated string that refers to the number of users for each defined role in the database.
	 */
	//"ROLE_" prefix is mandatory
	//@Secured({"ROLE_ADMIN"})
//	@Secured("org.gesis.db.getNumberOfUsersByRole")
//	public String getNumberOfUsersByRole(){
//		String s = "";
//		RoleType[] rt = RoleType.values();
//		s = s + "#Users ( " + rt[0].toString() + " ) = " + this.getRoleByName(rt[0]).getUsers().size();
//		for(int i = 1; i < rt.length; i++){
//			s = s + " , #Users ( " + rt[i].toString() + " ) = " + this.getRoleByName(rt[i]).getUsers().size();
//		}
//		return s;
//	}
}
