package eu.cessda.cvmanager.service.controller;

import java.io.Serializable;
import java.util.ArrayList;

import org.gesis.stardat.entity.CVScheme;

public class CVSchemeList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7196174485529710467L;
	private ArrayList<CVScheme> listOfCVSchemes = new ArrayList<CVScheme>();

	public ArrayList<CVScheme> getListOfCVSchemes() {
		return listOfCVSchemes;
	}

	public void setListOfCVSchemes(ArrayList<CVScheme> listOfCVSchemes) {
		this.listOfCVSchemes = listOfCVSchemes;
	}

}
