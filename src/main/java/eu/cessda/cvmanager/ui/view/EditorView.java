/**
 * 
 */
package eu.cessda.cvmanager.ui.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.gesis.security.SecurityService;
import org.gesis.security.db.DBservices;
import org.gesis.security.db.Role;
import org.gesis.security.db.User;
import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.DDIElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.cessda.cvmanager.ui.view.window.EditCodeWindow;

/**
 * @author klascr
 *
 */

@UIScope
@SpringView(name = EditorView.VIEW_NAME)
public class EditorView extends VerticalLayout implements View {

	final static Logger log = LoggerFactory.getLogger(EditorView.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -4522689928221256955L;

	public static final String VIEW_NAME = "Editor";

	@Autowired
	SecurityService securityService;

	@Autowired
	DBservices dbService;

	Grid<CVConcept> conceptGrid;

	ComboBox<String> languages = new ComboBox<String>();

	private String language;

	private String originalLanguage = "en";

	private RestClient client = new RestClient("http://localhost:8080/stardat-ddiflatdb");

	List<DDIStore> ddiConcepts;

	List<CVConcept> concepts = new ArrayList<CVConcept>();

	private Button addCode = new Button("Add new Code");

	private Button editCode = new Button("Edit Code");

	private TextField prefLabelEditor = new TextField();

	private Binder<CVConcept> binder;

	private TextField prefLanguageEditor = new TextField();

	String userName = "peter";

	final GridLayout gridLayout = new GridLayout(3, 3);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.
	 * ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {

		log.info("");
		ddiConcepts = client.getElementList("thesoz", DDIElement.CVCONCEPT);
		concepts.clear();
		for (DDIStore store : ddiConcepts) {
			CVConcept con = new CVConcept(store);
			concepts.add(con);
		}

		languages.setItems(Arrays.asList("de", "en", "ru", "fr"));

		languages.addValueChangeListener(lang -> {
			updateGrid(lang.getValue());
			setLanguage(lang.getValue());
			setOriginalLanguage("en");
		});
		languages.setSelectedItem("en");

		languages.setEmptySelectionAllowed(false);

		addCode.setEnabled(isAdmin());
		editCode.setEnabled(isAdmin());

	}

	@PostConstruct
	private void init() {

		log.info("");
		this.setHeightUndefined();
		gridLayout.setSizeFull();
		gridLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		conceptGrid = new Grid<CVConcept>();
		conceptGrid.asSingleSelect();

		binder = conceptGrid.getEditor().getBinder();
		binder.addValueChangeListener(event -> Notification.show("Binder Event"));

		conceptGrid.setSizeFull();
		conceptGrid.setSelectionMode(SelectionMode.SINGLE);

		conceptGrid.setItems(concepts);
		gridLayout.addComponent(languages, 0, 0);
		gridLayout.addComponent(addCode, 1, 0);
		gridLayout.addComponent(editCode, 2, 0);
		gridLayout.addComponent(conceptGrid, 0, 1, 2, 1);

		addCode.addClickListener(event -> {

		});

		editCode.addClickListener(event -> {
			CVConcept con = conceptGrid.getSelectedItems().iterator().next();
			Window window = new EditCodeWindow(client, con, getOriginalLanguage(), getLanguage());
			getUI().addWindow(window);
		});

		addComponents(new Label("Thesoz"), gridLayout);
	}

	private void updateGrid(String language) {

		log.info("" + language);
		conceptGrid.removeAllColumns();
		conceptGrid.addColumn(CVConcept::getId).setCaption("URI").setExpandRatio(1);

		conceptGrid.addColumn(concept -> concept.getPrefLabelByLanguage("en")).setCaption("en")
				.setEditorComponent(prefLabelEditor, (concept, value) -> concept.setPrefLabelByLanguage("en", value))
				.setWidth(100);

		Binding<CVConcept, String> prefLabelBinding = binder.bind(prefLanguageEditor,
				concept -> concept.getPrefLabelByLanguage(language),
				(concept, label) -> concept.setPrefLabelByLanguage(language, label));

		conceptGrid.addColumn(concept -> concept.getPrefLabelByLanguage(language)).setCaption(language)
				.setEditorBinding(prefLabelBinding).setWidth(100);// Component(prefLanguageEditor,
		// (concept, value) ->
		// updateConcept(concept,
		// value, "en"));

		conceptGrid.addColumn(concept -> concept.getDescriptionByLanguage(language)).setCaption("Definition")
				.setExpandRatio(1);
		gridLayout.removeComponent(0, 1);
		gridLayout.addComponent(conceptGrid, 0, 1, 2, 1);

	}

	private void updateConcept(CVConcept concept, String newLabel, String language) {

		log.info("");
		concept.setPrefLabelByLanguage(language, newLabel);
		concept.save();
		client.saveElement(concept.ddiStore, userName, "minor edit");
		// return concept;
	}

	private boolean isAdmin() {

		log.debug("");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken) && auth != null) {
			userName = securityService.getLoggedInUsername();
			User user = dbService.getUserByUsername(userName);
			boolean hasRole = false;

			for (Role role : user.getRoles()) {
				if (role.getName().equals("ROLE_ADMIN")) {
					hasRole = true;
					log.info(role.getName());
				}
			}
			return auth.isAuthenticated() && hasRole;
		}
		return false;
	}

	public String getLanguage() {

		return language;
	}

	public void setLanguage(String language) {

		this.language = language;
	}

	public String getOriginalLanguage() {

		return originalLanguage;
	}

	public void setOriginalLanguage(String originalLanguage) {

		this.originalLanguage = originalLanguage;
	}

}
