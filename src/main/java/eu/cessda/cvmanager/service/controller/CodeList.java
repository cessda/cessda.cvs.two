package eu.cessda.cvmanager.service.controller;

import java.io.Serializable;
import java.util.ArrayList;

public class CodeList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4262088197080322589L;

	private ArrayList<Code> listOfCodes = new ArrayList<Code>();

	public ArrayList<Code> getListOfCodes() {
		return listOfCodes;
	}

	public void setListOfCodes(ArrayList<Code> listOfCodes) {
		this.listOfCodes = listOfCodes;
	}

}
