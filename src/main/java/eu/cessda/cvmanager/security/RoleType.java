package eu.cessda.cvmanager.security;

public enum RoleType {
	
	ROLE_USER,					// default role after registration
	
	// role bound to specific user-agency
	ROLE_AGENCY_VIEWER,
	ROLE_AGENCY_CONTRIBUTOR, 	// SL /& TL (depends on language right assigned)
	ROLE_AGENCY_AUTHOR, 		// SL /& TL (depends on language right assigned)
	ROLE_AGENCY_AUTHOR_ADMIN,   // SL & TL
	
	// role bound to the user
	ROLE_ADMIN_AGENCY,			// agencies superadmin
	ROLE_ADMIN_TECHNICAL,		// system technical admin
	ROLE_ADMIN; 				// system superadmin

	/**
	 * Remove the "ROLE_" prefix and return only the name. For example, 'RoleType.ROLE_USER.toString()' gives "USER".   
	 */
	public String toString(){
		return this.name().substring(5);
	}
}
