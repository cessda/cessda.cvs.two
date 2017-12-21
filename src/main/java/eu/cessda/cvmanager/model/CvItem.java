package eu.cessda.cvmanager.model;

import java.util.List;

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
}
