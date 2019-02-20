package eu.cessda.cvmanager.ui.layout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.PublicationDetailsView;
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
	private MLabel agencyTransalteValue = new MLabel().withContentMode( ContentMode.HTML );
	
	private MTextField translatorAgency = new MTextField( "Translating agency" );
	private MTextField translatorAgencyLink = new MTextField( "Translating agency link" );
	
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
		agencyValue.setCaption("Agency");
		
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
			.withCaption( "Edit canonical URI" )
			.setWidth("100%");

		saveButton
			.addClickListener( e -> {
				String uriCv = urnEdit.getValue() + version.getNotation()  + ":" + version.getNumber() + "-" + version.getLanguage();
				version.setCanonicalUri(uriCv);
				version.setTranslateAgency( translatorAgency.getValue());
				version.setTranslateAgencyLink( translatorAgencyLink.getValue());
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
		
		if( version.getItemType().equals(ItemType.TL.toString()) && version.getTranslateAgency() != null && !version.getTranslateAgency().isEmpty()) {
			agencyTransalteValue.setVisible( true );
			agencyTransalteValue.setCaption("Translating agency");
			agencyTransalteValue.setValue("&nbsp; <a href='" + version.getTranslateAgencyLink() + "'>" + version.getTranslateAgency()  +"</a> ");
			translatorAgency.setValue( version.getTranslateAgency());
			translatorAgencyLink.setValue( version.getTranslateAgencyLink());
		} else {
			agencyTransalteValue.setVisible(false);
		}
		
		formLayout
			.addComponents(
				canonicalUri,
				canonicalUriVersion,
				urnEdit,
				agencyValue,
				agencyTransalteValue,
				translatorAgency,
				translatorAgencyLink,
				editSwitchButton,
				buttonLayout
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
			agencyTransalteValue.setVisible( true );
			translatorAgency.setVisible( false );
			translatorAgencyLink.setVisible( false );
			urnEdit.setVisible( false );
			buttonLayout.setVisible( false );
		} else {
			editSwitchButton.setVisible( false );
			canonicalUri.setVisible( false );
			canonicalUriVersion.setVisible( false );
			agencyTransalteValue.setVisible( false );
			translatorAgency.setVisible( true );
			translatorAgencyLink.setVisible( true );
			urnEdit.setVisible( true );
			buttonLayout.setVisible( true );
		}
	}

	private void refreshInfo() {
		String baseUrl = configService.getServerContextPath() + "/v1/resolver/";
		
		if( version.getCanonicalUri() == null || version.getCanonicalUri().isEmpty()) {
			canonicalUri.setVisible( false );
			canonicalUriVersion.setVisible( false );
		}
		else {
			int index = version.getCanonicalUri().lastIndexOf(":");
			String cvCanonicalUri = version.getCanonicalUri().substring(0, index);
			canonicalUri.setValue( "<a href='" + baseUrl +  cvCanonicalUri + "'>" + cvCanonicalUri + "</a>" );
			canonicalUriVersion.setValue( "<a href='" + baseUrl +  version.getCanonicalUri() + "'>" + version.getCanonicalUri() + "</a>");
			int index2 = cvCanonicalUri.lastIndexOf(":");
			if( index2 > 0)
				urnEdit.setValue( cvCanonicalUri.substring(0, index2) );
		}
		
		if( version.getItemType().equals(ItemType.TL.toString()) && version.getTranslateAgency() != null && !version.getTranslateAgency().isEmpty()) {
			agencyTransalteValue.setVisible( true );
			agencyTransalteValue.setCaption("Agency translator");
			agencyTransalteValue.setValue("&nbsp; <a href='" + version.getTranslateAgencyLink() + "'>" + version.getTranslateAgency()  +"</a> ");
		} else
			agencyTransalteValue.setVisible( false );
	}


	@Override
	public void updateMessageStrings(Locale locale) {
		
	}

}
