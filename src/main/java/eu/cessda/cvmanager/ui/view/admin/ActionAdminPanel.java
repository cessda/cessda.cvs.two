package eu.cessda.cvmanager.ui.view.admin;

import java.util.Iterator;
import java.util.Locale;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
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
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.window.DialogAddLanguageWindow;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;

public class ActionAdminPanel extends CustomComponent{

	private static final long serialVersionUID = -6349100242468318473L;
	public static String CLASS_NAME="action";

	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private final EventBus.UIEventBus eventBus;
	private final CvManagerService cvManagerService;
	
	private MVerticalLayout actionLayout = new MVerticalLayout();

	private MLabel panelHeader = new MLabel();
	private MButton buttonManageUser = new MButton();
	private MButton buttonManageAgency = new MButton();
	private MButton buttonManageUserAgency = new MButton();
	private MButton buttonManageUserAgencyLanguage = new MButton();
	private MButton buttonManageUserAgencyRole = new MButton();
	
	private boolean enableSort=false;
	
	private CvManagerAdminView cvManagerView;
	
	public ActionAdminPanel( CvManagerAdminView cvManagerAdminView) {
		this.cvManagerView = cvManagerAdminView;
		this.eventBus = cvManagerAdminView.getEventBus();
		this.cvManagerService = cvManagerAdminView.getCvManagerService();
		this.i18n = cvManagerAdminView.i18n;
		updateMessageStrings(locale);
		
		buttonManageUser.withFullWidth();
		buttonManageAgency.withFullWidth();
		buttonManageUserAgency.withFullWidth();
		buttonManageUserAgencyLanguage.withFullWidth();
		buttonManageUserAgencyRole.withFullWidth();

		buttonManageUser.addClickListener( this::doManageUser );
		buttonManageAgency.addClickListener( this::doManageAgency );
		buttonManageUserAgency.addClickListener( this::doManageUserAgency );
		buttonManageUserAgencyLanguage.addClickListener( this::doManageUserAgencyLanguage );
		buttonManageUserAgencyRole.addClickListener( this::doManageUserAgencyRole );
				
		actionLayout
			.withFullWidth()
			.withStyleName( "action-panel" )
			.add(
					panelHeader,
					buttonManageUser,
					buttonManageAgency,
					buttonManageUserAgency,
					buttonManageUserAgencyLanguage,
					buttonManageUserAgencyRole
			);
		
		switch( this.cvManagerView.getActionType()) {
//			case SEARCH:
//			case BROWSE:
//				buttonChangeAgency.setVisible( false );
//				buttonChangeLanguage.setVisible( false );
//				buttonCodeAdd.setVisible( false );
//				buttonCodeAddTranslation.setVisible( false );
//				buttonCodeAddChild.setVisible( false );
//				buttonCodeDelete.setVisible( false );
//				buttonCodeSort.setVisible( false );
//				buttonValidateCv.setVisible( false );
//				buttonFinaliseReview.setVisible( false );
//				buttonPublishCv.setVisible( false );
//				buttonUnpublishCv.setVisible( false );
//				break;
//			case DETAIL:
//				buttonCodeAddTranslation.setVisible( false );
//				buttonCodeAddChild.setVisible( false );
//				buttonCodeDelete.setVisible( false );
//				break;
		}
		
		setCompositionRoot(actionLayout);
	}
	
	private void doManageUser(ClickEvent event ) {
		getUI().getNavigator().navigateTo(ManageUserView.VIEW_NAME);
	}
	
	private void doManageAgency( ClickEvent event ) {
		getUI().getNavigator().navigateTo(ManageAgencyView.VIEW_NAME);
	}
	
	private void doManageUserAgency(ClickEvent event ) {
		getUI().getNavigator().navigateTo(ManageUserAgencyView.VIEW_NAME);
	}
	
	private void doManageUserAgencyLanguage(ClickEvent event ) {
		getUI().getNavigator().navigateTo(ManageUserAgencyLanguageView.VIEW_NAME);
	}
	
	private void doManageUserAgencyRole(ClickEvent event ) {
		getUI().getNavigator().navigateTo(ManageUserAgencyRoleView.VIEW_NAME);
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
	
	public boolean isEnableSort() {
		return enableSort;
	}

	public void setEnableSort(boolean enableSort) {
		this.enableSort = enableSort;
	}

	public void updateMessageStrings(Locale locale) {
		panelHeader.setValue(i18n.get("view.action.panel", locale));
		buttonManageUser.withCaption( "Manage User" );
		buttonManageAgency.withCaption( "Manage Agency" );
		buttonManageUserAgency.withCaption( "Manage User Agency");
		buttonManageUserAgencyLanguage.withCaption( "Manage User Agency Language");
		buttonManageUserAgencyRole.withCaption( "Manage User Agency Role");
	}
	
}
