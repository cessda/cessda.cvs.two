package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.TextArea;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;

public class IdentityLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final Locale locale;
	private final AgencyDTO agency;
	private final VersionDTO version;
	private final VersionService versionService;
	private final ConfigurationService configService;
	private String baseUrl;
	private enum LayoutMode{ READ, EDIT };
	
	private MFormLayout formLayout = new MFormLayout();
	
	
	private MTextField urnEdit = new MTextField();
	
	private MButton editSwitchButton = new MButton( "Edit" );
	private MCssLayout buttonLayout = new MCssLayout().withFullWidth();
	private MButton saveButton = new MButton( "Save" );
	private MButton cancelButton = new MButton( "Cancel" );
	
	private MLabel canonicalUri = new MLabel().withContentMode( ContentMode.HTML );
	private MLabel canonicalUriVersion = new MLabel().withContentMode( ContentMode.HTML );
	private MLabel agencyValue = new MLabel().withContentMode( ContentMode.HTML );
	private boolean readOnly;
	
	public IdentityLayout(I18N i18n, Locale locale, UIEventBus eventBus, 
			AgencyDTO agencyDTO, VersionDTO versionDTO,
			VersionService versionService, ConfigurationService configService,
			boolean readOnly) {
		super();
		this.i18n = i18n;
		this.locale = locale;
		this.version = versionDTO;
		this.versionService = versionService;
		this.readOnly = readOnly;
		this.configService = configService;
		this.agency = agencyDTO;
		
		this.withFullWidth();
		init();
	}
	
	private void init() {
		String baseCanonicalUri = null;
		if( version.getCanonicalUri() != null) {
			int index = version.getCanonicalUri().lastIndexOf(".");
			baseCanonicalUri = version.getCanonicalUri().substring( 0, index);
		}
			
		baseUrl = configService.getServerContextPath() + "/#!" + AgencyView.VIEW_NAME + "/" + agency.getName();
		
		formLayout.setMargin( false );
		
		canonicalUri.setCaption("Canonical URI");
		canonicalUriVersion.setCaption("Canonical URI of this version");
		agencyValue.setCaption("Agency Name");
		
		switchMode( LayoutMode.READ );
		refreshInfo();
		
		editSwitchButton
			.withStyleName("pull-right")
			.withVisible( false )
			.addClickListener( e -> switchMode( LayoutMode.EDIT));
		
		if(  CvManagerSecurityUtils.isAuthenticated() && CvManagerSecurityUtils.isCurrentUserAllowToEditMetadata(agency, version) && !readOnly) {
			editSwitchButton.setVisible( true );
		} else {
			editSwitchButton.setVisible( false );
		}
		
		urnEdit
			.withCaption( "Edit canonical URL" )
			.setWidth("100%");

		saveButton
			.addClickListener( e -> {
				String uriCv = urnEdit.getValue() + version.getNotation()  + ":" + version.getNumber() + "-" + version.getLanguage();
				version.setCanonicalUri(uriCv);
				versionService.save(version);
				refreshInfo();
				switchMode( LayoutMode.READ);
			});
		
		cancelButton
			.addClickListener( e -> switchMode( LayoutMode.READ));
		
		buttonLayout
			.add( saveButton, cancelButton);
		
		agencyValue
			.setValue("&nbsp; <a href='" + baseUrl + "'>" + agency.getName()  +"</a> ");
		
		
		formLayout
			.addComponents(
				canonicalUri,
				canonicalUriVersion,
				editSwitchButton,
				urnEdit,
				buttonLayout,
				
				agencyValue
			);
		
		this
			.add( 
				formLayout
			);
	}
	
	private void switchMode( LayoutMode layoutMode) {
		if( layoutMode.equals( LayoutMode.READ)) {
			editSwitchButton.setVisible( true );
			canonicalUri.setVisible( true );
			canonicalUriVersion.setVisible( true );
			urnEdit.setVisible( false );
			buttonLayout.setVisible( false );
		} else {
			editSwitchButton.setVisible( false );
			canonicalUri.setVisible( false );
			canonicalUriVersion.setVisible( false );
			urnEdit.setVisible( true );
			buttonLayout.setVisible( true );
		}
	}

	private void refreshInfo() {
		if( version.getCanonicalUri() == null || version.getCanonicalUri().isEmpty()) {
			canonicalUri.setVisible( false );
			canonicalUriVersion.setVisible( false );
		}
		else {
			int index = version.getCanonicalUri().lastIndexOf(":");
			String cvCanonicalUri = version.getCanonicalUri().substring(0, index);
			canonicalUri.setValue( cvCanonicalUri );
			canonicalUriVersion.setValue( version.getCanonicalUri());
			int index2 = cvCanonicalUri.lastIndexOf(":");
			if( index2 > 0)
				urnEdit.setValue( cvCanonicalUri.substring(0, index2) );
		}
	}


	@Override
	public void updateMessageStrings(Locale locale) {
		
	}

}
