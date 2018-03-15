package eu.cessda.cvmanager.model;

import java.util.List;

import org.gesis.wts.service.dto.LanguageRightDTO;
import org.gesis.wts.service.dto.UserAgencyDTO;
import org.gesis.wts.service.dto.UserAgencyRoleDTO;

public class AgencyMemberItem {
	private UserAgencyDTO userAgency;
	private List<UserAgencyRoleDTO> userAgencyRoles;
	private List<LanguageRightDTO> languageRights;
	
	public AgencyMemberItem(UserAgencyDTO userAgency) {
		this.userAgency = userAgency;
	}
	public UserAgencyDTO getUserAgency() {
		return userAgency;
	}
	public void setUserAgency(UserAgencyDTO userAgency) {
		this.userAgency = userAgency;
	}
	public List<UserAgencyRoleDTO> getUserAgencyRoles() {
		return userAgencyRoles;
	}
	public void setUserAgencyRoles(List<UserAgencyRoleDTO> userAgencyRoles) {
		this.userAgencyRoles = userAgencyRoles;
	}
	public List<LanguageRightDTO> getLanguageRights() {
		return languageRights;
	}
	public void setLanguageRights(List<LanguageRightDTO> languageRights) {
		this.languageRights = languageRights;
	}
}
