package eu.cessda.cvmanager.ui.view.window;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

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
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.data.TreeData;
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
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.ConfigurationService;
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
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;
import eu.cessda.cvmanager.utils.VersionUtils;

public class DialogManageStatusWindowNew extends MWindow {

	private static final long serialVersionUID = -8944364070898136792L;
	private static final Logger log = LoggerFactory.getLogger(DialogManageStatusWindowNew.class);
	
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
	
	private MCssLayout changeListBlock = new MCssLayout();
	private MLabel changeListTitle = new MLabel();
	private MGrid<VocabularyChangeDTO> changesGrid = new MGrid<>( VocabularyChangeDTO.class );
	
	private MCssLayout discussionBlock = new MCssLayout();
	private MLabel discussionTitle = new MLabel();
	private TextArea discussionArea = new TextArea();
	private MButton buttonDiscussionSave = new MButton("Save Notes");
	
	private MCssLayout statusBlock = new MCssLayout();
	private MLabel statusTitle = new MLabel( "Status" );
	private MLabel statusInfo = new MLabel( "Change CV' status from to" );
	private MButton buttonReviewInitial = new MButton("Initial Review");
	private MButton buttonReviewFinal = new MButton("Final Review");
	private MButton buttonStatusCancel = new MButton("Cancel", e -> this.close());
	private MCssLayout statusButtonLayout = new MCssLayout();
	
	private MCssLayout versionBlock = new MCssLayout();
	private MLabel versionTitle = new MLabel();
	private MLabel versionInfo = new MLabel();
	private MCssLayout versionHistoryLayout = new MCssLayout();
	private MLabel versionNotesLabel = new MLabel();
	private TextArea versionNotes = new TextArea();
	private MLabel versionChangesLabel = new MLabel();
	private TextArea versionChanges = new TextArea();
	private MLabel versionNumberLabel = new MLabel();
	private MTextField versionNumberField = new MTextField();
	private MCssLayout tlCloneInfoLayout = new MCssLayout();
	private MCssLayout versionButtonLayout = new MCssLayout();
	
	private MButton buttonPublishCv = new MButton("Publish");
	private MButton buttonSave = new MButton("Save");
	private MButton cancelButton = new MButton("Cancel", e -> this.close());
	
	private String nextStatus = null;
	private List<VersionDTO> latestTlVersions = new ArrayList<>();
	private String versionNumberSL = "1.0";
	private String versionNumberTL = "1.0.1";

	public DialogManageStatusWindowNew(StardatDDIService stardatDDIService,  
			CodeService codeService, ConceptService conceptService, 
			VocabularyService vocabularyService, VersionService versionService,
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
		init();
	}

	private void init() {
//		String buttonSuffix = ( sourceLanguage.equals(selectedLanguage) ? " SL " : " TL ") + selectedLanguage.name().toLowerCase();
		
		changeListTitle
			.withFullWidth()
			.withStyleName("section-header")
			.withValue( "Change logs" );
		
		List<VocabularyChangeDTO> changes = vocabularyChangeService.findAllByVocabularyVersionId( vocabulary.getId(), currentVersion.getId());
		changesGrid.setItems(changes);
		changesGrid
			.withFullWidth()
			.withHeight("240px")
			.setColumns("date", "changeType", "description", "userName");
		
		changeListBlock
		.withStyleName("section-block")
			.withFullWidth()
			.add( 
				changeListTitle,
				changesGrid
			);
		
		discussionTitle
			.withFullWidth()
			.withStyleName("section-header")
			.withValue( "Notes / Discussion" );
		
		discussionArea.setWidth("100%");
		discussionArea.setValue( currentVersion.getDiscussionNotes() == null ? "":currentVersion.getDiscussionNotes());
		
		buttonDiscussionSave
			.withStyleName("action-button2")
			.addClickListener( e -> {
				currentVersion.setDiscussionNotes( discussionArea.getValue() );
				currentVersion = versionService.save(currentVersion);
				Notification.show("Notes/Discussion is saved!");
			});
		
		discussionBlock
			.withStyleName("section-block")
			.withFullWidth()
			.add(
				discussionTitle,
				discussionArea,
				buttonDiscussionSave
			);
		
		statusTitle
			.withFullWidth()
			.withStyleName("section-header");
		
		statusInfo
			.withFullWidth();
		
		buttonReviewInitial
			.withStyleName("action-button2")
			.withVisible( false )
			.addClickListener(this::forwardStatus);
		buttonReviewFinal
			.withStyleName("action-button2")
			.withVisible( false )
			.addClickListener(this::forwardStatus);
		buttonStatusCancel
			.withStyleName("action-button2");
		
		statusButtonLayout
			.withStyleName("button-layout")
			.add(
				buttonStatusCancel,
				buttonReviewInitial,
				buttonReviewFinal
			);
		
		statusBlock
			.withStyleName("section-block")
			.withFullWidth()
			.add(
				statusTitle,
				statusInfo,
				statusButtonLayout
			);
		
		if( currentVersion.getStatus().equals(Status.DRAFT.toString())) {
			statusBlock.setVisible( true );
			versionBlock.setVisible( false );
			buttonReviewInitial.setVisible( true );
			buttonReviewFinal.setVisible( false );
			discussionArea.addStyleName("height-200");
			statusInfo.setValue("Change CV' " + currentVersion.getItemType() + " " + "\"" + currentVersion.getTitle() + "\"" +
					" from DRAFT to INITIAL_REVIEW" );
		}
		else if( currentVersion.getStatus().equals(Status.INITIAL_REVIEW.toString())) {
			statusBlock.setVisible( true );
			versionBlock.setVisible( false );
			buttonReviewInitial.setVisible( false );
			buttonReviewFinal.setVisible( true );
			discussionArea.addStyleName("height-200");
			statusInfo.setValue("Change CV' " + currentVersion.getItemType() + " " + "\"" + currentVersion.getTitle() + "\"" +
					" from INITIAL_REVIEW to FINAL_REVIEW" );
		}
		else if( currentVersion.getStatus().equals(Status.FINAL_REVIEW.toString())) {
			statusBlock.setVisible( false );
			buttonDiscussionSave.setVisible( false );
			versionBlock.setVisible( true );
			discussionArea.addStyleName("height-100");
			
			// prepare the version number
			// get latest published
			vocabulary.getLatestVersionByLanguage( vocabulary.getSourceLanguage(), null, Status.PUBLISHED.toString()).ifPresent( slPublish -> {
				versionNumberSL = slPublish.getNumber();
				if( currentVersion.getItemType().equals(ItemType.SL.toString())) {
					int lastDotIndex = versionNumberSL.lastIndexOf(".");
					String lastNumber = versionNumberSL.substring( lastDotIndex + 1);
					versionNumberSL = versionNumberSL.substring(0, lastDotIndex + 1) + (Integer.parseInt(lastNumber) + 1);
				}
				else {
					versionNumberTL = versionNumberSL + ".1";
					vocabulary.getLatestVersionByLanguage( currentVersion.getLanguage(), null, Status.PUBLISHED.toString()).ifPresent( tlPublish -> {
						String latestTLPublishNumber = tlPublish.getNumber();
						if( VersionUtils.compareVersion(latestTLPublishNumber,  versionNumberSL ) > 0 ) {
							int lastDotIndex2 = latestTLPublishNumber.lastIndexOf(".");
							String lastNumber2 = latestTLPublishNumber.substring( lastDotIndex2 + 1);
							versionNumberTL = latestTLPublishNumber.substring(0, lastDotIndex2 + 1) + (Integer.parseInt(lastNumber2) + 1);
						}
						
					});
				}
			});

			// If publishing SL
			if( currentVersion.getItemType().equals(ItemType.SL.toString())){
				// get available TL, get language first and then get the latest TL
				// The latest TL will be listed as the target TL to be cloned
				for(String lang : VocabularyDTO.getLanguagesFromVersions( vocabulary.getVersions())) {
					if( lang.equals( sourceLanguage.toString()))
						continue;
					latestTlVersions.add( vocabulary.getLatestVersionByLanguage(lang).get());
				}
				StringBuilder versionChangesContent = new StringBuilder();
				for( VocabularyChangeDTO vc : changes) {
					versionChangesContent.append( vc.getChangeType() + ": " + vc.getDescription() + "\n");
				}
				versionChanges.setValue(versionChangesContent.toString());
			} else {
				StringBuilder versionChangesContent = new StringBuilder();
				for( VocabularyChangeDTO vc : changes) {
					versionChangesContent.append( vc.getChangeType() + ": " + vc.getDescription() + "\n");
				}
				versionChanges.setValue(versionChangesContent.toString());
			}
		}
		
		buttonPublishCv
			.withStyleName("action-button2")
			.addClickListener(this::forwardToPublish);
		buttonSave
			.withStyleName("action-button2")
			.addClickListener(this::saveWithoutPublish);
		cancelButton
			.addStyleNames("action-button2");
		
		versionTitle
			.withFullWidth()
			.withStyleName("section-header")
			.withValue("Publish and Version");
		
		versionInfo
			.withFullWidth()
			.withValue("Please make sure that version info and version number are correct");
		
		versionHistoryLayout
			.withFullWidth()
			.withHeight("100px")
			.withStyleName( "yscroll","white-bg" )
			.add(
				new MLabel("Version History").withStyleName( "section-header" ).withFullWidth()
			);
		
		versionHistoryLayout
			.add(
				new MLabel( currentVersion.getSummary() == null ? "no prior version" : currentVersion.getSummary() ).withContentMode( ContentMode.HTML )
			);
			
		versionNotesLabel
			.withFullWidth()
			.withStyleName("section-header")
			.withValue("Version Notes");
		
		versionChangesLabel
			.withFullWidth()
			.withStyleName("section-header")
			.withValue("Version Changes");
		
		versionNotes.setWidth("100%");
		versionNotes.setHeight("160px");
		
		versionChanges.setWidth("100%");
		versionChanges.setHeight("160px");
		
		versionNumberLabel
			.withStyleName("section-header","pull-left")
			.withValue("Version Number: ");
		
		versionNumberField
			.withStyleName("pull-left")
			.setWidth("80px");
		
		if( sourceLanguage.equals( selectedLanguage )) {
			if( currentVersion.getNumber() == null )
				versionNumberField.setValue( versionNumberSL );
			else
				versionNumberField.setValue( currentVersion.getNumber());
		}
		else
			versionNumberField.setValue( versionNumberTL);

		if( currentVersion.getVersionNotes() != null )
			versionNotes.setValue( currentVersion.getVersionNotes() );
		
		versionButtonLayout
		.withStyleName("button-layout")
		.add(
				versionNumberLabel,
				versionNumberField,
				buttonPublishCv,
				buttonSave,
				cancelButton
		);
		
		tlCloneInfoLayout
			.withFullWidth();
		if( !latestTlVersions.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for(VersionDTO ver : latestTlVersions) {
				Language verLang = Language.valueOfEnum( ver.getLanguage());
				sb.append( verLang.toStringCapitalized() + " " + (ver.getNumber() == null ? "" : ver.getNumber() ) + " (" + ver.getStatus() + ") <br/>");
			}
			
			tlCloneInfoLayout
				.add(
						new MLabel()
							.withFullWidth()
							.withContentMode( ContentMode.HTML)
							.withValue("<strong>The following TL items will be cloned as draft : </strong>"),
						new MLabel()
							.withFullWidth()
							.withContentMode( ContentMode.HTML)
							.withValue( sb.toString() )
				);
		}
		
		versionBlock
			.withStyleName("section-block")
			.withFullWidth()
			.add(
				versionTitle,
				versionInfo,
				versionHistoryLayout,
				versionNotesLabel,
				versionNotes,
				versionChangesLabel,
				versionChanges,
				tlCloneInfoLayout,
				versionButtonLayout
			);
		
		layout
			.withFullWidth()
			.withStyleName("dialog-content")
			.add(
				changeListBlock,
				discussionBlock,
				statusBlock,
				versionBlock
				);
		
		this
			.withHeight("800px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);
	}
	
	private void saveWithoutPublish() {
		currentVersion.setDiscussionNotes( discussionArea.getValue() );
		currentVersion.setVersionNotes( versionNotes.getValue() );
		currentVersion.setVersionChanges( versionChanges.getValue() );
		currentVersion.setNumber( versionNumberField.getValue() );
		currentVersion = versionService.save(currentVersion);
		Notification.show("Changes are saved!");
		close();
	}
	
	private void forwardToPublish() {
		if( versionNotes.getValue() == null || versionNotes.getValue().isEmpty() || versionNumberField.getValue() == null || versionNumberField.getValue().isEmpty()) {
			Notification.show("Version Notes and Version number can not be empty");
			return;
		}
		forwardStatus();
	}

	private void forwardStatus() {
		String currentStatus = currentVersion.getStatus();
		
		String confirmInfo = null;
		if( currentStatus.equals( Status.DRAFT.toString())) {
			nextStatus = Status.INITIAL_REVIEW.toString();
			confirmInfo = "Are you sure that you want to change the state of CV-" + ( sourceLanguage.equals(selectedLanguage) ? "SL " : "TL ") + "\"" + currentVersion.getTitle() + "\" from " + currentStatus + " to " + nextStatus + "?";
		}
		else if( currentStatus.equals( Status.INITIAL_REVIEW.toString())) {
			nextStatus = Status.FINAL_REVIEW.toString();
			confirmInfo = "Are you sure that you want to change the state of CV-" + ( sourceLanguage.equals(selectedLanguage) ? "SL " : "TL ") + "\"" + currentVersion.getTitle() + "\" from " + currentStatus + " to " + nextStatus + "?";
		}
		else if( currentStatus.equals( Status.FINAL_REVIEW.toString())) {
			nextStatus = Status.PUBLISHED.toString();
			confirmInfo = "Are you sure that you want to publish CV-" + ( sourceLanguage.equals(selectedLanguage) ? "SL " : "TL ") + "\"" + currentVersion.getTitle() + "\"?"; 
		}

		confirmForwardStatus(confirmInfo);
	}

	private void confirmForwardStatus(String confirmInfo) {
		ConfirmDialog.show( this.getUI(), "Confirm",
				confirmInfo, "yes",
				"cancel",
		
					dialog -> {
						if( dialog.isConfirmed() ) {
							String versionNumber = versionNumberField.getValue();
								
							currentVersion.setStatus( nextStatus );
							
							if( selectedLanguage.equals( sourceLanguage )) {
								vocabulary.setStatus( nextStatus);
							}
							
							if( nextStatus.equals( Status.PUBLISHED.toString())) {
								// get workflow codes
								List<CodeDTO> codes = codeService.findWorkflowCodesByVocabulary( vocabulary.getId() );
								
								vocabulary.setStatuses( vocabulary.getLatestStatuses() );
								
								// add version number to URI
								currentVersion.setUri( currentVersion.getUri() + "/" + versionNumber);
								
								currentVersion.setVersionNotes( versionNotes.getValue());
								currentVersion.setNumber( versionNumberField.getValue());
								currentVersion.setPublicationDate( LocalDate.now());
								currentVersion.setLicenseId( agency.getLicenseId());
								currentVersion.setVersionChanges( versionChanges.getValue() );
								
								String urn =  agency.getCanonicalUri();
								if(urn == null) {
									urn = "urn:" + agency.getName().replace(" ", "") + "-cv:";
								}
								currentVersion.setCanonicalUri(urn + currentVersion.getTitle() + ":" + currentVersion.getNumber() + "-" + currentVersion.getLanguage());
								
								// add summary
								currentVersion.setSummary(
									(currentVersion.getSummary() == null ? "":currentVersion.getSummary()) +
									"<strong>" + currentVersion.getNumber() + "</strong>"+
									" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Date of publication:" + currentVersion.getPublicationDate() +
									"<br/>Notes:<br/>" + currentVersion.getVersionNotes() + (currentVersion.getVersionChanges() == null ? "":"<br/>Changes:<br/>" + currentVersion.getVersionChanges()) + "<br/><br/>"
								);
								
								// update concept uri
								for(ConceptDTO concept : currentVersion.getConcepts()) {
									concept.setUri( concept.getUri() + "/" + versionNumber);
									// update concept parent and position based on workflow code
									codes.stream().filter( c -> concept.getNotation().equals( c.getNotation())).findFirst().ifPresent( c -> {
										concept.setParent( c.getParent());
										concept.setPosition( c.getPosition());
									});
									
									conceptService.save( concept );
								}
								
								
								vocabulary.setVersionByLanguage(selectedLanguage, versionNumberField.getValue());
								// if SL is published
								if( selectedLanguage.equals( sourceLanguage )) {
									vocabulary.setVersionNumber( versionNumberField.getValue() );
									// only set Uri everytime SL published
									vocabulary.setUri( currentVersion.getUri());
									vocabulary.setPublicationDate( LocalDate.now());
									vocabulary.setLanguages( VocabularyDTO.getLanguagesFromVersions( vocabulary.getVersions()) );
									vocabulary.setLanguagesPublished( null);
									vocabulary.addLanguagePublished( sourceLanguage.toString());
									
									
									String cvUriLink = agency.getUri();
									if(cvUriLink == null )
										cvUriLink = ConfigurationService.DEFAULT_CV_LINK;
									if(!cvUriLink.endsWith("/"))
										cvUriLink += "/";
									
									// clone any latest TL if exist
									for( VersionDTO targetTLversion : latestTlVersions ) {
										// create new version
										VersionDTO newVersion = VersionDTO.clone(targetTLversion, SecurityUtils.getLoggedUser().getId(), null, agency.getLicenseId(), cvUriLink);
										newVersion.setUriSl( vocabulary.getUri());
										newVersion = versionService.save(newVersion);
										
										// save concepts with workflow codes ID
										for( CodeDTO code: codes) {
											ConceptDTO.getConceptFromCode(newVersion.getConcepts(), code.getNotation()).ifPresent( c -> c.setCodeId( code.getId()));
										}
										for( ConceptDTO newConcept: newVersion.getConcepts()) {
											newConcept.setVersionId( newVersion.getId());
											conceptService.save(newConcept);
										}
										
										vocabulary.addVersions(newVersion);
									}
								} else {
									// if TL is published
									currentVersion.setUriSl( vocabulary.getUri());
									currentVersion = versionService.save(currentVersion);
									
									vocabulary.addLanguagePublished( selectedLanguage.toString());
								}
							} else {
								// other status forward but not publish
								currentVersion = versionService.save(currentVersion);
							}
							
							// save to database
							vocabulary = vocabularyService.save(vocabulary);
							
							// index for editor
							vocabularyService.index(vocabulary);
							
							// more steps for publishing SL and TL
							// store also in the flatDB
							// index elastic for the publication site
							if( nextStatus.equals( Status.PUBLISHED.toString())) {
								publishCv(vocabulary, currentVersion);
								vocabularyService.indexPublish(vocabulary, currentVersion);
								// navigate to the Publication detail
								String uri = null;
								try {
									uri = URLEncoder.encode(currentVersion.getUri(), "UTF-8");
								} catch (UnsupportedEncodingException e) {
									uri = currentVersion.getUri();
									e.printStackTrace();
								}
								UI.getCurrent().getNavigator().navigateTo( DetailView.VIEW_NAME + "/" + vocabulary.getNotation()+ "?url=" +  uri);
							}
							else	
								eventBus.publish(EventScope.UI, DetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVSCHEME_UPDATED, null) );
							closeDialog();
						}
					}
				);
	}
	

	private void publishCv( VocabularyDTO vocabulary, VersionDTO version ) {
		VersionDTO slLatestVersion = null;

		// get latest version
		vocabulary.setVers( vocabulary.getLatestVersions( Status.PUBLISHED.toString() ));
		
		// set languages from latest version
//		vocabulary.setLanguages( VocabularyDTO.getLanguagesFromVersions( vocabulary.getVers() ));
		// set published languages across versions
		// update/generate vocabulary content
//		// 1. clear vocabulary DTO first before replacing content
//		vocabulary.clearContent();
		// 1. generate vocabulary content from the latest versions
		for( VersionDTO versionDTO : vocabulary.getVers()) {
			if( versionDTO.getItemType().equals( ItemType.SL.toString()))
				slLatestVersion = versionDTO;
			Language versionLanguage = Language.valueOfEnum( versionDTO.getLanguage() );
			vocabulary.setVersionByLanguage(versionLanguage, versionDTO.getNumber());
			vocabulary.setTitleDefinition( versionDTO.getTitle(), versionDTO.getDefinition(), versionLanguage);
		}
		
		// 2. generate code content from version, create codeMap first
		// Publishing SL
		if( version.getItemType().equals( ItemType.SL.toString())) {
			// clone each code
			List<CodeDTO> codes = codeService.findWorkflowCodesByVocabulary( vocabulary.getId() );
			List<CodeDTO> newCodes = new ArrayList<>();
			for( CodeDTO eachCode : codes ) {
				//TODO:  probably only need to clone published one
				CodeDTO newCode = CodeDTO.clone(eachCode);
				newCode.setArchived( true );
				newCode.setVersionId(version.getId());
				newCode.setVersionNumber( version.getNumber() );
				newCode = codeService.save( newCode);
				
				newCodes.add(newCode);
				
				// store also the concept "code id changed"
				if( ConceptDTO.getConceptFromCode( version.getConcepts(), eachCode.getId()).isPresent()  ) {
					ConceptDTO c = ConceptDTO.getConceptFromCode( version.getConcepts(), eachCode.getId()).get();
					c.setCodeId( newCode.getId());
					conceptService.save(c);
				}
			}
			
			// save also in the cv scheme
			if (cvScheme != null && cvScheme.getContainerId().equals( currentVersion.getUri())) {
				cvScheme.setStatus( nextStatus );
				cvScheme.setOrderedMemberList( null );
				cvScheme.save();
				DDIStore ddiStore = stardatDDIService.saveElement(cvScheme.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Publish Cv");
				//refresh cvScheme
				cvScheme = new CVScheme(ddiStore);
				// save also cvConcept
				// get cv concept one by one in case it is exist
				List<DDIStore> ddiConcepts = stardatDDIService.findByIdAndElementType(cvScheme.getContainerId(), DDIElement.CVCONCEPT);
				
				// remove existing cvConcept
				for(DDIStore ddiConcept: ddiConcepts) {
					stardatDDIService.deleteById( ddiConcept.getPrimaryKey(), SecurityUtils.getCurrentUserLogin().get() , "replace concept by deleting first");
				}
				
				// store complete codeDTOs to CVConcept
				TreeData<CodeDTO> codeTree = new TreeData<>();
				CvCodeTreeUtils.buildCvConceptTree(newCodes, codeTree);
				
				// generate tree concept
				TreeData<CVConcept> cvConceptTree = new TreeData<>();
				cvConceptTree = CvCodeTreeUtils.generateCVConceptTreeFromCodeTree(codeTree, cvScheme);
				
				// save all cvConcepts and update cvScheme
				storeCvConceptTree( cvConceptTree , cvScheme);
				
				
			} 
			// cvScheme already exist "in case that CV is imported"
			else 
			{
				
				CVScheme newCvScheme = new CVScheme();
				newCvScheme.loadSkeleton(newCvScheme.getDefaultDialect());
				newCvScheme.setId( currentVersion.getUri());
				newCvScheme.setContainerId(newCvScheme.getId());
				newCvScheme.setStatus( Status.PUBLISHED.toString() );
				
//				// issue in CV version
//				CVVersion cvVersion = new CVVersion();
//				cvVersion.setContainerId( newCvScheme.getContainerId());
//				cvVersion.setType( currentVersion.getNumber() );
//				
//				newCvScheme.setVersion(cvVersion);
				
				
				// Store also Owner Agency, Vocabulary and codes
				// store vocabulary content
				newCvScheme = VocabularyDTO.setCvSchemeByVocabulary(newCvScheme, vocabulary);
				
				// store owner agency
				List<CVEditor> editorSet = new ArrayList<>();
				CVEditor cvEditor = new CVEditor();
				cvEditor.setName( agency.getName());
				cvEditor.setLogoPath( agency.getLogopath());
				
				editorSet.add( cvEditor );
				newCvScheme.setOwnerAgency((ArrayList<CVEditor>) editorSet);
				
				newCvScheme.setCode( vocabulary.getNotation());
				
				newCvScheme.save();
				DDIStore ddiStore = stardatDDIService.saveElement(newCvScheme.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Publish Cv");
				// TODO: fix unable to store nameCode
				//refresh cvScheme
				newCvScheme = new CVScheme(ddiStore);
				newCvScheme.setCode( vocabulary.getNotation());
				newCvScheme = new CVScheme( stardatDDIService.saveElement(newCvScheme.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Cv add missing nameCode"));
				
				 
				// store complete codeDTOs to CVConcept
				TreeData<CodeDTO> codeTree = new TreeData<>();
				CvCodeTreeUtils.buildCvConceptTree(newCodes, codeTree);
				
				// generate tree concept
				TreeData<CVConcept> cvConceptTree = new TreeData<>();
				cvConceptTree = CvCodeTreeUtils.generateCVConceptTreeFromCodeTree(codeTree, newCvScheme);
				
				// save all cvConcepts and update cvScheme
				storeCvConceptTree( cvConceptTree , newCvScheme);
				
			}

		}
		else 	// Publishing TL
		{
			// the codes need to be re-saved
			// get the codes from vocabulary and slLatest version
			Language versionLanguage = Language.valueOfEnum( version.getLanguage() );
			List<CodeDTO> codes = codeService.findArchivedByVocabularyAndVersion( vocabulary.getId(), slLatestVersion.getId());
			Map<String, CodeDTO> codeMap = CodeDTO.getCodeAsMap(codes);
			for( ConceptDTO concept : version.getConcepts()) {
				CodeDTO code = codeMap.get( concept.getNotation());
				if( code == null )
					continue;
				code.setTitleDefinition( concept.getTitle(), concept.getDefinition(), versionLanguage);
				codeService.save(code);
			}
			
			// save in the flatdb
			// save CvScheme
			cvScheme.setTitleByLanguage(versionLanguage.toString(), version.getTitle());
			cvScheme.setDescriptionByLanguage(versionLanguage.toString(), version.getDefinition());
			cvScheme.save();
			DDIStore ddiStore = stardatDDIService.saveElement(cvScheme.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Publish Cv " + version.getNotation() + " TL" + versionLanguage.toString());
			//refresh cvScheme
			cvScheme = new CVScheme(ddiStore);
			// save  CvConcept
			List<DDIStore> ddiConcepts = stardatDDIService.findByIdAndElementType(cvScheme.getContainerId(), DDIElement.CVCONCEPT);
			Map<String, CVConcept> cvConceptMaps = new HashMap<>();
			for(DDIStore ddiConcept: ddiConcepts) {
				CVConcept cvConcept = new CVConcept(ddiConcept);
				cvConceptMaps.put( cvConcept.getNotation(), cvConcept);
			}
			for( ConceptDTO concept : version.getConcepts()) {
				CVConcept cvConcept = cvConceptMaps.get( concept.getNotation() );
				if( cvConcept == null )
					continue;
				cvConcept.setPrefLabelByLanguage(versionLanguage.toString(),  concept.getTitle());
				cvConcept.setDescriptionByLanguage(versionLanguage.toString(), concept.getDefinition());
				cvConcept.save();
				try {
					stardatDDIService.saveElement(cvConcept.ddiStore, SecurityUtils.getCurrentUserLogin().get() , "publish code " + cvConcept.getNotation());
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
	
	private void storeCvConceptTree(TreeData<CVConcept> cvConceptTree, CVScheme newCvScheme) {
		List<CVConcept> rootItems = cvConceptTree.getRootItems();
		for(CVConcept topCvConcept : rootItems) {
			System.out.println("Store CV-concept:" + topCvConcept.getNotation());
			DDIStore ddiStoreTopCvConcept = stardatDDIService.saveElement(topCvConcept.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Add Code " + topCvConcept.getNotation());
			newCvScheme.addOrderedMemberList(ddiStoreTopCvConcept.getElementId());
			
			for( CVConcept childCvConcept : cvConceptTree.getChildren(topCvConcept)) {
				storeCvConceptTreeChild( cvConceptTree, childCvConcept, topCvConcept);
			}
		}
		// store top concept
		newCvScheme.save();
		stardatDDIService.saveElement(newCvScheme.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Update Top Concept");
	}

	private void storeCvConceptTreeChild(TreeData<CVConcept> cvConceptTree, CVConcept cCvConcept,
			CVConcept topCvConcept) {
		System.out.println("Store CV-concept c:" + cCvConcept.getNotation());
		// store cvConcept
		DDIStore ddiStoreCvConcept = stardatDDIService.saveElement(cCvConcept.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Add Code " + cCvConcept.getNotation());
		// store narrower
		topCvConcept.addOrderedNarrowerList( ddiStoreCvConcept.getElementId());
		topCvConcept.save();
		stardatDDIService.saveElement(topCvConcept.ddiStore, "User", "Add Code narrower");
		for( CVConcept cvConceptChild : cvConceptTree.getChildren(cCvConcept)) {
			storeCvConceptTreeChild( cvConceptTree, cvConceptChild, cCvConcept);
		}
	}

	public void closeDialog() {
		this.close();
	}

}
