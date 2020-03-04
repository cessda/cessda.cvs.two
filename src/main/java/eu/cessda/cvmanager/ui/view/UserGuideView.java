package eu.cessda.cvmanager.ui.view;

import java.util.List;
import java.util.Locale;

import org.gesis.wts.security.LoginSucceedEvent;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.service.AgencyService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.domain.enumeration.ObjectType;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.MetadataFieldService;
import eu.cessda.cvmanager.service.MetadataValueService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.MetadataFieldDTO;
import eu.cessda.cvmanager.service.dto.MetadataValueDTO;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;

@UIScope
@SpringView(name = UserGuideView.VIEW_NAME)
public class UserGuideView extends CvView  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4913971320134365516L;
	public static final String VIEW_NAME = "user-guide";
	
	private static final String USER_GUIDE_FIELD = "system.menu.userguide";
	private final MetadataFieldService metadataFieldService;
	private final MetadataValueService metadataValueService;
	private Locale locale = UI.getCurrent().getLocale();
	
	private enum LayoutMode{ READ, EDIT }

    private MCssLayout userGuideLayout = new MCssLayout();
	private MCssLayout infoLayout = new MCssLayout().withFullSize();
	private MLabel notExistInfo = new MLabel("No information is available, login as admin to update content");
	private MLabel contentInfo = new MLabel().withContentMode( ContentMode.HTML);
	
	private MCssLayout editLayout = new MCssLayout().withFullSize();
	private RichTextArea infoEditor = new RichTextArea();
	
	private MButton editSwitchButton = new MButton( "Edit" );
	private MCssLayout buttonLayout = new MCssLayout().withFullWidth();
	private MButton saveButton = new MButton( "Save" );
	private MButton cancelButton = new MButton( "Cancel" );
	
	private MetadataValueDTO userGuideContent;

	public UserGuideView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			StardatDDIService stardatDDIService, SecurityService securityService, AgencyService agencyService,
			VocabularyService vocabularyService, CodeService codeService, MetadataFieldService metadataFieldService,
			MetadataValueService metadataValueService) {
		super(i18n, eventBus, configService, stardatDDIService, securityService, agencyService, vocabularyService, 
				codeService, PublicationDetailsView.VIEW_NAME);
		this.metadataValueService = metadataValueService;
		this.metadataFieldService = metadataFieldService;
		
		this.sidePanel.setVisible( false );
		init();
		eventBus.subscribe(this);
	}
	
	private void init() {
		List<MetadataValueDTO> metadataValues = metadataValueService.findByMetadataField(USER_GUIDE_FIELD, ObjectType.SYSTEM);
		if( !metadataValues.isEmpty() ) {
			userGuideContent = metadataValues.get(0);
		}
		
		switchMode( LayoutMode.READ );
		refreshInfo();
		
		editSwitchButton
			.withStyleName("pull-right")
			.withVisible( false )
			.addClickListener( e -> switchMode( LayoutMode.EDIT));
		
		if( CvManagerSecurityUtils.isAuthenticated() && CvManagerSecurityUtils.isUserAdmin()) {
			editSwitchButton.setVisible( true );
		} else {
			editSwitchButton.setVisible( false );
		}
		
		infoLayout
			.add(
				notExistInfo,
				contentInfo,
				editSwitchButton
			);
		
		infoEditor.setWidth("100%");
		infoEditor.setHeight("500px");
		if( userGuideContent != null && !userGuideContent.getValue().isEmpty())
			infoEditor.setValue( userGuideContent.getValue());
		
		saveButton
			.withStyleName("pull-right")
			.addClickListener( e -> {
				MetadataFieldDTO metadataField = null;
				if( !metadataFieldService.existsByMetadataKey(USER_GUIDE_FIELD)) {
					metadataField = new MetadataFieldDTO();
					metadataField.setMetadataKey(USER_GUIDE_FIELD);
					metadataField.setObjectType(ObjectType.SYSTEM);
					metadataField = metadataFieldService.save(metadataField);
				} else {
					metadataField = metadataFieldService.findByMetadataKey(USER_GUIDE_FIELD);
				}
				if(userGuideContent == null ) {
					userGuideContent = new MetadataValueDTO();
					userGuideContent.setMetadataFieldId(metadataField.getId());
				}
				if( infoEditor.getValue().isEmpty()) {
					userGuideContent.setValue( "" );
				}
				else {
					userGuideContent.setValue( toXHTML( infoEditor.getValue() ) );
				}
				
				userGuideContent = metadataValueService.save(userGuideContent);
				refreshInfo();
				switchMode( LayoutMode.READ);
			});
		
		cancelButton
			.withStyleName("pull-right")
			.addClickListener( e -> switchMode( LayoutMode.READ));
		
		buttonLayout
			.add( saveButton, cancelButton);
		
		editLayout
			.add(
				infoEditor,
				buttonLayout
			);
		userGuideLayout
			.withStyleName("content-mainmenu")
			.add( 
				infoLayout, 
				editLayout
			);
		mainContainer
			.withStyleName("center-mainmenu")
			.add( 
				userGuideLayout
			);
	}
	
	private String toXHTML( String html ) {
	    final Document document = Jsoup.parse(html);
	    document.select("script,.hidden,link").remove();
	    document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);    
	    return document.body().html();
	}
	
	private void switchMode( LayoutMode layoutMode) {
		if( layoutMode.equals( LayoutMode.READ)) {
			infoLayout.setVisible( true );
			editLayout.setVisible( false );
		} else {
			infoLayout.setVisible( false );
			editLayout.setVisible( true );
		}
	}

	private void refreshInfo() {
		if( userGuideContent != null &&  userGuideContent.getValue() != null && !userGuideContent.getValue().isEmpty()) {
			contentInfo.setVisible( true );
			notExistInfo.setVisible( false );
			contentInfo.setValue( userGuideContent.getValue() );
		}
		else {
			contentInfo.setVisible( false );
			notExistInfo.setVisible( true );
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// activate home button
		topMenuButtonUpdateActive(5);
	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		// TODO Auto-generated method stub
		
	}
	
	@EventBusListenerMethod(scope = EventScope.UI)
	public void onPersonModified(LoginSucceedEvent event) {
		editSwitchButton.setVisible( true );
	}

}
