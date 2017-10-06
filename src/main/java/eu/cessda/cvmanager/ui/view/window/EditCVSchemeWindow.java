package eu.cessda.cvmanager.ui.view.window;

import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import eu.cessda.cvmanager.ui.view.EditorView;

public class EditCVSchemeWindow extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8116725336044618619L;

	private static final Logger log = LoggerFactory.getLogger(EditCVSchemeWindow.class);

	Binder<CVScheme> binder = new Binder<CVScheme>();

	private TextField tfTitle = new TextField("Title");

	private TextArea description = new TextArea("Description");

	private String orginalLanguage;

	private String language;

	private Button storeCode = new Button("Save");

	private CVScheme cvScheme;

	private EditorView theView;

	public EditCVSchemeWindow(RestClient client, CVScheme cvScheme, String orignalLanguage, String language,
			EditorView theView) {
		super("Edit CVScheme");
		setWidth("600px");
		setHeight("500px");

		setModal(true);
		setOrginalLanguage(orignalLanguage);
		setLanguage(language);
		setCvScheme(cvScheme);
		this.setTheView(theView);

		FormLayout layout = new FormLayout();

		layout.addComponent(tfTitle);

		layout.addComponent(description);

		binder.setBean(getCvScheme());

		binder.bind(tfTitle, concept -> getTitleByLanguage(concept),
				(concept, value) -> setTitleByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));

		layout.addComponent(storeCode);

		storeCode.addClickListener(event -> {
			// CVConcept cv = binder.getBean();
			log.trace(getCvScheme().getTitleByLanguage(getLanguage()));
			getCvScheme().save();
			client.saveElement(getCvScheme().ddiStore, "Peter", "minor edit");
			getTheView().updateGrid(getLanguage());
			close();

		});

		setContent(layout);
	}

	private CVScheme setTitleByLanguage(CVScheme concept, String value) {

		concept.setTitleByLanguage(getOrginalLanguage(), value);
		return concept;
	}

	private String getTitleByLanguage(CVScheme concept) {

		return concept.getTitleByLanguage(getOrginalLanguage());

	}

	private Object setDescriptionByLanguage(CVScheme concept, String value) {

		System.out.println("FooBar");
		concept.setDescriptionByLanguage(getOrginalLanguage(), value);
		return null;
	}

	private String getDescriptionByLanguage(CVScheme concept) {

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

	public CVScheme getCvScheme() {
		return cvScheme;
	}

	public void setCvScheme(CVScheme cvScheme) {
		this.cvScheme = cvScheme;
	}

	public EditorView getTheView() {
		return theView;
	}

	public void setTheView(EditorView theView) {
		this.theView = theView;
	}

}
