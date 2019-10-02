package eu.cessda.cvmanager.ui.layout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.RichTextArea;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.view.PublicationDetailsView;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;
import eu.cessda.cvmanager.utils.ParserUtils;
import eu.cessda.cvmanager.utils.VersionUtils;

public class VersionLayout extends MCssLayout implements Translatable {

	private static final Logger log = LoggerFactory.getLogger(VersionLayout.class);
	private enum LayoutMode{ READ, EDIT };
	
	private static final long serialVersionUID = -2461005203070668382L;
	private final I18N i18n;
	private final Locale locale;
	private final UIEventBus eventBus;
	private final AgencyDTO agency;
	private final VocabularyDTO vocabulary;
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
	private final VocabularyChangeService vocabularyChangeService;
	private final ConfigurationService configService;
	private final ConceptService conceptService;
	private final VersionService versionService;
	private boolean readOnly;
	private String baseUrl;
	private Map<String, List<VersionDTO>> versionMap;
	
	public VersionLayout(I18N i18n, Locale locale, UIEventBus eventBus, 
			AgencyDTO agencyDTO, VocabularyDTO vocabularyDTO,
			VocabularyChangeService vocabularyChangeService,
			ConfigurationService configService, 
			ConceptService conceptService,
			VersionService versionService,
			boolean readOnly) {
		super();
		this.i18n = i18n;
		this.locale = locale;
		this.eventBus = eventBus;
		this.agency = agencyDTO;
		this.vocabulary = vocabularyDTO;
		this.vocabularyChangeService = vocabularyChangeService;
		this.configService = configService;
		this.conceptService = conceptService;
		this.versionService = versionService;
		this.readOnly = readOnly;
		
		this
			.withFullWidth();
		
		init();
	}

	private void init() {
		baseUrl = configService.getServerContextPath() + "/#!" + PublicationDetailsView.VIEW_NAME + "/" + vocabulary.getNotation() + "?url=";
		versionMap = VersionDTO.generateVersionMap( vocabulary.getVersions());
		
	}
	
	public void refreshContent( VersionDTO version ) {
		this.removeAllComponents();
		if( version.getItemType().equals(ItemType.SL.toString())) {
			this.add( new MLabel("<h3>Source</h3>").withFullWidth().withContentMode( ContentMode.HTML));
		}
		else {
			this.add( new MLabel("<h3>Translation</h3>").withFullWidth().withContentMode( ContentMode.HTML));
		}
		
		
		for(Map.Entry<String,List<VersionDTO>> eachVersions : versionMap.entrySet()) {
			boolean expandLayout = true;
			if(eachVersions.getKey().startsWith( ItemType.SL.toString())) {
				for(VersionDTO orderedVer : eachVersions.getValue()) {
					boolean showSlVersion = false;
					// only shows version in specific language
					if( !version.getLanguage().equals( orderedVer.getLanguage()))
						continue;
					// do not show if version number not exist
					if( orderedVer.getNumber() == null )
						continue;
					//only show equal or lower version
					if( version.getNumber() == null || VersionUtils.compareVersion(orderedVer.getNumber(), version.getNumber()) <= 0) 
						showSlVersion = true;
					
					if( orderedVer.getStatus().equals( Status.PUBLISHED.toString()) && showSlVersion) {
						this.add( generateVersion(orderedVer, expandLayout));
						expandLayout = false;
					}
				}
			} else {
				for(VersionDTO orderedVer : eachVersions.getValue()) {
					boolean showTlVersion = false;
					// only shows its versions
					if( !version.getLanguage().equals( orderedVer.getLanguage()))
						continue;
					
					if( orderedVer.getNumber() == null )
						continue;
					//only show equal or lower version
					if( version.getNumber() == null || VersionUtils.compareVersion(orderedVer.getNumber(), version.getNumber()) <= 0) 
						showTlVersion = true;
					
					if( orderedVer.getStatus().equals( Status.PUBLISHED.toString()) && showTlVersion) {
						this.add( generateVersion(orderedVer, showTlVersion));
						expandLayout = false;
					}
				}
			}
		}
	}
	
	public MCssLayout generateVersion( VersionDTO versionDTO, boolean expand) {
		MCssLayout versionLayout = new MCssLayout();
		MCssLayout panelHead = new MCssLayout();
		MButton toggleButton = new MButton().withStyleName("nostyle-button","pull-left");
		MLabel infoVersion = new MLabel().withContentMode( ContentMode.HTML);
		MLabel noteVersion = new MLabel().withContentMode( ContentMode.HTML).withFullWidth().withVisible( false );
		MLabel changeVersion = new MLabel().withContentMode( ContentMode.HTML).withFullWidth().withVisible( false );
		String cvUrl = null;
		
		MCssLayout infoLayout = new MCssLayout().withFullSize();
		MCssLayout editLayout = new MCssLayout().withFullSize();
		RichTextArea infoEditor = new RichTextArea();
		MButton editSwitchButton = new MButton( "Edit" );
		MCssLayout buttonLayout = new MCssLayout().withFullWidth();
		MButton saveButton = new MButton( "Save" );
		MButton cancelButton = new MButton( "Cancel" );
		
		// set initial view to read mode
		switchMode( infoLayout, editLayout, LayoutMode.READ);
		
		MButton comparatorLayoutToggleButton = new MButton("Show changes from previous version");
		comparatorLayoutToggleButton
			.withStyleName(ValoTheme.BUTTON_LINK + " pull-left")
			.withVisible( false );
		
		CvComparatorLayout comparatorLayout = new CvComparatorLayout(conceptService);
		comparatorLayout
			.withStyleName("compare-version")
			.withVisible( false );
		
		comparatorLayoutToggleButton.addClickListener( e -> {
			if( comparatorLayout.isVisible()) {
				comparatorLayout.setVisible( false );
				e.getButton().setCaption("Show comparison with previous version");
			} else {
				if( !comparatorLayout.isVersionCompared()) {
					VersionDTO prevVersion = vocabulary.getVersionById( versionDTO.getPreviousVersion());
					comparatorLayout.compareVersion(prevVersion, versionDTO, true);
					comparatorLayout.showChangeLog( false );
					versionLayout.add( comparatorLayout );
					comparatorLayout.setVisible( true );
				}
				comparatorLayout.setVisible( true );
				e.getButton().setCaption("Hide comparison with previous version");
			}

				
		});
		
		toggleButton
			.withIcon( VaadinIcons.PLUS)
			.addClickListener( e ->{
				if( e.getButton().getIcon().equals( VaadinIcons.PLUS)) {
					e.getButton().setIcon( VaadinIcons.MINUS);
					noteVersion.setVisible( true );
//					if( versionDTO.getVersionChanges() != null )
						changeVersion.setVisible( true );
					comparatorLayoutToggleButton.setVisible( true );
					
				} else {
					e.getButton().setIcon( VaadinIcons.PLUS);
					noteVersion.setVisible( false );
					changeVersion.setVisible( false );
					comparatorLayoutToggleButton
						.withCaption( "Show comparison with previous version")
						.withVisible( false );
					comparatorLayout.setVisible( false );
				}
			});
		
		if( expand) 
			toggleButton.click();
			
		panelHead
			.withFullWidth()
			.add( toggleButton, infoVersion);

		try {
			cvUrl = baseUrl + URLEncoder.encode(versionDTO.getUri(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			cvUrl = baseUrl + versionDTO.getUri();
			log.error(e.getMessage(), e);
		}
		
		infoVersion
			.withValue("<h2>" +
					"<a href='" + cvUrl + "'>" +versionDTO.getLanguage() + ": " + versionDTO.getNumber() +"</a> " +
					" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Date of publication: " + versionDTO.getPublicationDate() + "</h2>");
		
		noteVersion
			.withValue("<h2>Version notes</h2>" +
				versionDTO.getVersionNotes());
		
//		if( versionDTO.getVersionChanges() != null && !versionDTO.getVersionChanges().isEmpty())
			changeVersion
				.withValue("<h2>Changes since previous version</h2>" +
					versionDTO.getVersionChanges().replaceAll("(\r\n|\n)", "<br />"));
		
		editSwitchButton
			.withStyleName("pull-right")
			.withVisible( false )
			.addClickListener( e -> switchMode( infoLayout, editLayout, LayoutMode.EDIT));
		
		if( CvManagerSecurityUtils.isAuthenticated() && CvManagerSecurityUtils.isCurrentUserAllowToEditMetadata(agency, versionDTO)) {
			editSwitchButton.setVisible( true );
		} else {
			editSwitchButton.setVisible( false );
		}
		
		infoEditor.setWidth("100%");
		infoEditor.setHeight("340px");
		if( versionDTO.getVersionNotes() != null )
			infoEditor.setValue( versionDTO.getVersionNotes() );
		
		saveButton
			.withStyleName("pull-right", ValoTheme.BUTTON_PRIMARY)
			.addClickListener( e -> {
				if( infoEditor.getValue().isEmpty()) {
					versionDTO.setVersionNotes( "" );
				}
				else {
					versionDTO.setVersionNotes( ParserUtils.toXHTML( infoEditor.getValue() ) );
				}
				versionService.save(versionDTO);
				noteVersion.setValue( "<h2>Version notes</h2>" + versionDTO.getVersionNotes());
				switchMode( infoLayout, editLayout, LayoutMode.READ);
			});
		
		cancelButton
			.withStyleName("pull-right")
			.addClickListener( e -> switchMode( infoLayout, editLayout, LayoutMode.READ));
		
		buttonLayout
			.add( saveButton, cancelButton);
		
		editLayout
			.add(
				infoEditor,
				buttonLayout
			);
		
		infoLayout.add( 
			noteVersion,
			editSwitchButton
		);
		
		if( versionDTO.isInitialVersion() ) {
			versionLayout
			.withStyleName( "version-item" )
			.add(
				panelHead,
				infoLayout, 
				editLayout
			);
		} else {
			versionLayout
			.withStyleName( "version-item" )
			.add(
				panelHead,
				noteVersion,
				changeVersion,
				comparatorLayoutToggleButton
			);
		
		}
		
		
		return versionLayout;
	}
	
	private void switchMode( MCssLayout infoLayout, MCssLayout editLayout, LayoutMode layoutMode) {
		if( layoutMode.equals( LayoutMode.READ)) {
			infoLayout.setVisible( true );
			editLayout.setVisible( false );
		} else {
			infoLayout.setVisible( false );
			editLayout.setVisible( true );
		}
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
	
	private CvComparatorLayout generateCompareLayout (VersionDTO currentVersion) {
		CvComparatorLayout comparatorLayout = new CvComparatorLayout(conceptService);
		comparatorLayout.withStyleName("compare-version");
		VersionDTO prevVersion = vocabulary.getVersionById( currentVersion.getPreviousVersion());
		comparatorLayout.compareVersion(prevVersion, currentVersion, true);
		return comparatorLayout;
	}
	

}
