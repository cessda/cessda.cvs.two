package eu.cessda.cvmanager.ui.view;

import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.ui.component.CvSchemeComponent;
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
	private MButton buttonChangeLanguage = new MButton( "Change Language" );
	
	private MButton buttonAddCode = new MButton( "Add Code" );
	private MButton buttonDeleteCode = new MButton( "Delete Code" );
	private MButton buttonSortCode = new MButton( "Sort Code" );
	
	private MButton buttonValidateCv= new MButton( "Validate CV" );
	private MButton buttonFinaliseReview = new MButton( "Finalise review" );
	private MButton buttonPublishCv = new MButton( "Publish CV" );
	private MButton buttonUnpublishCv = new MButton( "Unpublish CV" );
	
	private DetailView detailView;
	
	public ActionPanel(DetailView detailView) {
		this.detailView = detailView;
		this.eventBus = detailView.getEventBus();
		this.client = detailView.getClient();
		this.detailGrid = detailView.getDetailGrid();
		
		
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
			.add(
					panelHeader,
					
					buttonAddCv,
					buttonChangeAgency,
					buttonChangeLanguage,
					
					buttonAddCode,
					buttonDeleteCode,
					buttonSortCode,
					
					buttonValidateCv,
					buttonFinaliseReview,
					buttonPublishCv,
					buttonUnpublishCv
			);
		
		setCompositionRoot(actionLayout);
	}
	
	private void doAddCv() {
		CVScheme cvScheme = new CVScheme();
		cvScheme.loadSkeleton(cvScheme.getDefaultDialect());
		cvScheme.createId();
		cvScheme.setContainerId(cvScheme.getId());

		Window window = new EditCVSchemeWindow(client, cvScheme, "en", detailView.getSelectedLang(),
				detailView);
		getUI().addWindow(window);
	}
	
	private void doChangeAgency() {
		
	}
	
	private void doChangeLanguage() {
		
	}
	
	private void doAddCode() {
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doDeleteCode() {
		if( detailGrid.getColumn("cvConceptRemove") == null ) {		
			detailView
				.getDetailGrid()
					.addColumn( cvconcept -> "x",
						new ButtonRenderer(clickEvent -> {
							detailView.getConcepts().remove(clickEvent.getItem());
							detailGrid.setItems( detailView.getConcepts() );
				    })).setId("cvConceptRemove");
			
			if( !detailGrid.getColumn("cvConceptRemove").isHidden()) {
				detailGrid.getColumn("cvConceptRemove").setHidden( true );
			} else {
				detailGrid.getColumn("cvConceptRemove").setHidden( false );
			}
			
		}
	}
	
	private void doSortCode() {
		
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
