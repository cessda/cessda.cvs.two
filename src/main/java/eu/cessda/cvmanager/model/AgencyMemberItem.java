package eu.cessda.cvmanager.model;

public class AgencyMemberItem {
	private String name;
	private String username;
	private String role;
	private String language;
	
	public AgencyMemberItem(String name, String username, String role, String language) {
		this.name = name;
		this.username = username;
		this.role = role;
		this.language = language;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
}
