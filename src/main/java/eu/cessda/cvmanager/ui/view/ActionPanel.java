package eu.cessda.cvmanager.ui.view;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.Iterator;
import java.util.Locale;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.ui.view.window.DialogAddLanguageWindow;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;

public class ActionPanel extends CustomComponent{

	private static final long serialVersionUID = -6349100242468318473L;
	public static String CLASS_NAME="action";

	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private final EventBus.UIEventBus eventBus;
	private final CvManagerService cvManagerService;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	
	private MVerticalLayout actionLayout = new MVerticalLayout();

	private MLabel panelHeader = new MLabel();
	private MButton buttonAddCv = new MButton();
	private MButton buttonChangeAgency = new MButton();
	private MButton buttonChangeLanguage = new MButton();
	
	private MButton buttonCodeAdd = new MButton();
	private MButton buttonCodeAddTranslation = new MButton();
	private MButton buttonCodeAddChild = new MButton();
	private MButton buttonCodeDelete = new MButton();
	private MButton buttonCodeSort = new MButton();
	
	private MButton buttonValidateCv= new MButton();
	private MButton buttonFinaliseReview = new MButton();
	private MButton buttonPublishCv = new MButton();
	private MButton buttonUnpublishCv = new MButton();
	
	// agency button
	private MButton buttonManageMember = new MButton();
	
	private boolean enableSort=false;
	
	private CvManagerView cvManagerView;
	
	public ActionPanel( CvManagerView cvManagerView, AgencyService agencyService, VocabularyService vocabularyService) {
		this.cvManagerView = cvManagerView;
		this.eventBus = cvManagerView.getEventBus();
		this.cvManagerService = cvManagerView.getCvManagerService();
		this.i18n = cvManagerView.i18n;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		updateMessageStrings(locale);
		
		buttonAddCv
			.withFullWidth();
		buttonChangeAgency
			.withFullWidth();
		buttonChangeLanguage
			.withFullWidth();
		buttonCodeAdd
			.withFullWidth();
		buttonCodeAddChild
			.withFullWidth();
		buttonCodeAddTranslation
			.withFullWidth();
		buttonCodeDelete
			.withFullWidth();
		buttonCodeSort
			.withFullWidth();
		buttonValidateCv
			.withFullWidth();
		buttonFinaliseReview
			.withFullWidth();
		buttonPublishCv
			.withFullWidth();
		buttonUnpublishCv
			.withFullWidth();
		buttonManageMember
			.withFullWidth();
		
		buttonAddCv.addClickListener( this::doCvAdd );
		buttonChangeAgency.addClickListener( this::doCvSelectAgency );
		buttonChangeLanguage.addClickListener( this::doCvAddTranslation );
		
		buttonCodeAdd.addClickListener( this::doAddCode );
		buttonCodeAddTranslation.addClickListener( this::doCodeAddTranslation );
		buttonCodeAddChild.addClickListener( this::doCodeAddChild );
		buttonCodeDelete.addClickListener( this::doDeleteCode );
		buttonCodeSort.addClickListener( this::doSortCode );
		
		buttonValidateCv.addClickListener( this::doValidateCv );
		buttonFinaliseReview.addClickListener( this::doFinaliseReview );
		buttonPublishCv.addClickListener( this::doPublishCv );
		buttonUnpublishCv.addClickListener( this::doUnpublishCv );
		
		
		buttonManageMember.addClickListener( this::doManageMember );
		
		conceptSelectedChange(null);
		
		actionLayout
			.withFullWidth()
			.withStyleName( "action-panel" )
			.add(
					panelHeader,
					buttonAddCv,
					//buttonChangeAgency,
					buttonChangeLanguage,
					new Label("<div style=\"width:100%\">&nbsp;</div>", ContentMode.HTML),
					buttonCodeAdd,
					buttonCodeAddTranslation,
					buttonCodeAddChild,
					buttonCodeDelete,
					buttonCodeSort,
					
					buttonManageMember
					
//					buttonValidateCv,
//					buttonFinaliseReview,
//					buttonPublishCv,
//					buttonUnpublishCv
			);
		
		if( SecurityUtils.isCurrentUserSystemAdmin())
			buttonManageMember.setVisible( true );
		else {
			buttonManageMember.setVisible( false );
		}
		
		switch( this.cvManagerView.getActionType()) {
			case SEARCH:
			case BROWSE:
				buttonChangeAgency.setVisible( false );
				buttonChangeLanguage.setVisible( false );
				buttonCodeAdd.setVisible( false );
				buttonCodeAddTranslation.setVisible( false );
				buttonCodeAddChild.setVisible( false );
				buttonCodeDelete.setVisible( false );
				buttonCodeSort.setVisible( false );
				buttonValidateCv.setVisible( false );
				buttonFinaliseReview.setVisible( false );
				buttonPublishCv.setVisible( false );
				buttonUnpublishCv.setVisible( false );
				
				buttonManageMember.setVisible( false );
				break;
			case DETAIL:
				buttonCodeAddTranslation.setVisible( false );
				buttonCodeAddChild.setVisible( false );
				buttonCodeDelete.setVisible( false );
				
				buttonManageMember.setVisible( false );
				break;
			case AGENCY:
				buttonAddCv.setVisible( false);
				buttonChangeAgency.setVisible( false );
				buttonChangeLanguage.setVisible( false );
				buttonCodeAdd.setVisible( false );
				buttonCodeAddTranslation.setVisible( false );
				buttonCodeAddChild.setVisible( false );
				buttonCodeDelete.setVisible( false );
				buttonCodeSort.setVisible( false );
				buttonValidateCv.setVisible( false );
				buttonFinaliseReview.setVisible( false );
				buttonPublishCv.setVisible( false );
				buttonUnpublishCv.setVisible( false );
				break;
		}
		
		setCompositionRoot(actionLayout);
	}
	
	private void doCvAdd( ClickEvent event ) {
		applyButtonStyle( event.getButton());
		
		CVScheme newCvScheme = new CVScheme();
		newCvScheme.loadSkeleton(newCvScheme.getDefaultDialect());
		newCvScheme.createId();
		newCvScheme.setContainerId(newCvScheme.getId());

		Window window = new DialogCVSchemeWindow(eventBus, cvManagerService, agencyService, vocabularyService, newCvScheme, i18n);
		getUI().addWindow(window);
	}
	
	private void doCvSelectAgency(ClickEvent event ) {
		applyButtonStyle( event.getButton());

	}
	
	private void doCvAddTranslation(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		
		Window window = new DialogAddLanguageWindow(eventBus, cvManagerService, cvManagerView.getCvItem().getCvScheme(), cvManagerView);
		getUI().addWindow(window);
	}
	
	private void doAddCode(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_ADD_DIALOG, null) );
	}
	
	private void doCodeAddTranslation(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_TRANSLATION_DIALOG, null) );
	}
	
	private void doCodeAddChild(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_ADDCHILD_DIALOG, null) );
	}
	
	private void doManageMember(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		eventBus.publish(EventScope.UI, AgencyView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.AGENCY_MANAGE_MEMBER, null) );
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
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_DELETED, null) );
	}
	
	public void conceptSelectedChange( CVConcept cvCode) {
		cvManagerView.getCvItem().setCvConcept(cvCode);
		if( cvCode != null ) {
			buttonCodeAddTranslation.setVisible( true );
			buttonCodeAddChild.setVisible( true );
			buttonCodeDelete.setVisible( true );
		} else {
			buttonCodeAddTranslation.setVisible( false );
			buttonCodeAddChild.setVisible( false );
			buttonCodeDelete.setVisible( false );
		}
	}
	
	public void languageSelectionChange(String sourceLanguage, String currentLanguage) {
		enableSort = false;
		buttonCodeSort.withCaption( "Enable order code" );
		if( sourceLanguage.equals(currentLanguage)) {
			buttonCodeSort.setVisible( true );
		} else
			buttonCodeSort.setVisible( false );
	}
	
	private void doSortCode(ClickEvent event ) {
		applyButtonStyle( event.getButton());
		enableSort = !enableSort;
		buttonCodeSort.withCaption( enableSort ? "Disable order code" : "Enable order code" );
		eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_SORT, enableSort) );
	}
	
	private void doValidateCv() {
		
	}
	
	private void doFinaliseReview() {
		
	}
	
	private void doPublishCv() {
		
	}
	
	private void doUnpublishCv() {
		
	}
	
	public boolean isEnableSort() {
		return enableSort;
	}

	public void setEnableSort(boolean enableSort) {
		this.enableSort = enableSort;
	}

	public void updateMessageStrings(Locale locale) {
		panelHeader.setValue(i18n.get("view.action.panel", locale));
		buttonAddCv.withCaption(i18n.get("view.action.button.cvscheme.new", locale));
		buttonChangeAgency.withCaption( i18n.get("view.action.button.cvscheme.editor", locale));
		buttonChangeLanguage.withCaption( i18n.get("view.action.button.cvscheme.translation", locale));
		buttonCodeAdd.withCaption( i18n.get("view.action.button.cvconcept.new", locale));
		buttonCodeAddTranslation.withCaption( i18n.get("view.action.button.cvconcept.translation", locale));
		buttonCodeAddChild.withCaption( "Add Child" );
		buttonCodeDelete.withCaption( i18n.get("view.action.button.cvconcept.delete", locale));
		buttonCodeSort.withCaption( enableSort ? "Disable order code" : "Enable order code" );
		buttonValidateCv.withCaption( i18n.get("view.action.button.cvconcept.validate", locale));
		buttonFinaliseReview.withCaption( i18n.get("view.action.button.cvconcept.finalize", locale));
		buttonPublishCv.withCaption( i18n.get("view.action.button.cvconcept.publish", locale));
		buttonUnpublishCv.withCaption( i18n.get("view.action.button.cvconcept.unpublish", locale));
		
		buttonManageMember.withCaption( "Manage Member" );
	}

	public MButton getButtonManageMember() {
		return buttonManageMember;
	}

	public MVerticalLayout getActionLayout() {
		return actionLayout;
	}

	public MButton getButtonAddCv() {
		return buttonAddCv;
	}

	public MButton getButtonCodeAdd() {
		return buttonCodeAdd;
	}

	public MButton getButtonCodeSort() {
		return buttonCodeSort;
	}
	
	public void setSlRoleActionButtonVisible(boolean visibility) {
		buttonAddCv.setVisible(visibility);
		buttonCodeAdd.setVisible(visibility);
		buttonCodeSort.setVisible(visibility);
	}
	
	public void setTlRoleActionButtonVisible(boolean visibility) {
		buttonChangeLanguage.setVisible(visibility);
	}
}
