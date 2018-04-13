package eu.cessda.cvmanager.ui.view.window;

import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.EditorView;

public class DialogEditCodeWindow extends MWindow {

	private static final Logger log = LoggerFactory.getLogger(DialogEditCodeWindow.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8118228014482059473L;
	private final EventBus.UIEventBus eventBus;

	Binder<CVConcept> binder = new Binder<CVConcept>();
	private MVerticalLayout layout = new MVerticalLayout();
	
	private MLabel lSourceNotation = new MLabel( "Code" );
	private MLabel lTitle = new MLabel( "Descriptive term" );
	private MLabel lDescription = new MLabel( "Definition" );
	private MLabel lLanguage = new MLabel( "Language" );
	private MLabel lNotation = new MLabel( "Code" );
	private MLabel lSourceTitle = new MLabel( "Descriptive term (source)" );
	private MLabel lSourceDescription = new MLabel( "Definition (source)" );
	private MLabel lSourceLanguage = new MLabel( "Language (source)" );
	
	private MTextField sourceNotation = new MTextField("Code");
	private MTextField sourceTitle = new MTextField("Descriptive term (source)");
	private MTextField sourceLanguage = new MTextField("Language (source)");
	private TextArea sourceDescription = new TextArea("Definition en (source)");

	private MTextField notation = new MTextField("Code");
	private TextField preferedLabel = new TextField("Descriptive term*");
	private TextArea description = new TextArea("Definition*");
	private ComboBox<String> languageCb = new ComboBox<>("Language*");

	private String selectedLanguage;

	private Button storeCode = new Button("Save");

	private CVScheme cvScheme;
	private CVConcept code;
	
	MHorizontalLayout sourceRow = new MHorizontalLayout();
	MHorizontalLayout sourceRowA = new MHorizontalLayout();
	MHorizontalLayout sourceRowB = new MHorizontalLayout();

	public DialogEditCodeWindow(EventBus.UIEventBus eventBus, StardatDDIService stardatDDIService, CVScheme cvScheme, CVConcept code, String sLanguage) {
		super( "Edit Code");
		this.cvScheme = cvScheme;
		this.code = code;
		this.selectedLanguage = sLanguage;
		
		this.eventBus = eventBus;
		
		sourceNotation.withWidth("85%");
		sourceNotation.setValue( code.getNotation() == null ? "" : code.getNotation() );
		sourceNotation.setReadOnly( true );
		
		notation.withWidth("85%");
		
		sourceTitle.withFullWidth();
		sourceTitle.setValue( code.getPrefLabelByLanguage( "en" ) );
		sourceDescription.setSizeFull();
		
		sourceDescription.setValue( code.getDescriptionByLanguage( "en" ) );
		sourceDescription.setReadOnly( true );
		sourceTitle.setReadOnly( true );
		
		preferedLabel.setWidth("100%");
		description.setSizeFull();
		
		sourceLanguage
			.withReadOnly( true)
			.setValue( "English" );
		
		lTitle.withStyleName( "required" );
		lLanguage.withStyleName( "required" );
		lDescription.withStyleName( "required" );
		
		preferedLabel.setCaption( "Descriptive term (" + selectedLanguage + ")*");
		description.setCaption( "Definition ("+ selectedLanguage +")*");
		
		Set<String> currentCvLanguage = cvScheme.getLanguagesByTitle();
		
		languageCb.setItems( Language.getFilteredEnumCapitalized( currentCvLanguage ));
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setValue( Language.getEnumCapitalized( selectedLanguage ));
		languageCb.setReadOnly( true );
		languageCb.addValueChangeListener( e -> {
			setSelectedLanguage( Language.valueOf( e.getValue().toString().toUpperCase()).getLanguage());
			
			preferedLabel.setCaption( "Descriptive term (" + selectedLanguage + ")*");
			description.setCaption( "Definition ("+ selectedLanguage +")*");
			
			preferedLabel.setValue( code.getPrefLabelByLanguage(selectedLanguage));
			description.setValue( code.getDescriptionByLanguage(selectedLanguage) );
			
			if( e.getValue().equals( "en" )) {
				sourceRow.setVisible( false );
				sourceRowA.setVisible( false );
				sourceRowB.setVisible( false );
			} else {
				sourceRow.setVisible( false );
				sourceRowA.setVisible( true );
				sourceRowB.setVisible( true );
			}
		});
		
//		if( selectedLanguage.equals( "en" )) {
//			sourceRow.setVisible( false );
//			sourceRowA.setVisible( false );
//			sourceRowB.setVisible( false );
//		} else {
//			sourceRow.setVisible( true );
//			sourceRowA.setVisible( true );
//			sourceRowB.setVisible( true );
//		}

		binder.setBean(code);
		
		binder.bind(notation, concept -> concept.getNotation(),
				(concept, value) ->  concept.setNotation(value));

		binder.bind(preferedLabel, concept -> getPrefLabelByLanguage(concept),
				(concept, value) -> setPrefLabelByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));

		storeCode.addClickListener(event -> {
			// CVConcept cv = binder.getBean();
			log.trace(code.getPrefLabelByLanguage(selectedLanguage));
			code.save();
			DDIStore ddiStore = stardatDDIService.saveElement(code.ddiStore, "Peter", "minor edit");
			
			eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, ddiStore) );
			
			this.close();

		});

		Button cancelButton = new Button("Cancel", e -> this.close());
		
		MHorizontalLayout row1 = new MHorizontalLayout();
		
		if( !selectedLanguage.equals( "en" )) {
			layout
				.withHeight("98%")
				.withStyleName("dialog-content")
				.add( 
					sourceRowA
						.withFullWidth()
						.add(
							new MHorizontalLayout()
							.withFullWidth()
							.add(
									lSourceNotation, sourceNotation
							).withExpand(lSourceNotation, 0.215f).withExpand(sourceNotation, 0.785f),
							new MHorizontalLayout().add(
									lSourceLanguage, sourceLanguage
							)
					).withExpand( sourceRowA.getComponent(0), 0.7f)
					 .withExpand( sourceRowA.getComponent(1), 0.3f),
					sourceRow
						.withFullWidth()
						.add(
								lSourceTitle, sourceTitle
							).withExpand(lSourceTitle, 0.15f).withExpand( sourceTitle, 0.85f),
					sourceRowB
						.withFullWidth()
						.withHeight("270px")
						.add(
							lSourceDescription, sourceDescription
						).withExpand( lSourceDescription, 0.15f).withExpand( sourceDescription, 0.85f),
					new MHorizontalLayout()
						.withFullWidth()
						.add(
							new MHorizontalLayout()
							.withFullWidth()
							.add(
									lTitle, preferedLabel
							).withExpand(lTitle, 0.31f).withExpand(preferedLabel, 0.69f),
							new MHorizontalLayout().add(
									lLanguage, languageCb
							)
					),
					new MHorizontalLayout()
						.withFullWidth()
						.withHeight("290px")
						.add(
							lDescription, description
						).withExpand( lDescription, 0.15f).withExpand( description, 0.85f),
					new MHorizontalLayout()
						.withFullWidth()
						.add( storeCode,
							cancelButton
						)
						.withExpand(storeCode, 0.8f)
						.withAlign(storeCode, Alignment.BOTTOM_RIGHT)
						.withExpand(cancelButton, 0.1f)
						.withAlign(cancelButton, Alignment.BOTTOM_RIGHT)
				)
				.withExpand(layout.getComponent(0), 0.03f)
				.withExpand(layout.getComponent(1), 0.03f)
				.withExpand(layout.getComponent(2), 0.25f)
				.withExpand(layout.getComponent(3), 0.03f)
				.withExpand(layout.getComponent(4), 0.25f)
				.withAlign(layout.getComponent(5), Alignment.BOTTOM_RIGHT);
		} else {
			layout
			.withHeight("98%")
			.withStyleName("dialog-content")
			.add( 
				row1
					.withFullWidth()
					.add(
						new MHorizontalLayout()
						.withFullWidth()
						.add(
								lNotation, notation
						).withExpand(lNotation, 0.215f).withExpand(notation, 0.785f),
						new MHorizontalLayout().add(
								lLanguage, languageCb
						)
				).withExpand( row1.getComponent(0), 0.7f)
				 .withExpand( row1.getComponent(1), 0.3f),
				new MHorizontalLayout()
					.withFullWidth()
					.add(
						lTitle, preferedLabel
					).withExpand(lTitle, 0.15f).withExpand( preferedLabel, 0.85f),
				new MHorizontalLayout()
				.withFullWidth()
				.withHeight("300px")
				.add(
					lDescription, description
				).withExpand( lDescription, 0.15f).withExpand( description, 0.85f),
				new MHorizontalLayout()
				.withFullWidth()
				.add( storeCode,
					cancelButton
				)
				.withExpand(storeCode, 0.8f)
				.withAlign(storeCode, Alignment.BOTTOM_RIGHT)
				.withExpand(cancelButton, 0.1f)
				.withAlign(cancelButton, Alignment.BOTTOM_RIGHT)
			)
			.withExpand(layout.getComponent(0), 0.06f)
			.withExpand(layout.getComponent(1), 0.06f)
			.withExpand(layout.getComponent(2), 0.4f)
			.withExpand(layout.getComponent(3), 0.4f)
			.withAlign(layout.getComponent(3), Alignment.BOTTOM_RIGHT);
		}
		
		this
			.withHeight("800px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);
	}

	private CVConcept setPrefLabelByLanguage(CVConcept concept, String value) {

		concept.setPrefLabelByLanguage(selectedLanguage, value);
		return concept;
	}

	private String getPrefLabelByLanguage(CVConcept concept) {

		return concept.getPrefLabelByLanguage(selectedLanguage);

	}

	private Object setDescriptionByLanguage(CVConcept concept, String value) {

		System.out.println("FooBar");
		concept.setDescriptionByLanguage(selectedLanguage, value);
		return null;
	}

	private String getDescriptionByLanguage(CVConcept concept) {

		return concept.getDescriptionByLanguage(selectedLanguage);

	}

	public void setSelectedLanguage(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}
	
	
}
