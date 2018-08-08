package eu.cessda.cvmanager.model;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;

import com.vaadin.data.TreeData;

import eu.cessda.cvmanager.service.dto.CodeDTO;

public class CvItem {
	private CVScheme cvScheme;
	// for selected concept
	private CVConcept cvConcept;
	private TreeData<CVConcept> cvConceptTreeData;
	private TreeData<CodeDTO> cvCodeTreeData;
	
	private String currentCvId;
	private String currentNotation;
	private String currentConceptId;
	private String currentLanguage;
	
	public CVScheme getCvScheme() {
		return cvScheme;
	}
	public void setCvScheme(CVScheme cvScheme) {
		this.cvScheme = cvScheme;
	}
	public TreeData<CVConcept> getCvConceptTreeData() {
		return cvConceptTreeData;
	}
	public void setCvConceptTreeData(TreeData<CVConcept> cvCodeTreeData) {
		this.cvConceptTreeData = cvCodeTreeData;
	}

	public Stream<CVConcept> getFlattenedCvConceptStreams(){
		return cvConceptTreeData
				.getRootItems()
				.stream()
				.flatMap( x -> flattened(x));
	}
	
	public Map<String, CVConcept> getCVConceptMap(){
		return getFlattenedCvConceptStreams()
				.filter( c -> c.getNotation() != null) // remove concept that does not have code notation
				.collect(Collectors.toMap( CVConcept::getNotation, c -> c));
	}
	
	private Stream<CVConcept> flattened( CVConcept cvConcept){
		return Stream.concat(
				Stream.of( cvConcept ), 
				cvConceptTreeData
					.getChildren(cvConcept)
						.stream()
						.flatMap( x -> flattened(x)) 
				);
	}
	public String getCurrentNotation() {
		return currentNotation;
	}
	public void setCurrentNotation(String currentNotation) {
		this.currentNotation = currentNotation;
	}
	public String getCurrentCvId() {
		return currentCvId;
	}
	public void setCurrentCvId(String currentCvId) {
		this.currentCvId = currentCvId;
	}
	public String getCurrentConceptId() {
		return currentConceptId;
	}
	public void setCurrentConceptId(String currentConceptId) {
		this.currentConceptId = currentConceptId;
	}
	public String getCurrentLanguage() {
		return currentLanguage;
	}
	public void setCurrentLanguage(String currentLanguage) {
		this.currentLanguage = currentLanguage;
	}
	public CVConcept getCvConcept() {
		return cvConcept;
	}
	public void setCvConcept(CVConcept cvConcept) {
		this.cvConcept = cvConcept;
	}
	public TreeData<CodeDTO> getCvCodeTreeData() {
		return cvCodeTreeData;
	}
	public void setCvCodeTreeData(TreeData<CodeDTO> cvCodeTreeData) {
		this.cvCodeTreeData = cvCodeTreeData;
	}
	
}
