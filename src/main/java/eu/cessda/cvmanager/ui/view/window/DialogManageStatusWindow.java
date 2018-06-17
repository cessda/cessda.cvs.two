package eu.cessda.cvmanager.ui.view.window;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.CVVersion;
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
import com.vaadin.data.provider.Query;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.model.CvItem;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.view.DetailView;

public class DialogManageStatusWindow extends MWindow {

	private static final long serialVersionUID = -8944364070898136792L;
	private static final Logger log = LoggerFactory.getLogger(DialogManageStatusWindow.class);
	
	private final UIEventBus eventBus;
	private final VersionService versionService;
	private final VocabularyChangeService vocabularyChangeService;
	private final StardatDDIService stardatDDIService;
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
	private MLabel versionNumberLabel = new MLabel();
	private MTextField versionNumberField = new MTextField();
	private MCssLayout versionButtonLayout = new MCssLayout();
	
	private MButton buttonPublishCv = new MButton("Publish");
	private MButton cancelButton = new MButton("Cancel", e -> this.close());
	
	private String nextStatus = null;

	public DialogManageStatusWindow(StardatDDIService stardatDDIService,  
			VocabularyService vocabularyService, VersionService versionService,
			CVScheme cvScheme, VocabularyDTO vocabularyDTO, VersionDTO versionDTO, 
			Language selectedLanguage, Language sourceLanguage, AgencyDTO agencyDTO, 
			UIEventBus eventBus, VocabularyChangeService vocabularyChangeService) {
		super("Manage Status " + ( sourceLanguage.equals(selectedLanguage) ? " SL " : " TL ") + selectedLanguage.name().toLowerCase());
		this.stardatDDIService = stardatDDIService;
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
				Notification.show("Notes/DIscussion is saved!");
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
			versionBlock.setVisible( true );
			discussionArea.addStyleName("height-100");
		}
		
		buttonPublishCv
			.withStyleName("action-button2")
			.addClickListener(this::forwardToPublish);
		cancelButton
			.addStyleNames("action-button2");
		
		
//		private MCssLayout versionBlock = new MCssLayout();
//		private MLabel versionTitle = new MLabel();
//		private MLabel versionInfo = new MLabel();
//		private MCssLayout versionHistoryLayout = new MCssLayout();
//		private TextArea versionNotes = new TextArea();
//		private MLabel versionNumberLabel = new MLabel();
//		private MTextField versionNumberField = new MTextField();
//		private MCssLayout versionButtonLayout = new MCssLayout();
		
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
			.withStyleName( "white-bg" )
			.add(
				new MLabel("Version History").withStyleName( "section-header" ).withFullWidth()
			);
		
		// TODO: Fix after versioning activated
		versionHistoryLayout
			.add(
				new MLabel("No prior version").withContentMode( ContentMode.HTML )
			);
		
		versionNotesLabel
			.withFullWidth()
			.withStyleName("section-header")
			.withValue("Version Notes");
		
		versionNotes.setWidth("100%");
		versionNotes.setHeight("200px");
		
		versionNumberLabel
			.withStyleName("section-header","pull-left")
			.withValue("Version Number: ");
		
		versionNumberField
			.withStyleName("pull-left")
			.setWidth("80px");
		
		if( sourceLanguage.equals( selectedLanguage ))
			versionNumberField.setValue("1.0.0");
		else
			versionNumberField.setValue("1.0.1");
		
		versionButtonLayout
		.withStyleName("button-layout")
		.add(
				versionNumberLabel,
				versionNumberField,
				buttonPublishCv,
				cancelButton
		);
		
		versionBlock
			.withStyleName("section-block")
			.withFullWidth()
			.add(
				versionTitle,
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
							
							currentVersion.setStatus( nextStatus );
							vocabulary.setVersionByLanguage(selectedLanguage, nextStatus);
							
							if( selectedLanguage.equals( sourceLanguage )) {
								vocabulary.setStatus( nextStatus);
							}
							vocabulary.setStatuses( vocabulary.getLatestStatuses() );
							vocabulary.addLanguagePublished( selectedLanguage.name().toLowerCase());
							
							if( nextStatus.equals( Status.PUBLISHED.toString())) {
								currentVersion.setVersionNotes( versionNotes.getValue());
								currentVersion.setNumber( versionNumberField.getValue());
								currentVersion.setPublicationDate( LocalDate.now());
								vocabulary.setVersionByLanguage(selectedLanguage, versionNumberField.getValue());
								if( selectedLanguage.equals( sourceLanguage ))
									vocabulary.setPublicationDate( LocalDate.now());
								
								CVVersion cvVersion = new CVVersion();
								cvVersion.setPublicationDate( LocalDate.now());
								cvVersion.setContainerId( cvScheme.getContainerId());
							}
							
							// save to database
							vocabulary = vocabularyService.save(vocabulary);
							
							// index for editor
							vocabularyService.index(vocabulary);
							
							// index for publication
							if( nextStatus.equals( Status.PUBLISHED.toString()))
								vocabularyService.indexPublish(vocabulary);
							
							// save to flatDB
							cvScheme.setStatus( nextStatus );
							cvScheme.save();
						
							DDIStore ddiStore = stardatDDIService.saveElement(cvScheme.ddiStore, SecurityUtils.getCurrentUserLogin().get(), "Publish Cv");
							
							eventBus.publish(EventScope.UI, DetailView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVSCHEME_UPDATED, null) );
							closeDialog();
						}
					}
				);
	}
	
	public void closeDialog() {
		this.close();
	}

}
