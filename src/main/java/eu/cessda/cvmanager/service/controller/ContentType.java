package eu.cessda.cvmanager.service.controller;

public class ContentType {

	public static final String TYPE_URI = "uri";
	public static final String TYPE_LITERAL = "literal";

	private String type;
	private String value;

	public ContentType() {

	}

	public ContentType(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
