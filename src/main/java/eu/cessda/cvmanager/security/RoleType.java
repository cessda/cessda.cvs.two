package eu.cessda.cvmanager.security;

/**
 * An enumeration of the pre-defined roles in the system.
 * 
 * All roles should have the "ROLE_" prefix for the system to work correctly.
 * 
 * @author Karam
 */
public enum RoleType {
	
	ROLE_ADMIN,
	ROLE_USER,
	ROLE_UNKNOWN,
	ROLE_ANONYMOUS,
	ROLE_ADMIN_SL,
	ROLE_ADMIN_TL;
	
	/**
	 * Remove the "ROLE_" prefix and return only the name. For example, 'RoleType.ROLE_USER.toString()' gives "USER".   
	 */
	public String toString(){
		return this.name().substring(5);
	}
}
