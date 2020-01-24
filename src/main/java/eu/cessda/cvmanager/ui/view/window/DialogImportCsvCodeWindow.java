package eu.cessda.cvmanager.ui.view.window;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.gesis.wts.domain.enumeration.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.ui.Alignment;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CodeService;
import eu.cessda.cvmanager.service.dto.CodeDTO;
import eu.cessda.cvmanager.service.dto.ConceptDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.manager.WorkspaceManager;
import eu.cessda.cvmanager.service.mapper.CsvRowToConceptDTOMapper;
import eu.cessda.cvmanager.ui.view.EditorDetailsView;
import eu.cessda.cvmanager.ui.view.importing.CsvImportLayout;
import eu.cessda.cvmanager.ui.view.importing.CsvRow;

public class DialogImportCsvCodeWindow extends MWindow implements Translatable{

	private static final long serialVersionUID = -2960064213533383226L;
	private static final Logger log = LoggerFactory.getLogger(DialogImportCsvCodeWindow.class);

	private final transient WorkspaceManager workspaceManager;
	private final transient EventBus.UIEventBus eventBus;
	private final transient I18N i18n;
	private final transient CodeService codeService;
	private final transient CsvRowToConceptDTOMapper csvRowToConceptDTOMapper;
	
	private CsvImportLayout csvImportLayout;

	private MCssLayout layout = new MCssLayout();


	private MButton importButton = new MButton("Import Codes", e -> saveCode());
	private MButton cancelButton = new MButton("Cancel", e -> this.close());
	
	private VocabularyDTO vocabulary;
	private VersionDTO version;
	private ConceptDTO concept;
	private CodeDTO code;
	private CodeDTO parentCode;
	private Language language;

	public DialogImportCsvCodeWindow(WorkspaceManager workspaceManager, CodeService codeService, EventBus.UIEventBus eventBus,
									 VocabularyDTO vocabularyDTO, VersionDTO versionDTO, I18N i18n, CsvRowToConceptDTOMapper csvRowToConceptDTOMapper) {
		super("Import Csv");
		this.workspaceManager = workspaceManager;

		this.eventBus = eventBus;
		this.i18n = i18n;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.codeService = codeService;
		this.csvRowToConceptDTOMapper = csvRowToConceptDTOMapper;

		language = Language.getByIso( this.version.getLanguage());
		
		csvImportLayout = new CsvImportLayout(i18n, Language.getByIso( version.getLanguage() ), this);

		importButton.setVisible( false );
		
		layout
			.withFullWidth()
			.withStyleName("dialog-content import-dialog")
			.add( 
				csvImportLayout,
				new MHorizontalLayout()
				.withFullWidth()
				.add( importButton,
					cancelButton
				)
				.withExpand(importButton, 0.8f)
				.withAlign(importButton, Alignment.BOTTOM_RIGHT)
				.withExpand(cancelButton, 0.1f)
				.withAlign(cancelButton, Alignment.BOTTOM_RIGHT)
			);

		
		this
			.withHeight("650px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);

		updateMessageStrings(getLocale());
	}

	private void saveCode() {
		// if source language code is added
		if( vocabulary.getSourceLanguage().equals( language.toString())) {
			for(CsvRow csvRow: csvImportLayout.getCsvRows()) {
				// get workspace code map from this vocabulary
				Map<String, CodeDTO> existWfCodeMap = CodeDTO.getCodeAsMap( codeService.findWorkflowCodesByVocabulary( vocabulary.getId()));
				// determine if code already available, whether skip or update the code
				CodeDTO existCode = existWfCodeMap.get(csvRow.getNotation());
				// skip code that exist
				if( existCode != null )
					continue;
				
				CodeDTO newCode = new CodeDTO();
				concept = csvRowToConceptDTOMapper.toDto(csvRow);
				
				if(concept.getNotation() == null || concept.getNotation().isEmpty() || concept.getTitle() == null || concept.getTitle().isEmpty())
					continue;
				
				// determine if concept has parent by notation structure
				if(concept.getNotation().contains(".")) // if has parent
				{
					int lastDotIndex = concept.getNotation().lastIndexOf(".");
					// find parent code
					CodeDTO parentCode = existWfCodeMap.get( concept.getNotation().substring(0, lastDotIndex));
					if(parentCode == null )
						continue;
					
					String baseNotation = concept.getNotation().substring( lastDotIndex + 1 );
					workspaceManager.saveCodeAndConcept(vocabulary, version, newCode, parentCode, concept, null,
							baseNotation, concept.getTitle(), concept.getDefinition());
				} 
				else { // if top concept
					workspaceManager.saveCodeAndConcept(vocabulary, version, newCode, null, concept, null,
							concept.getNotation(), concept.getTitle(), concept.getDefinition());
				}
				
				// save change log
				workspaceManager.storeChangeLog(vocabulary, version, "Code added", concept.getNotation());
				
			}
		} 
		// otherwise the translated language
		else {
			// get existing published code on specific version
			Map<String, CodeDTO> existWfCodeMap = CodeDTO.getCodeAsMap( codeService.findWorkflowCodesByVocabulary( vocabulary.getId()));
			// get conceptMap from latest SL
			Optional<VersionDTO> latestSlVersionOpt = vocabulary.getLatestSlVersion( true );
			Map<String, ConceptDTO> slConceptMap = new HashMap<>();
			if(latestSlVersionOpt.isPresent())
				slConceptMap = latestSlVersionOpt.get().getConceptAsMap();
			
			for(CsvRow csvRow: csvImportLayout.getCsvRows()) {
				// find out, if code from excel exist on the code
				// if exist then add the TL concept, otherwise ignore
				
				concept = csvRowToConceptDTOMapper.toDto(csvRow);
				
				if(concept.getNotation() == null || concept.getNotation().isEmpty() || concept.getTitle() == null || concept.getTitle().isEmpty())
					continue;
				
				CodeDTO code = existWfCodeMap.get(concept.getNotation() );
				// if there is no code, means no SL concept as well, just skip
				if( code == null )
					continue;
				// create connection with SL concept
				ConceptDTO slConcept = slConceptMap.get( code.getNotation() );
				
				// find parent code
				if( code.getParent() != null ) {
					CodeDTO parentCode = existWfCodeMap.get( code.getParent() );
					if( parentCode == null )
						continue;
					
					workspaceManager.saveCodeAndConcept(vocabulary, version, code, parentCode, concept, slConcept,
							concept.getNotation(), concept.getTitle(), concept.getDefinition());
				} else {
					workspaceManager.saveCodeAndConcept(vocabulary, version, code, null, concept, slConcept,
							concept.getNotation(), concept.getTitle(), concept.getDefinition());
				}
					
				// save change log
				workspaceManager.storeChangeLog(vocabulary, version, "Code TL added", concept.getNotation());
				
			}
		
		}
		
		eventBus.publish(EventScope.UI, EditorDetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, null) );
		this.close();
	}



	@Override
	public void updateMessageStrings(Locale locale) {

	}

	public MButton getImportButton() {
		return importButton;
	}
	
	
	
}
