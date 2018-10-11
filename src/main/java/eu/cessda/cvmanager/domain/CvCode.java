package eu.cessda.cvmanager.domain;

import javax.validation.constraints.NotNull;

public class CvCode{
	@NotNull
	private String code;
	@NotNull
	private String term;
	private String definition;
	// the "code" attribute of CvCode parent
	private String parent;
	// the position of the code (positive integer)
	private Integer position;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	
}