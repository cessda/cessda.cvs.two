package eu.cessda.cvmanager.service.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Code {

	private ContentType url;

	private ContentType code;

	private ContentType prefLabel;

	private ContentType languagePrefLabel;

	private ContentType language;

	private ContentType description;

	public Code() {

	}

	public ContentType getUrl() {
		return url;
	}

	public void setUrl(ContentType url) {
		this.url = url;
	}

	public ContentType getCode() {
		return code;
	}

	public void setCode(ContentType code) {
		this.code = code;
	}

	public ContentType getPrefLabel() {
		return prefLabel;
	}

	public void setPrefLabel(ContentType prefLabel) {
		this.prefLabel = prefLabel;
	}

	public ContentType getLanguagePrefLabel() {
		return languagePrefLabel;
	}

	public void setLanguagePrefLabel(ContentType languagePrefLabel) {
		this.languagePrefLabel = languagePrefLabel;
	}

	public ContentType getLanguage() {
		return language;
	}

	public void setLanguage(ContentType language) {
		this.language = language;
	}

	public ContentType getDescription() {
		return description;
	}

	public void setDescription(ContentType description) {
		this.description = description;
	}

}
