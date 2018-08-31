package eu.cessda.cvmanager.ui.view.window;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.CVVersion;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MCheckBox;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.data.provider.Query;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.DetailsView;

public class DialogCreateVersionWindow extends MWindow {

	private static final long serialVersionUID = -8944364070898136792L;
	private static final Logger log = LoggerFactory.getLogger(DialogCreateVersionWindow.class);
	
	private final UIEventBus eventBus;
	private final VersionService versionService;
	private final VocabularyChangeService vocabularyChangeService;
	private final StardatDDIService stardatDDIService;
	private final CodeService codeService;
	private final ConceptService conceptService;
	private final VocabularyService vocabularyService;
	
	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
	private CVScheme cvScheme;
	private Language selectedLanguage;
	private Language sourceLanguage;
	private VersionDTO currentVersion;
	
	private MCssLayout layout = new MCssLayout();
	
	private MCssLayout versionListBlock = new MCssLayout();
	private MLabel cloneBlockTitle = new MLabel();
	private MLabel cloneBlockInfo = new MLabel().withContentMode( ContentMode.HTML );
//	private MGrid<CloneVersion> versionGrid = new MGrid<>( CloneVersion.class );
	
	private MCssLayout discussionBlock = new MCssLayout();
	private MLabel discussionTitle = new MLabel();
	private MLabel discussionInfo = new MLabel();
	private TextArea discussionArea = new TextArea();
	
	private MCssLayout versioningBlock = new MCssLayout();
	private MLabel versioningTitle = new MLabel();
	private MLabel versionInfo = new MLabel();
	private MCssLayout versionHistoryLayout = new MCssLayout();
	private MLabel versionNotesLabel = new MLabel();
	private TextArea versionNotes = new TextArea();
//	private MLabel versionNumberLabel = new MLabel();
//	private MTextField versionNumberField = new MTextField();
	private MCssLayout versionButtonLayout = new MCssLayout();
	
	private MButton buttonPublishCv = new MButton("Create New Version");
	private MButton cancelButton = new MButton("Cancel", e -> this.close());
	
	private boolean slVersioning;
	private String recommendVersionNumber = null;
//	List<CloneVersion> cloneVersions = new ArrayList<>();

	public DialogCreateVersionWindow(StardatDDIService stardatDDIService, CodeService codeService, 
			ConceptService conceptService, VocabularyService vocabularyService, VersionService versionService,
			CVScheme cvScheme, VocabularyDTO vocabularyDTO, VersionDTO versionDTO, 
			Language selectedLanguage, Language sourceLanguage, AgencyDTO agencyDTO, 
			UIEventBus eventBus, VocabularyChangeService vocabularyChangeService) {
		super("Manage Status " + ( sourceLanguage.equals(selectedLanguage) ? " SL " : " TL ") + selectedLanguage.name().toLowerCase());
		this.stardatDDIService = stardatDDIService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.cvScheme = cvScheme;
		
		this.agency = agencyDTO;
		this.vocabulary = vocabularyDTO;
		this.currentVersion = versionDTO;
		this.sourceLanguage = sourceLanguage;
		this.selectedLanguage = selectedLanguage;
		
		this.eventBus = eventBus;
		this.vocabularyChangeService = vocabularyChangeService;
		this.recommendVersionNumber = currentVersion.getNumber();
		
		init();
	}

	private void init() {
//		String buttonSuffix = ( sourceLanguage.equals(selectedLanguage) ? " SL " : " TL ") + selectedLanguage.name().toLowerCase();
		if( selectedLanguage.equals(sourceLanguage))
			slVersioning = true;
//		Map<String, List<VersionDTO>> orderedLanguageVersionMap = null;
//		if( selectedLanguage.equals(sourceLanguage)) {
//			slVersioning = true;
//			orderedLanguageVersionMap = versionService.getOrderedLanguageVersionMap(vocabulary.getId());
//		}
//		else {
//			orderedLanguageVersionMap = versionService.getOrderedLanguageSpecificVersionMap(vocabulary.getId(), selectedLanguage);
//		}
//		orderedLanguageVersionMap.forEach( (k,v) -> cloneVersions.add( new CloneVersion(k, v, currentVersion)));
		
		// increment the recommended version number
		int lastDotIndex = recommendVersionNumber.lastIndexOf(".");
		String lastNumber = recommendVersionNumber.substring( lastDotIndex + 1);
		recommendVersionNumber = recommendVersionNumber.substring(0, lastDotIndex + 1) + (Integer.parseInt(lastNumber) + 1);
//		versionNumberField.setValue( recommendVersionNumber );
		
//		versionGrid
//			.withFullWidth()
//			.withHeight("200px")
//			.setItems(cloneVersions);
//		
//		versionGrid.removeAllColumns();
//		versionGrid.addColumn( cVersion -> {
//				return cVersion.getCloneCb();
//			}, new ComponentRenderer())
//			.setCaption("Clone")
//			.setExpandRatio( 1 )
//			.setId("cloneClm");
//		versionGrid.addColumn( cVersion -> cVersion.getLanguage())
//			.setCaption("Language")
//			.setExpandRatio( 2 )
//			.setId("languageClm");
//		versionGrid.addColumn( cVersion -> cVersion.getType())
//			.setCaption("Type")
//			.setExpandRatio( 1 )
//			.setId("typeClm");
//		versionGrid.addColumn( cVersion -> {
//			return cVersion.getVersionOption();
//			}, new ComponentRenderer())
//			.setCaption("Version")
//			.setExpandRatio( 1 )
//			.setId("versionClm");
		
		cloneBlockTitle
			.withFullWidth()
			.withStyleName("section-header")
			.withValue( "CV to be cloned" );
		
		cloneBlockInfo
			.withFullWidth()
			.withValue("Select CVs to be cloned.</br>All clones CVs will be added as draft");
		
		versionListBlock
		.withStyleName("section-block")
			.withFullWidth()
			.add( 
				cloneBlockTitle,
				cloneBlockInfo
//				versionGrid
			);
		
		discussionTitle
			.withFullWidth()
			.withStyleName("section-header")
			.withValue( "Notes / Discussion" );
		discussionInfo
			.withFullWidth()
			.withValue("The discussion notes will be saved if new version is created");
		discussionArea.setWidth("100%");
		discussionArea.setValue( currentVersion.getDiscussionNotes() == null ? "":currentVersion.getDiscussionNotes());
		
		discussionBlock
			.withStyleName("section-block")
			.withFullWidth()
			.add(
				discussionTitle,
				discussionInfo,
				discussionArea
			);
		
		buttonPublishCv
			.withStyleName("action-button2")
			.addClickListener(this::createNewVersion);
		cancelButton
			.addStyleNames("action-button2");
		
		versioningTitle
			.withFullWidth()
			.withStyleName("section-header")
			.withValue("Version History");
		
		versionInfo
			.withFullWidth()
			.withContentMode( ContentMode.HTML )
			.withValue("The current version for " + currentVersion.getNotation() + "-" + currentVersion.getLanguage()
					+ "(" + currentVersion.getItemType() + ") is <strong>" + currentVersion.getNumber() + "</strong>" );
		
		versionHistoryLayout
			.withFullWidth()
			.withHeight("100px")
			.withStyleName( "yscroll","white-bg" );
		
		versionHistoryLayout
			.add(
				new MLabel( currentVersion.getSummary() == null ? "no prior version" : currentVersion.getSummary() ).withContentMode( ContentMode.HTML )
			);
		
		versionNotesLabel
			.withFullWidth()
			.withStyleName("section-header")
			.withValue("Version Notes");
		
		versionNotes.setWidth("100%");
		versionNotes.setHeight("100px");
//		
//		versionNumberLabel
//			.withStyleName("section-header","pull-left")
//			.withValue("Version Number: ");
//		
//		versionNumberField
//			.withStyleName("pull-left")
//			.setWidth("80px");
		
		versionButtonLayout
		.withStyleName("button-layout")
		.add(
//				versionNumberLabel,
//				versionNumberField,
				buttonPublishCv,
				cancelButton
		);
		
		versioningBlock
			.withStyleName("section-block")
			.withFullWidth()
			.add(
				versioningTitle,
				versionInfo,
				versionHistoryLayout,
				versionNotesLabel,
				versionNotes,
				versionButtonLayout
			);
		
		layout
			.withFullWidth()
			.withStyleName("dialog-content")
			.add(
//				versionListBlock,
				discussionBlock,
				versioningBlock
				);
		
		this
			.withHeight("800px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);
	}
	

	
	private void createNewVersion() {
			// get all checked version and perform cloning
//			for(CloneVersion cv: cloneVersions) {
				// clone the version
//				if( cv.getCloneCb().getValue() == true) {
		
		
			// get workflow codes
			List<CodeDTO> codes = codeService.findWorkflowCodesByVocabulary( vocabulary.getId() );
			
			// set version number
			String versionNumber = recommendVersionNumber;
			if( slVersioning && !currentVersion.getItemType().equals( ItemType.SL.toString())) {
				versionNumber = versionNumber + ".1";
			}
			
			VersionDTO newVersion = VersionDTO.clone(currentVersion, SecurityUtils.getLoggedUser().getId(), versionNumber, agency.getLicenseId() );
			newVersion.setDiscussionNotes( discussionArea.getValue() );
			
			
			CVScheme newCvScheme = new CVScheme();
			newCvScheme.loadSkeleton(newCvScheme.getDefaultDialect());
			newCvScheme.createId();
			newCvScheme.setContainerId(newCvScheme.getId());
			
			if( slVersioning ) {
				newVersion.setUri( newCvScheme.getContainerId() );
				newVersion.setUriSl( newVersion.getUriSl());
			}
			
			newVersion = versionService.save(newVersion);
			// save concepts
			for( CodeDTO code: codes) {
				ConceptDTO.getConceptFromCode(newVersion.getConcepts(), code.getNotation()).ifPresent( c -> c.setCodeId( code.getId()));
			}
			
			for( ConceptDTO newConcept: newVersion.getConcepts()) {
				newConcept.setVersionId( newVersion.getId());
				conceptService.save(newConcept);
			}
			
			vocabulary.addVersions(newVersion);
					
					
					
//				}
//			}
			// save whole vocabulary
			vocabulary = vocabularyService.save(vocabulary);
			
			// reindex
			vocabularyService.index(vocabulary);
			
			eventBus.publish(EventScope.UI, DetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVSCHEME_NEWVERSION, null) );
			close();
			UI.getCurrent().getNavigator().navigateTo( DetailsView.VIEW_NAME + "/" + vocabulary.getNotation());
	}
	
	public void closeDialog() {
		this.close();
	}
	
	
	class CloneVersion{
		private boolean clone;
		private String language;
		private String Type;
		private VersionDTO version;
		private List<VersionDTO> versions;
		private MCheckBox cloneCb = new MCheckBox();
		private ComboBox<VersionDTO> versionOption = new ComboBox<>();
		
		public CloneVersion(String language, List<VersionDTO> versions, VersionDTO currentVersion) {
			
			if( this.versions != null && !this.versions.isEmpty() )
				return;
			
			this.language = language;
			this.versions = versions;
			this.clone = true;
			this.version = versions.get(0);
			this.Type = version.getItemType();
			this.cloneCb.setValue( true );
			this.versionOption.setItems( versions );
			this.versionOption.setWidth("100%");
			this.versionOption.setItemCaptionGenerator( vers -> {
				return vers.getNumber() + " (" + vers.getStatus() + ")";
			});
			this.versionOption.setValue( version );
			this.versionOption.setEmptySelectionAllowed( false );
			this.versionOption.setTextInputAllowed( false );
			this.versionOption.addValueChangeListener( e -> {
				if( e.getValue() != null )
					this.version = e.getValue();
			});
			if( currentVersion.getLanguage().equals( language )) {
				this.cloneCb.setReadOnly( true );
			}
		}
		public boolean isClone() {
			return clone;
		}
		public void setClone(boolean clone) {
			this.clone = clone;
		}
		public String getLanguage() {
			return language;
		}
		public void setLanguage(String language) {
			this.language = language;
		}
		public String getType() {
			return Type;
		}
		public void setType(String type) {
			Type = type;
		}
		public VersionDTO getVersion() {
			return version;
		}
		public void setVersion(VersionDTO version) {
			this.version = version;
		}
		public List<VersionDTO> getVersions() {
			return versions;
		}
		public void setVersions(List<VersionDTO> versions) {
			this.versions = versions;
		}
		public MCheckBox getCloneCb() {
			return cloneCb;
		}
		public void setCloneCb(MCheckBox cloneCb) {
			this.cloneCb = cloneCb;
		}
		public ComboBox<VersionDTO> getVersionOption() {
			return versionOption;
		}
		public void setVersionOption(ComboBox<VersionDTO> versionOption) {
			this.versionOption = versionOption;
		}
	}

}
