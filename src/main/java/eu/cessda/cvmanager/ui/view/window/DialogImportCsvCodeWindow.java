package eu.cessda.cvmanager.ui.view.window;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.Query;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
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
import eu.cessda.cvmanager.service.mapper.CsvRowToConceptDTOMapper;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.DetailsView;
import eu.cessda.cvmanager.ui.view.importing.CsvImportLayout;
import eu.cessda.cvmanager.ui.view.importing.CsvRow;
import eu.cessda.cvmanager.utils.CvCodeTreeUtils;

public class DialogImportCsvCodeWindow extends MWindow implements Translatable{

	private static final long serialVersionUID = -2960064213533383226L;
	private static final Logger log = LoggerFactory.getLogger(DialogImportCsvCodeWindow.class);

	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;
	private final VocabularyService vocabularyService;
	private final VersionService versionService;
	private final ConceptService conceptService;
	private final CodeService codeService;
	private final VocabularyChangeService vocabularyChangeService;
	private final CsvRowToConceptDTOMapper csvRowToConceptDTOMapper;
	
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

	public DialogImportCsvCodeWindow(EventBus.UIEventBus eventBus, VocabularyService vocabularyService, 
			VersionService versionService, CodeService codeService, ConceptService conceptService, VocabularyDTO vocabularyDTO,
			VersionDTO versionDTO, I18N i18n, Locale locale, VocabularyChangeService vocabularyChangeService,
			CsvRowToConceptDTOMapper csvRowToConceptDTOMapper) {
		super("Import Csv");
		
		this.eventBus = eventBus;
		this.i18n = i18n;
		this.vocabulary = vocabularyDTO;
		this.version = versionDTO;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.vocabularyService = vocabularyService;
		this.versionService = versionService;
		this.vocabularyChangeService = vocabularyChangeService;
		this.csvRowToConceptDTOMapper = csvRowToConceptDTOMapper;

		language = Language.valueOfEnum( this.version.getLanguage());
		
		csvImportLayout = new CsvImportLayout(i18n, Language.valueOfEnum( version.getLanguage() ), this);

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

		updateMessageStrings(locale);
	}

	private void saveCode() {
		for(CsvRow csvRow: csvImportLayout.getCsvRows()) {
			concept = csvRowToConceptDTOMapper.toDto(csvRow);
			// Check imput
			if(concept.getNotation() == null || concept.getNotation().isEmpty())
				continue;
			
			// remove any non a-z
			// TODO: determine parent
			concept.setNotation( concept.getNotation().replaceAll("[^A-Za-z]", ""));
			
			if(concept.getTitle() == null || concept.getTitle().isEmpty())
				continue;
			
			String uri = version.getUri();
			int lastIndex = uri.lastIndexOf("/");
			if( lastIndex == -1) {
				uri = ConfigurationService.DEFAULT_CV_LINK;
				if(!uri.endsWith("/"))
					uri += "/";
				uri += version.getNotation();
			} else {
				uri = uri.substring(0, lastIndex);
			}
				
			code = new CodeDTO();
			code.setNotation( concept.getNotation() );
			code.setUri( code.getNotation() );
			code.setTitleDefinition( concept.getTitle(), concept.getDefinition(), language);
			
			concept.setUri( uri + "#" + concept.getNotation() + "/" + language.toString());
			
			if( !code.isPersisted() ) {
				code.setSourceLanguage( language.toString());
				code.setVocabularyId( vocabulary.getId() );
			}
			
			// save the code
			if( parentCode == null) {
				
				List<CodeDTO> codeDTOs = codeService.findWorkflowCodesByVocabulary( vocabulary.getId());
				// re-save tree structure 
				TreeData<CodeDTO> codeTreeData = CvCodeTreeUtils.getTreeDataByCodes( codeDTOs );
				codeTreeData.addRootItems(code);
				
				List<CodeDTO> newCodeDTOs = CvCodeTreeUtils.getCodeDTOByCodeTree(codeTreeData);
				for( CodeDTO eachCode: newCodeDTOs) {
					if( !eachCode.isPersisted())
						code = codeService.save(eachCode);
					else
						codeService.save(eachCode);
				}
				
			}
			
			// save to concept
			if( !concept.isPersisted()) {
				vocabulary.addCode(code);
				concept.setCodeId( code.getId());
				concept.setVersionId( version.getId() );
				concept.setPosition( version.getConcepts().size());
				concept = conceptService.save(concept);
				version.addConcept(concept);
				version = versionService.save(version);
			}

			// save change log
			VocabularyChangeDTO changeDTO = new VocabularyChangeDTO();
			changeDTO.setVocabularyId( vocabulary.getId());
			changeDTO.setVersionId( version.getId()); 
			changeDTO.setChangeType( "Code added" );
			changeDTO.setDescription( concept.getNotation());
			changeDTO.setDate( LocalDateTime.now() );
			UserDetails loggedUser = SecurityUtils.getLoggedUser();
			changeDTO.setUserId( loggedUser.getId() );
			changeDTO.setUserName( loggedUser.getFirstName() + " " + loggedUser.getLastName());
			vocabularyChangeService.save(changeDTO);
		}
		
		

		// indexing editor
		vocabularyService.index(vocabulary);
		
		eventBus.publish(EventScope.UI, DetailsView.VIEW_NAME, this, new CvManagerEvent.Event( EventType.CVCONCEPT_CREATED, null) );
		this.close();
	}



	@Override
	public void updateMessageStrings(Locale locale) {

	}

	public MButton getImportButton() {
		return importButton;
	}
	
	
	
}
