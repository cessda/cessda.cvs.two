package eu.cessda.cvmanager.service.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gesis.stardat.entity.CVScheme;

public class CVSchemeList implements Serializable {

	private static final long serialVersionUID = -7196174485529710467L;
	private List<CVScheme> listOfCVSchemes = new ArrayList<>();

	public List<CVScheme> getListOfCVSchemes() {
		return listOfCVSchemes;
	}

	public void setListOfCVSchemes(List<CVScheme> listOfCVSchemes) {
		this.listOfCVSchemes = listOfCVSchemes;
	}

}
