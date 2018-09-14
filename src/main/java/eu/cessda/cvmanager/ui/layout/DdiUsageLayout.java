package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.dto.AgencyDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.RichTextArea;

import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;

public class DdiUsageLayout extends MCssLayout implements Translatable {
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final Locale locale;
	private final VersionDTO version;
	private final AgencyDTO agency;
	private final VersionService versionService;
	
	private enum LayoutMode{ READ, EDIT };
	
	private MCssLayout infoLayout = new MCssLayout().withFullSize();
	private MLabel notExistInfo = new MLabel("No information is available");
	private MLabel contentInfo = new MLabel().withContentMode( ContentMode.HTML);
	
	private MCssLayout editLayout = new MCssLayout().withFullSize();
	private RichTextArea infoEditor = new RichTextArea();
	
	private MButton editSwitchButton = new MButton( "Edit" );
	private MCssLayout buttonLayout = new MCssLayout().withFullWidth();
	private MButton saveButton = new MButton( "Save" );
	private MButton cancelButton = new MButton( "Cancel" );
	private boolean readOnly;
	
	public DdiUsageLayout(I18N i18n, Locale locale, UIEventBus eventBus, 
			AgencyDTO agencyDTO, VersionDTO versionDTO,
			VersionService versionService, boolean readOnly) {
		super();
		this.i18n = i18n;
		this.locale = locale;
		this.version = versionDTO;
		this.agency = agencyDTO;
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
		
		if( CvManagerSecurityUtils.isAuthenticated() && CvManagerSecurityUtils.isCurrentUserAllowToEditMetadata(agency, version) && !readOnly) {
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
		infoEditor.setHeight("340px");
		if( version.getDdiUsage() != null )
			infoEditor.setValue( version.getDdiUsage() );
		
		saveButton
			.withStyleName("pull-right")
			.addClickListener( e -> {
				if( infoEditor.getValue().isEmpty()) {
					version.setDdiUsage( "" );
				}
				else {
					version.setDdiUsage( toXHTML( infoEditor.getValue() ) );
				}
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
				infoEditor,
				buttonLayout
			);
		this
			.add( 
				infoLayout, 
				editLayout
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
		if( version.getDdiUsage() != null && !version.getDdiUsage().isEmpty()) {
			contentInfo.setVisible( true );
			notExistInfo.setVisible( false );
			contentInfo.setValue( version.getDdiUsage() );
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
