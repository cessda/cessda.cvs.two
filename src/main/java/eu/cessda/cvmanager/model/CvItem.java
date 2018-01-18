package eu.cessda.cvmanager.model;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;

import com.vaadin.data.TreeData;

public class CvItem {
	private CVScheme cvScheme;
	// for selected concept
	private CVConcept cvConcept;
	private TreeData<CVConcept> cvCodeTreeData;
	
	private String currentCvId;
	private String currentConceptId;
	private String currentLanguage;
	
	public CVScheme getCvScheme() {
		return cvScheme;
	}
	public void setCvScheme(CVScheme cvScheme) {
		this.cvScheme = cvScheme;
	}
	public TreeData<CVConcept> getCvCodeTreeData() {
		return cvCodeTreeData;
	}
	public void setCvCodeTreeData(TreeData<CVConcept> cvCodeTreeData) {
		this.cvCodeTreeData = cvCodeTreeData;
	}

	public Stream<CVConcept> getFlattenedCvConceptStreams(){
		return cvCodeTreeData
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
				cvCodeTreeData
					.getChildren(cvConcept)
						.stream()
						.flatMap( x -> flattened(x)) 
				);
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

}
