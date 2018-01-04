package eu.cessda.cvmanager.model;

import java.util.List;
import java.util.stream.Stream;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;

import com.vaadin.data.TreeData;

public class CvItem {
	private CVScheme cvScheme;
	private TreeData<CVConcept> cvCodeTreeData;
	
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
	
	private Stream<CVConcept> flattened( CVConcept cvConcept){
		return Stream.concat(
				Stream.of( cvConcept ), 
				cvCodeTreeData
					.getChildren(cvConcept)
						.stream()
						.flatMap( x -> flattened(x)) 
				);
	}
}
