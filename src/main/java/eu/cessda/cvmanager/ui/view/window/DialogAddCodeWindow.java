package eu.cessda.cvmanager.ui.view.window;

import java.util.Locale;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import eu.cessda.cvmanager.Language;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.view.DetailView;

public class DialogAddCodeWindow extends MWindow implements Translatable{

	private static final long serialVersionUID = -2960064213533383226L;
	private static final Logger log = LoggerFactory.getLogger(DialogAddCodeWindow.class);

	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;

	Binder<CVConcept> binder = new Binder<CVConcept>();
	
	private MLabel lNotation = new MLabel( "Code" );
	private MLabel lTitle = new MLabel( "Descriptive term" );
	private MLabel lDescription = new MLabel( "Definition" );
	private MLabel lLanguage = new MLabel( "Language (source)" );

	private MVerticalLayout layout = new MVerticalLayout();
	private MTextField notation = new MTextField();
	private TextField preferedLabel = new TextField();
	private TextArea description = new TextArea();
	private ComboBox<String> languageCb = new ComboBox<>();
	private String orginalLanguage;

	private Button storeCode = new Button("Save");
	private CVConcept theCode;
	private CVConcept parentCode;
	private CVScheme cvScheme;

	public DialogAddCodeWindow(EventBus.UIEventBus eventBus, CvManagerService cvManagerService, CVScheme cvSch, CVConcept newCode, CVConcept parent, String orignalLanguage, I18N i18n, Locale locale) {
		super( parent == null ? i18n.get( "dialog.detail.code.add.window.title" , locale):i18n.get( "dialog.detail.code.child.window.title" , locale, ( parent.getNotation() == null? parent.getPrefLabelByLanguage("en") : parent.getNotation() )));
		
		this.eventBus = eventBus;
		this.cvScheme = cvSch;
		this.parentCode = parent;
		this.i18n = i18n;

		languageCb.setItems( Language.getAllEnumCapitalized());
		languageCb.setEmptySelectionAllowed( false );
		languageCb.setTextInputAllowed( false );
		languageCb.setValue("English");
		languageCb.setReadOnly( true );
		
		preferedLabel.setWidth("100%");
		description.setSizeFull();
		notation.withWidth("85%");

		setOrginalLanguage(orignalLanguage);
		setTheCode(newCode);

		binder.setBean(getTheCode());
		
		binder.bind(notation, concept -> concept.getNotation(),
				(concept, value) -> concept.setNotation(value));

		binder.bind(preferedLabel, concept -> getPrefLabelByLanguage(concept),
				(concept, value) -> setPrefLabelByLanguage(concept, value));

		binder.bind(description, concept -> getDescriptionByLanguage(concept),
				(concept, value) -> setDescriptionByLanguage(concept, value));

		storeCode.addClickListener(event -> {
			// CVConcept cv = binder.getBean();
			log.trace(getTheCode().getPrefLabelByLanguage(getOrginalLanguage()));
			getTheCode().save();
			DDIStore ddiStore = cvManagerService.saveElement(getTheCode().ddiStore, "User", "Add Code");
			if(parentCode == null ) // root concept
			{
				cvScheme.addOrderedMemberList(ddiStore.getElementId());
				cvScheme.save();
				DDIStore ddiStoreCv = cvManagerService.saveElement(cvScheme.ddiStore, "User", "Update Top Concept");
			}
			else // child code, add narrower data in parent
			{
				//parentCode.addOrderedNarrowerList("http://lod.gesis.org/thesoz/concept_" + ddiStore.getElementId());
				parentCode.addOrderedNarrowerList( ddiStore.getElementId());
				parentCode.save();
				cvManagerService.saveElement(parentCode.ddiStore, "User", "Add Code");
			}
			
			eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, ddiStore) );
			this.close();
		});
		
		Button cancelButton = new Button("Cancel", e -> this.close());
		MHorizontalLayout row1 = new MHorizontalLayout();
		
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
				.withHeight("400px")
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
			.withExpand(layout.getComponent(0), 0.07f)
			.withExpand(layout.getComponent(1), 0.07f)
			.withExpand(layout.getComponent(2), 0.5f)
			.withExpand(layout.getComponent(3), 0.3f)
			.withAlign(layout.getComponent(3), Alignment.BOTTOM_RIGHT);

		
		this
			.withHeight("650px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);

		updateMessageStrings(locale);
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

		return concept.getDescriptionByLanguage(getOrginalLanguage());

	}

	public String getOrginalLanguage() {

		return orginalLanguage;
	}

	public void setOrginalLanguage(String orginalLanguage) {

		this.orginalLanguage = orginalLanguage;
	}

	public CVConcept getTheCode() {
		return theCode;
	}

	public void setTheCode(CVConcept theCode) {
		this.theCode = theCode;
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		lNotation.withValue( i18n.get( "dialog.detail.code.add.form.notation" , locale)).withStyleName( "required" );
		lTitle.withValue( i18n.get( "dialog.detail.code.add.form.title" , locale)).withStyleName( "required" );
		lDescription.withValue( i18n.get( "dialog.detail.code.add.form.definition" , locale)).withStyleName( "required" );
		lLanguage.withValue( i18n.get( "dialog.detail.code.add.form.language" , locale));
	}
}
