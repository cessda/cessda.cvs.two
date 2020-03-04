package eu.cessda.cvmanager.service;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;

public class Concept implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6411665223869437017L;

	private URI URI;
	
	private Map<String, String> prefLabel = new HashMap<>();
	

	private Map<String, String> altLabel = new HashMap<>();
	
	private Map<String, String> definition = new HashMap<>();
	


	public Map<String, String> getPrefLabel() {
		return prefLabel;
	}


	public void setPrefLabel(Map<String, String> prefLabel) {
		this.prefLabel = prefLabel;
	}


	public Map<String, String> getAltLabel() {
		return altLabel;
	}


	public void setAltLabel(Map<String, String> altLabel) {
		this.altLabel = altLabel;
	}


	public URI getURI() {
		return URI;
	}


	public void setURI(URI uRI) {
		URI = uRI;
	}
	
	public String getAltLabelByLanguage(String language) {
		return getAltLabel().get(language);
	}
	
	public String getPrefLabelByLanguage(String language) {
		return getPrefLabel().get(language);
	}


	public Map<String, String> getDefinition() {
		return definition;
	}


	public void setDefinition(Map<String, String> definition) {
		this.definition = definition;
	}
	
	public String getDefinitionByLanguage(String language) {
		return getDefinition().get(language);
	}


	


	

}
