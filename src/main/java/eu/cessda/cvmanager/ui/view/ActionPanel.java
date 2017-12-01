package eu.cessda.cvmanager.ui.view;

import java.util.Iterator;
import java.util.Locale;

import org.gesis.security.util.LoginSucceedEvent;
import org.gesis.stardat.ddiflatdb.client.RestClient;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
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
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.component.CvSchemeComponent;
import eu.cessda.cvmanager.ui.view.window.DialogAddLanguageWindow;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;
import eu.cessda.cvmanager.ui.view.window.DialogAddCodeWindow;

public class ActionPanel extends CustomComponent{

	private static final long serialVersionUID = -6349100242468318473L;

	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private final EventBus.UIEventBus eventBus;
	private final CvManagerService cvManagerService;
	
	private MVerticalLayout actionLayout = new MVerticalLayout();

	private MLabel panelHeader = new MLabel();
	private MButton buttonAddCv = new MButton();
	private MButton buttonChangeAgency = new MButton();
	private MButton buttonChangeLanguage = new MButton();
	
	private MButton buttonAddCode = new MButton();
	private MButton buttonAddCodeTranslation = new MButton();
	private MButton buttonDeleteCode = new MButton();
	private MButton buttonSortCode = new MButton();
	
	private MButton buttonValidateCv= new MButton();
	private MButton buttonFinaliseReview = new MButton();
	private MButton buttonPublishCv = new MButton();
	private MButton buttonUnpublishCv = new MButton();
	
	private CvManagerView cvManagerView;
	private CVScheme cvScheme;
	
	public ActionPanel( CvManagerView cvManagerView) {
		this.cvManagerView = cvManagerView;
		this.eventBus = cvManagerView.getEventBus();
		this.cvManagerService = cvManagerView.getCvManagerService();
		this.i18n = cvManagerView.i18n;
		updateMessageStrings(locale);
		
		buttonAddCv
			.withFullWidth();
		buttonChangeAgency
			.withFullWidth();
		buttonChangeLanguage
			.withFullWidth();
		buttonAddCode
			.withFullWidth();
		buttonAddCodeTranslation
			.withFullWidth();
		buttonDeleteCode
			.withFullWidth();
		buttonSortCode
			.withFullWidth();
		buttonValidateCv
			.withFullWidth();
		buttonFinaliseReview
			.withFullWidth();
		buttonPublishCv
			.withFullWidth();
		buttonUnpublishCv
			.withFullWidth();
		
		
		buttonAddCv.addClickListener( this::doAddCv );
		buttonChangeAgency.addClickListener( this::doChangeAgency );
		buttonChangeLanguage.addClickListener( this::doChangeLanguage );
		
		buttonAddCode.addClickListener( this::doAddCode );
		buttonAddCodeTranslation.addClickListener( this::doAddCodeTranslation );
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
					//buttonChangeAgency,
					buttonChangeLanguage,
					new Label("<div style=\"width:100%\">&nbsp;</div>", ContentMode.HTML),
					buttonAddCode,
					buttonAddCodeTranslation,
					buttonDeleteCode//,
//					buttonSortCode//,
					
//					buttonValidateCv,
//					buttonFinaliseReview,
//					buttonPublishCv,
//					buttonUnpublishCv
			);
		
		switch( this.cvManagerView.getActionType()) {
			case SEARCH:
			case BROWSE:
				buttonChangeAgency.setVisible( false );
				buttonChangeLanguage.setVisible( false );
				buttonAddCode.setVisible( false );
				buttonAddCodeTranslation.setVisible( false );
				buttonDeleteCode.setVisible( false );
				buttonSortCode.setVisible( false );
				buttonValidateCv.setVisible( false );
				buttonFinaliseReview.setVisible( false );
				buttonPublishCv.setVisible( false );
				buttonUnpublishCv.setVisible( false );
				break;
			case DETAIL:
				
				break;
		}
		
		setCompositionRoot(actionLayout);
	}
	
	private void doAddCv( ClickEvent event ) {
		applyButtonStyle( event.getButton());
		
		CVScheme newCvScheme = new CVScheme();
		newCvScheme.loadSkeleton(newCvScheme.getDefaultDialect());
		newCvScheme.createId();
		newCvScheme.setContainerId(newCvScheme.getId());

		Window window = new DialogCVSchemeWindow(eventBus, cvManagerService, newCvScheme, "en", "en");
		getUI().addWindow(window);
	}
	
	private void doChangeAgency(ClickEvent event ) {
		applyButtonStyle( event.getButton());

	}
	
	private void doChangeLanguage(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		
		Window window = new DialogAddLanguageWindow(eventBus, cvManagerService, cvManagerView.getCvScheme());
		getUI().addWindow(window);
	}
	
	private void doAddCode(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_ADD_DIALOG, null) );
	}
	
	private void doAddCodeTranslation(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_TRANSLATION_DIALOG, null) );
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
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_EDIT_MODE, null) );
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
	
	public void updateMessageStrings(Locale locale) {
		panelHeader.setValue(i18n.get("view.action.panel", locale));
		buttonAddCv.withCaption(i18n.get("view.action.button.cvscheme.new", locale));
		buttonChangeAgency.withCaption( i18n.get("view.action.button.cvscheme.editor", locale));
		buttonChangeLanguage.withCaption( i18n.get("view.action.button.cvscheme.translation", locale));
		buttonAddCode.withCaption( i18n.get("view.action.button.cvconcept.new", locale));
		buttonAddCodeTranslation.withCaption( i18n.get("view.action.button.cvconcept.translation", locale));
		buttonDeleteCode.withCaption( i18n.get("view.action.button.cvconcept.delete", locale));
		buttonSortCode.withCaption( i18n.get("view.action.button.cvconcept.order", locale));
		buttonValidateCv.withCaption( i18n.get("view.action.button.cvconcept.validate", locale));
		buttonFinaliseReview.withCaption( i18n.get("view.action.button.cvconcept.finalize", locale));
		buttonPublishCv.withCaption( i18n.get("view.action.button.cvconcept.publish", locale));
		buttonUnpublishCv.withCaption( i18n.get("view.action.button.cvconcept.unpublish", locale));
	}
	
}
