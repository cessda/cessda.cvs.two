package eu.cessda.cvmanager.service.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CodeList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4262088197080322589L;

	private List<Code> listOfCodes = new ArrayList<>();

	public List<Code> getListOfCodes() {
		return listOfCodes;
	}

	public void setListOfCodes(List<Code> listOfCodes) {
		this.listOfCodes = listOfCodes;
	}

}
