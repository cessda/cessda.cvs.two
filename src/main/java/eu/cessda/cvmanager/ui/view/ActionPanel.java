package eu.cessda.cvmanager.ui.view;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.Iterator;

import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.ui.component.CvSchemeComponent;
import eu.cessda.cvmanager.ui.view.window.AddLanguageCVSchemeWindow;
import eu.cessda.cvmanager.ui.view.window.EditCVSchemeWindow;

public class ActionPanel extends CustomComponent{

	private static final long serialVersionUID = -6349100242468318473L;

	private final EventBus.UIEventBus eventBus;
	private final RestClient client;
	private final Grid<CVConcept> detailGrid;
	
	private MVerticalLayout actionLayout = new MVerticalLayout();

	private MLabel panelHeader = new MLabel( "ACTIONS" );
	private MButton buttonAddCv = new MButton( "Add new CV" );
	private MButton buttonChangeAgency = new MButton( "Change agency" );
	private MButton buttonChangeLanguage = new MButton( "Add Language" );
	
	private MButton buttonAddCode = new MButton( "Add Code" );
	private MButton buttonDeleteCode = new MButton( "Delete Code" );
	private MButton buttonSortCode = new MButton( "Sort Code" );
	
	private MButton buttonValidateCv= new MButton( "Validate CV" );
	private MButton buttonFinaliseReview = new MButton( "Finalise review" );
	private MButton buttonPublishCv = new MButton( "Publish CV" );
	private MButton buttonUnpublishCv = new MButton( "Unpublish CV" );
	
	private DetailView detailView;
	private CVScheme cvScheme;
	
	public ActionPanel(DetailView detailView) {
		this.detailView = detailView;
		this.eventBus = detailView.getEventBus();
		this.client = detailView.getClient();
		this.detailGrid = detailView.getDetailGrid();
		this.cvScheme = detailView.getCvScheme();
		
		buttonAddCv
//			.withStyleName( ValoTheme.BUTTON_LINK )
			.withFullWidth();
		buttonChangeAgency
//			.withStyleName( ValoTheme.BUTTON_LINK )
			.withFullWidth();
		buttonChangeLanguage
//			.withStyleName( ValoTheme.BUTTON_LINK )
			.withFullWidth();
		
		buttonAddCode
//			.withStyleName( ValoTheme.BUTTON_LINK )
			.withFullWidth();
		buttonDeleteCode
//			.withStyleName( ValoTheme.BUTTON_LINK )
			.withFullWidth();
		buttonSortCode
//			.withStyleName( ValoTheme.BUTTON_LINK )
			.withFullWidth();
		
		buttonValidateCv
//			.withStyleName( ValoTheme.BUTTON_LINK )
			.withFullWidth();
		buttonFinaliseReview
//			.withStyleName( ValoTheme.BUTTON_LINK )
			.withFullWidth();
		buttonPublishCv
//			.withStyleName( ValoTheme.BUTTON_LINK )
			.withFullWidth();
		buttonUnpublishCv
//			.withStyleName( ValoTheme.BUTTON_LINK )
			.withFullWidth();
		
		
		buttonAddCv.addClickListener( this::doAddCv );
		buttonChangeAgency.addClickListener( this::doChangeAgency );
		buttonChangeLanguage.addClickListener( this::doChangeLanguage );
		
		buttonAddCode.addClickListener( this::doAddCode );
		buttonDeleteCode.addClickListener( this::doDeleteCode );
		buttonSortCode.addClickListener( this::doSortCode );
		
		buttonValidateCv.addClickListener( this::doValidateCv );
		buttonFinaliseReview.addClickListener( this::doFinaliseReview );
		buttonPublishCv.addClickListener( this::doPublishCv );
		buttonUnpublishCv.addClickListener( this::doUnpublishCv );
		
		actionLayout
			.withFullWidth()
			.withStyleName( "action-panel" )
			.add(
					panelHeader,
					buttonAddCv,
					buttonChangeAgency,
					buttonChangeLanguage,
					new Label("<div style=\"width:100%\">&nbsp;</div>", ContentMode.HTML),
					buttonAddCode,
					buttonDeleteCode,
					buttonSortCode//,
					
//					buttonValidateCv,
//					buttonFinaliseReview,
//					buttonPublishCv,
//					buttonUnpublishCv
			);
		
		setCompositionRoot(actionLayout);
	}
	
	private void doAddCv( ClickEvent event ) {
		applyButtonStyle( event.getButton());
		
		CVScheme cvScheme = new CVScheme();
		cvScheme.loadSkeleton(cvScheme.getDefaultDialect());
		cvScheme.createId();
		cvScheme.setContainerId(cvScheme.getId());

		Window window = new EditCVSchemeWindow(eventBus, client, cvScheme, "en", "en");
		getUI().addWindow(window);
	}
	
	private void doChangeAgency(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		
		
		
	}
	
	private void doChangeLanguage(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		
		Window window = new AddLanguageCVSchemeWindow(eventBus, client, cvScheme, detailView);
		getUI().addWindow(window);
	}
	
	private void doAddCode(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		
	}
	
	private void applyButtonStyle(Button pressedButton) {

		Iterator<Component> iterate = actionLayout.iterator();
		while (iterate.hasNext()) {
			Component c = (Component) iterate.next();
			if( c instanceof  Button) {
				((Button) c).removeStyleName( "button-pressed" );
			}
		}
		pressedButton.addStyleName( "button-pressed" );
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doDeleteCode(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		if( detailGrid.getColumn("cvConceptRemove") == null ) {		
			detailView
				.getDetailGrid()
					.addColumn( cvconcept -> "x",
						new ButtonRenderer(clickEvent -> {
							detailView.getConcepts().remove(clickEvent.getItem());
							detailGrid.setItems( detailView.getConcepts() );
				    })).setId("cvConceptRemove");
		} else {
			if( !detailGrid.getColumn("cvConceptRemove").isHidden()) {
				detailGrid.getColumn("cvConceptRemove").setHidden( true );
			} else {
				detailGrid.getColumn("cvConceptRemove").setHidden( false );
			}
		}
	}
	
	private void doSortCode(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		
	}
	
	private void doValidateCv() {
		
	}
	
	private void doFinaliseReview() {
		
	}
	
	private void doPublishCv() {
		
	}
	
	private void doUnpublishCv() {
		
	}
	
	
}
