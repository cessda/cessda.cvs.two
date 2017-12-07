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

	public ContentType getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = new ContentType(ContentType.TYPE_URI);
		this.url.setValue(url);

	}

	public ContentType getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = new ContentType(ContentType.TYPE_LITERAL);
		this.code.setValue(code);
	}

	public ContentType getPrefLabel() {
		return prefLabel;
	}

	public void setPrefLabel(String prefLabel) {
		this.prefLabel = new ContentType(ContentType.TYPE_LITERAL);
		this.prefLabel.setValue(prefLabel);
	}

	public ContentType getLanguagePrefLabel() {
		return languagePrefLabel;
	}

	public void setLanguagePrefLabel(String languagePrefLabel) {
		this.languagePrefLabel = new ContentType(ContentType.TYPE_LITERAL);
		this.languagePrefLabel.setValue(languagePrefLabel);
	}

	public ContentType getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = new ContentType(ContentType.TYPE_LITERAL);
		this.language.setValue(language);
	}

	public ContentType getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = new ContentType(ContentType.TYPE_LITERAL);
		this.description.setValue(description);
	}

}
