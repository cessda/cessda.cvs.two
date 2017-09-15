package eu.cessda.cvmanager.service.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class is a representation of a user in our system. The class corresponds
 * to a concrete table in the database (the "users" table), where each row is an
 * instance of this class. Accordingly, the user is the tuple (id, username,
 * password, first name, last name, enabled, locked).
 * 
 * @author Karam
 */
@Entity
@Table(name = "user_data")
public class UserData {

	/**
	 * Auto-generated.
	 */
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Unique + not null
	 */
	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	/**
	 * Not null
	 */
	@Column(name = "email", nullable = false)
	private String email;

	/**
	 * Unique + not null
	 */
	@Column(name = "password", nullable = false)
	private String password;

	/**
	 * Not null
	 */
	@Column(name = "affiliation")
	private String affiliation;

	/**
	 * Unique + not null
	 */
	@Column(name = "token")
	private String token;

	/**
	 * Unique + not null
	 */
	@Column(name = "identifier")
	private String identifier;

	/**
	 * Not null
	 */
	@Column(name = "dspaceemail", nullable = false)
	private String dspaceemail;

	/**
	 * not null
	 */
	@Column(name = "dspacepassword", nullable = false)
	private String dspacepassword;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String userName) {
		this.username = userName;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the affiliation
	 */
	public String getAffiliation() {
		return affiliation;
	}

	/**
	 * @param affiliation
	 *            the affiliation to set
	 */
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public final String getPassword() {
		return password;
	}

	public final void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDspaceemail() {
		return dspaceemail;
	}

	public void setDspaceemail(String dspaceemail) {
		this.dspaceemail = dspaceemail;
	}

	public String getDspacepassword() {
		return dspacepassword;
	}

	public void setDspacepassword(String dspacepassword) {
		this.dspacepassword = dspacepassword;
	}

}
