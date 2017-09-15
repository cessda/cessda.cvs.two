package eu.cessda.cvmanager.ui.view.window;

import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class EditCodeWindow extends Window {

	private static final Logger log = LoggerFactory.getLogger(EditCodeWindow.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8118228014482059473L;

	Binder<CVConcept> binder = new Binder<CVConcept>();

	private TextField preferedLabel = new TextField("Code");

	private TextArea description = new TextArea("Description");

	private String orginalLanguage;

	private String language;

	private Button storeCode = new Button("Save");

	public EditCodeWindow(RestClient client, CVConcept code, String orignalLanguage, String language) {
		super("Edit Code");
		setWidth("600px");
		setHeight("500px");

		setModal(true);
		setOrginalLanguage(orignalLanguage);
		setLanguage(language);

		FormLayout layout = new FormLayout();

		layout.addComponent(preferedLabel);

		layout.addComponent(description);

		binder.setBean(code);

		binder.bind(preferedLabel, concept -> getPrefLabelByLanguage(concept),
				(concept, value) -> setPrefLabelByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));

		layout.addComponent(storeCode);

		storeCode.addClickListener(event -> {
			// CVConcept cv = binder.getBean();
			log.trace(code.getPrefLabelByLanguage(getLanguage()));
			code.save();
			client.saveElement(code.ddiStore, "Peter", "minor edit");
			close();

		});

		setContent(layout);
	}

	private CVConcept setPrefLabelByLanguage(CVConcept concept, String value) {

		concept.setPrefLabelByLanguage(getOrginalLanguage(), value);
		return concept;
	}

	private String getPrefLabelByLanguage(CVConcept concept) {

		return concept.getPrefLabelByLanguage(getOrginalLanguage());

	}

	private Object setDescriptionByLanguage(CVConcept concept, String value) {

		System.out.println("FooBar");
		concept.setDescriptionByLanguage(getOrginalLanguage(), value);
		return null;
	}

	private String getDescriptionByLanguage(CVConcept concept) {

		return concept.getDescriptionByLanguage(getLanguage());

	}

	public String getOrginalLanguage() {

		return orginalLanguage;
	}

	public void setOrginalLanguage(String orginalLanguage) {

		this.orginalLanguage = orginalLanguage;
	}

	public String getLanguage() {

		return language;
	}

	public void setLanguage(String language) {

		this.language = language;
	}

}
