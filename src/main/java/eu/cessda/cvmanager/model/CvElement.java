package eu.cessda.cvmanager.model;

import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.ui.CustomComponent;

public class CvElement{
	private String languageIso;
	private String title;
	private String description;
	private String version;
	private String publicationDate;
	
	public CvElement(String languageIso, String title, String description, String version) {
		this.languageIso = languageIso;
		this.title = title;
		this.description = description;
		this.version = version;
	}
	
	public CvElement(String languageIso, String title, String description, String version, String date) {
		this(languageIso, title, description, version );
		this.publicationDate = date;
	}
	public String getLanguageIso() {
		return languageIso;
	}
	public void setLanguageIso(String languageIso) {
		this.languageIso = languageIso;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getPublicationDate() {
		return publicationDate;
	}
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
	
}
