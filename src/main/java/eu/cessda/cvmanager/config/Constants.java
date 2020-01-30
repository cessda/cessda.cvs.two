package eu.cessda.cvmanager.config;

/**
 * Application constants.
 */
public final class Constants
{

	// Regex for acceptable logins
	public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

	public static final String DEFAULT_LANGUAGE = "en";

	public static final String NOTATION_REGEX = "[^A-Za-z0-9-+:]";

	// Role
	protected static final String[] USER_ROLE = {
			"ROLE_ADMIN_AGENCY", // agencies superadmin
			"ROLE_ADMIN_TECHNICAL", // system technical admin
			"ROLE_ADMIN" };

	protected static final String[] USER_AGENCY_ROLE = {
			"ROLE_AGENCY_VIEWER",
			"ROLE_AGENCY_CONTRIBUTOR", // SL /& TL (depends on language right assigned)
			"ROLE_AGENCY_AUTHOR", // SL /& TL (depends on language right assigned)
			"ROLE_AGENCY_AUTHOR_ADMIN" // SL & TL
	};

	public static final String REQUIRED = "required";
	public static final String REQUIRED_FIELD_INFO = "* required field, require an input with at least 2 characters";
	public static final String TMPDIR = System.getProperty( "java.io.tmpdir" );

	private Constants()
	{
		throw new IllegalStateException( "Utility class!" );
	}
}
