package eu.cessda.cvmanager.ui.layout;

import java.time.LocalDate;
import java.util.Locale;

import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.TextArea;

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.dto.VersionDTO;

public class LicenseLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final Locale locale;
	private final VersionDTO version;
	private final VersionService versionService;
	
	private enum LayoutMode{ READ, EDIT };
	
	private MCssLayout infoLayout = new MCssLayout().withFullSize();
	private MLabel notExistInfo = new MLabel("No information is available");
	private MLabel contentInfo = new MLabel().withContentMode( ContentMode.HTML);
	
	private MCssLayout editLayout = new MCssLayout().withFullSize();
	private MLabel editLabelInfo = new MLabel("Edit information, you can provide HTML content here:");
	private MLabel copyrightLabel = new MLabel("<strong>Copyright:</strong>").withFullWidth().withContentMode( ContentMode.HTML);
	private TextArea copyright = new TextArea("Copyright");
	private MLabel licenseLabel = new MLabel("<strong>License:</strong>").withFullWidth().withContentMode( ContentMode.HTML);
    private TextArea license = new TextArea("License");
	
	private MButton editSwitchButton = new MButton( "Edit" );
	private MCssLayout buttonLayout = new MCssLayout().withFullWidth();
	private MButton saveButton = new MButton( "Save" );
	private MButton cancelButton = new MButton( "Cancel" );
	private boolean readOnly;
	
	public LicenseLayout(I18N i18n, Locale locale, UIEventBus eventBus, 
			AgencyDTO agencyDTO, VersionDTO versionDTO,
			VersionService versionService, boolean readOnly) {
		super();
		this.i18n = i18n;
		this.locale = locale;
		this.version = versionDTO;
		this.versionService = versionService;
		this.readOnly = readOnly;
		
		this.withFullWidth();
		init();
	}
	
	private void init() {
		switchMode( LayoutMode.READ );
		refreshInfo();
		
		editSwitchButton
			.withStyleName("pull-right")
			.withVisible( false )
			.addClickListener( e -> switchMode( LayoutMode.EDIT));
		
		if( SecurityUtils.isAuthenticated() && SecurityUtils.isUserAdmin() && !readOnly) {
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
		
		copyright.setWidth("100%");
		copyright.setHeight("80px");
		license.setWidth("100%");
		license.setHeight("80px");
		
		saveButton
			.withStyleName("pull-right")
			.addClickListener( e -> {
				version.setLicense( license.getValue());
				version.setCopyright( copyright.getValue());
				versionService.save(version);
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
				editLabelInfo,
				copyrightLabel,
				copyright,
				licenseLabel,
				license,
				buttonLayout
			);
		this
			.add( 
				infoLayout, 
				editLayout
			);
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
		if( version.getCopyright() != null && !version.getCopyright().isEmpty()) {
			contentInfo.setVisible( true );
			notExistInfo.setVisible( false );
			
			int year = LocalDate.now().getYear();
			if( version.getStatus().equals( Status.PUBLISHED.toString())) {
				year = version.getPublicationDate().getYear();
			}
			contentInfo.setValue( "<p>Copyright © " + version.getCopyright() + " " + year + ".</p>" +
						"<p>This work is licensed under a "+ version.getLicense() + ".</p>");
		}
		else {
			contentInfo.setVisible( false );
			notExistInfo.setVisible( true );
		}
	}


	@Override
	public void updateMessageStrings(Locale locale) {
		
	}

}
