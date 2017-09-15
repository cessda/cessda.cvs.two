package eu.cessda.cvmanager.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSCreationException;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSEntity;
import org.semanticweb.skos.SKOSLiteral;
import org.semanticweb.skos.SKOSUntypedLiteral;
import org.semanticweb.skosapibinding.SKOSManager;

public class CVService {

	private SKOSDataset dataset;

	private List<Concept> conceptList = new ArrayList<Concept>();

	private List<String> languageList = new ArrayList<String>();

	public CVService() {

		init();

	}

	private void init() {
		try {

			// First create a new SKOSManager
			SKOSManager manager = new SKOSManager();

			// use the manager to load a SKOS vocabulary from a URI (either
			// physical or on the web)

			setDataset(manager.loadDataset(URI.create("file:/daten/thesoz-komplett.xml")));
			for (SKOSConcept skosConcept : getDataset().getSKOSConcepts()) {
				Concept concept = new Concept();
				concept.setURI(skosConcept.getURI());

				for (SKOSAnnotation assertion : getDataset().getSKOSAnnotations(skosConcept)) {

					// if the annotation is a literal annotation?
					String lang = "";
					String value = "";

					if (assertion.isAnnotationByConstant()) {

						SKOSLiteral literal = assertion.getAnnotationValueAsConstant();
						value = literal.getLiteral();
						if (value != null) {
							if (!literal.isTyped()) {
								// if it has language
								SKOSUntypedLiteral untypedLiteral = literal.getAsSKOSUntypedLiteral();
								if (untypedLiteral.hasLang()) {
									lang = untypedLiteral.getLang();

									if (assertion.getURI().getFragment().equals("altLabel")) {
										concept.getAltLabel().put(lang, value);
									} else if (assertion.getURI().getFragment().equals("prefLabel")) {
										concept.getPrefLabel().put(lang, value);

									}
									if (!languageList.contains(lang)) {
										languageList.add(lang);
									}
								}
							}
						}
					} else {
						// annotation is some resource
						SKOSEntity entity = assertion.getAnnotationValue();
						value = entity.getURI().getFragment();
					}
					log.debug("\t\t" + assertion.getURI().getFragment() + " " + value + " Lang:" + lang);
				}

				conceptList.add(concept);
			}

		} catch (SKOSCreationException e) {
			e.printStackTrace();
		}

	}

	public SKOSDataset getDataset() {
		return dataset;
	}

	public void setDataset(SKOSDataset dataset) {
		this.dataset = dataset;
	}

	public List<Concept> getConceptList() {
		return conceptList;
	}

	public void setConceptList(List<Concept> conceptList) {
		this.conceptList = conceptList;
	}

	public List<String> getLanguageList() {
		return languageList;
	}

	public void setLanguageList(List<String> languageList) {
		this.languageList = languageList;
	}

}
