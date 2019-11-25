package eu.cessda.cvmanager.service.es.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class DDIStoreES implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3017242117847322545L;

	@Id
	private Long primaryKey;

	private String type;

	private String content;

	private String parentIdentifier;

	private Integer elementOrder;

	private String study;

	private String elementId;

	private String studyId;

	private String htmlResultLong;
	private String htmlResultShort;
	private String htmlDetail;

	public DDIStoreES() {

	}

	public DDIStoreES(String elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getElementOrder() {
		return elementOrder;
	}

	public void setElementOrder(Integer elementOrder) {
		this.elementOrder = elementOrder;
	}

	public String getStudy() {
		return study;
	}

	public void setStudy(String study) {
		this.study = study;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public Long getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Long primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getStudyId() {
		return studyId;
	}

	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

	/**
	 * @return the htmlResultLong
	 */
	public String getHtmlResultLong() {
		return htmlResultLong;
	}

	/**
	 * @param htmlResultLong
	 *            the htmlResultLong to set
	 */
	public void setHtmlResultLong(String htmlResultLong) {
		this.htmlResultLong = htmlResultLong;
	}

	/**
	 * @return the htmlResultShort
	 */
	public String getHtmlResultShort() {
		return htmlResultShort;
	}

	/**
	 * @param htmlResultShort
	 *            the htmlResultShort to set
	 */
	public void setHtmlResultShort(String htmlResultShort) {
		this.htmlResultShort = htmlResultShort;
	}

	/**
	 * @return the htmlDetail
	 */
	public String getHtmlDetail() {
		return htmlDetail;
	}

	/**
	 * @param htmlDetail
	 *            the htmlDetail to set
	 */
	public void setHtmlDetail(String htmlDetail) {
		this.htmlDetail = htmlDetail;
	}

	public String getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(String parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}
}
