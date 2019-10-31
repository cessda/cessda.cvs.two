package eu.cessda.cvmanager.ui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.security.SecurityService;
import org.gesis.wts.ui.view.admin.CvManagerAdminView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.repository.search.VocabularyPublishSearchRepository;
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
import eu.cessda.cvmanager.service.impl.ImportServiceImpl;
import eu.cessda.cvmanager.utils.WorkflowUtils;

@UIScope
@SpringView(name = ManageImportView.VIEW_NAME)
public class ManageImportView extends CvManagerAdminView {

	private static Logger log = LoggerFactory.getLogger(ManageImportView.class);
	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "manage-import";
	private final transient VocabularyService vocabularyService;
	private final transient CodeService codeService;
	private final transient VersionService versionService;
	private final transient ConceptService conceptService;
	private final transient VocabularyChangeService vocabularyChangeService;
	private final transient VocabularySearchRepository vocabularySearchRepository;
	private final transient VocabularyPublishSearchRepository vocabularyPublishSearchRepository;

	// autowired
	private final transient ImportServiceImpl importServiceImpl;

	// components
	private MLabel pageTitle = new MLabel();
	private MVerticalLayout layout = new MVerticalLayout();

	public ManageImportView(I18N i18n, EventBus.UIEventBus eventBus, SecurityService securityService,
			ImportServiceImpl importServiceImpl, BCryptPasswordEncoder encrypt, VocabularyService vocabularyService,
			CodeService codeService, VersionService versionService,
			VocabularyChangeService vocabularyChangeService, ConceptService conceptService,
			VocabularySearchRepository vocabularySearchRepository,
			VocabularyPublishSearchRepository vocabularyPublishSearchRepository) {
		super(VIEW_NAME, i18n, eventBus, securityService, CvManagerAdminView.ActionType.DEFAULT.toString());
		eventBus.subscribe(this, ManageImportView.VIEW_NAME);
		this.vocabularyService = vocabularyService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.versionService = versionService;
		this.vocabularyChangeService = vocabularyChangeService;
		this.vocabularySearchRepository = vocabularySearchRepository;
		this.vocabularyPublishSearchRepository = vocabularyPublishSearchRepository;

		this.importServiceImpl = importServiceImpl;
	}

	@PostConstruct
	public void init() {
		pageTitle.withContentMode(ContentMode.HTML).withValue("<h1>Manage Import</h1>");

		layout.withSpacing(false).withMargin(false).withFullSize();

		MButton importStardatDDI = new MButton("Imports stardatDDI to DB");
		importStardatDDI.addClickListener(e -> importServiceImpl.importFromStardat());

		MButton deleteIndexButton = new MButton("Delete ES Index");
		deleteIndexButton.withStyleName(ValoTheme.BUTTON_DANGER).addClickListener(e -> {
			vocabularySearchRepository.deleteAll();
			vocabularyPublishSearchRepository.deleteAll();
		});

		MButton reIndexButton = new MButton("Re-index DB");
		reIndexButton.addClickListener(e -> {
			vocabularySearchRepository.deleteAll();
			vocabularyPublishSearchRepository.deleteAll();
			vocabularyService.findAll().forEach(v -> {
				vocabularyService.index(v);
				vocabularyService.indexPublish(v, null);
			});
		});

		MButton dropContent = generateDropContent();

		MButton assignConceptSlButton = generateAssignSlToTl();

		MCssLayout normalisePanel = normaliseDropVersionError();

		MCssLayout normaliseTlPublishedConceptPanel = normaliseMissingPublishedTLConcepts();

		MCssLayout normaliseSLConceptPanel = normaliseMissingSLConcepts();

		MCssLayout normaliseConceptPanel = normaliseMissingTLConcepts();

		layout.addComponents(pageTitle, importStardatDDI, new MLabel().withFullWidth(), deleteIndexButton,
				reIndexButton, new MLabel().withFullWidth(), assignConceptSlButton, new MLabel().withFullWidth(),
				normalisePanel, new MLabel().withFullWidth(), normaliseSLConceptPanel,
				normaliseConceptPanel, normaliseTlPublishedConceptPanel, new MLabel().withFullWidth(),
				dropContent);
		rightContainer.add(layout).withExpand(layout, 1);
	}

	private MCssLayout normaliseDropVersionError() {
		// Specific function to normalize code when TL missing during drop version
		MCssLayout normalisePanel = new MCssLayout().withFullWidth().withStyleName("panel-group");
		MLabel normalizeTlLabel = new MLabel("<h2>Normalize Code</h2>").withContentMode(ContentMode.HTML);
		MTextField cvTarget = new MTextField("Cv Notation:");
		MButton normalizeButton = new MButton("Normalize");
		normalizeButton.addClickListener(e -> {
			if (cvTarget.isEmpty()) {
				Notification.show("Target CV empty");
				return;
			}
			// find the latest version from specific vocabulary
			VocabularyDTO vocabulary = vocabularyService.getByNotation(cvTarget.getValue());
			if (vocabulary == null) {
				Notification.show("Target CV not found");
				return;
			}
			// get latest version
			List<VersionDTO> latestVersions = vocabulary.getLatestVersionGroup(true);

			Optional<VersionDTO> optSlVersion = latestVersions.stream()
					.filter(v -> v.getItemType().equals(ItemType.SL.toString())).findFirst();
			VersionDTO slVersion = null;

			if (!optSlVersion.isPresent()) {
				Notification.show("SL version not found");
				return;
			}
			slVersion = optSlVersion.get();
			// get latest published code
			List<CodeDTO> latestPublishedCodes = codeService.findArchivedByVocabularyAndVersion(vocabulary.getId(),
					slVersion.getId());

			Map<String, ConceptDTO> slConceptsMap = slVersion.getConceptAsMap();

			// run through TL version and check if concepts exist
			checkIfConceptsExists(cvTarget, latestVersions, latestPublishedCodes, slConceptsMap);

		});

		normalisePanel.add(normalizeTlLabel, cvTarget, normalizeButton);
		return normalisePanel;
	}

	private void checkIfConceptsExists(MTextField cvTarget, List<VersionDTO> latestVersions,
			List<CodeDTO> latestPublishedCodes, Map<String, ConceptDTO> slConceptsMap) {
		for (VersionDTO version : latestVersions) {
			if (version.getItemType().equals(ItemType.SL.toString()))
				continue;

			log.info("Checking: {}  TL: {}", cvTarget.getValue(), version.getLanguage());

			// get concept and match with codes
			Map<String, ConceptDTO> conceptMap = version.getConceptAsMap();

			if (conceptMap.isEmpty()) {
				for (CodeDTO eachCode : latestPublishedCodes) {
					String notation = eachCode.getNotation();
					// check if concept exist if not create new concept
					if (conceptMap.get(notation) == null) {

						// check for SL concept
						ConceptDTO slConcept = slConceptsMap.get(notation);

						String title = eachCode.getTitleByLanguage(version.getLanguage());
						if (title == null || title.trim().isEmpty() || slConcept == null)
							continue;

						String definition = eachCode.getDefinitionByLanguage(version.getLanguage());

						ConceptDTO newConcept = new ConceptDTO();
						newConcept.setUri(WorkflowUtils.generateCodeUri(version.getUri(), version.getNotation(),
								notation, version.getLanguage()) + "/" + version.getNumber());
						newConcept.setNotation(notation);
						newConcept.setTitle(title);
						newConcept.setDefinition(definition);
						newConcept.setCodeId(eachCode.getId());
						newConcept.setSlConcept(slConcept.getId());
						newConcept.setVersionId(version.getId());
						newConcept.setPosition(slConcept.getPosition());

						log.info("Storing Concept : {} TL: {}", notation, version.getLanguage());

						conceptService.save(newConcept);
					}

				}
			}

		}
	}

	private MCssLayout normaliseMissingSLConcepts() {
		// Specific function to normalize code when TL missing during drop version
		MCssLayout normalisePanel = new MCssLayout().withFullWidth().withStyleName("panel-group");
		MLabel normalizeTlLabel = new MLabel("<h2>Normalize SL Concept missing</h2>").withContentMode(ContentMode.HTML);
		MTextField cvTarget = new MTextField("Cv Notation:");
		MButton normalizeButton = new MButton("Normalize");
		normalizeButton.addClickListener(e -> {
			if (cvTarget.isEmpty()) {
				Notification.show("Target CV empty");
				return;
			}
			// find the latest version from specific vocabulary
			VocabularyDTO vocabulary = vocabularyService.getByNotation(cvTarget.getValue());
			if (vocabulary == null) {
				Notification.show("Target CV not found");
				return;
			}
			// get latest version
			List<VersionDTO> latestVersions = vocabulary.getLatestVersionGroup(false);

			Optional<VersionDTO> optSlVersion = latestVersions.stream()
					.filter(v -> v.getItemType().equals(ItemType.SL.toString())).findFirst();
			VersionDTO slVersion = null;

			if (!optSlVersion.isPresent()) {
				Notification.show("SL version not found");
				return;
			}
			slVersion = optSlVersion.get();
			// get latest published code
			List<CodeDTO> workflowCodes = codeService.findWorkflowCodesByVocabulary(vocabulary.getId());

			Map<String, ConceptDTO> slConceptsMap = slVersion.getConceptAsMap();

			// check if SL concept missing
			for (CodeDTO eachCode : workflowCodes) {
				if( slConceptsMap.get( eachCode.getNotation() ) == null ) {
					String title = eachCode.getTitleByLanguage(slVersion.getLanguage());
					String definition = eachCode.getDefinitionByLanguage(slVersion.getLanguage());
					ConceptDTO newConcept = new ConceptDTO();
					newConcept.setUri(WorkflowUtils.generateCodeUri(slVersion.getUri(), slVersion.getNotation(), eachCode.getNotation(),
							slVersion.getLanguage()) + "/" + slVersion.getNumber());
					newConcept.setNotation(eachCode.getNotation());
					newConcept.setTitle(title);
					newConcept.setDefinition(definition);
					newConcept.setCodeId(eachCode.getId());
					newConcept.setVersionId(slVersion.getId());
					newConcept.setPosition(eachCode.getPosition());
					newConcept.setParent( eachCode.getParent() );

					log.debug("Storing Concept : {} SL: {}", slVersion.getNotation(), slVersion.getLanguage());

					conceptService.save(newConcept);
				}
			}
		});

		normalisePanel.add(normalizeTlLabel, cvTarget, normalizeButton);
		return normalisePanel;
	}

	private MCssLayout normaliseMissingTLConcepts() {
		// Specific function to normalize code when TL missing during drop version
		MCssLayout normalisePanel = new MCssLayout().withFullWidth().withStyleName("panel-group");
		MLabel normalizeTlLabel = new MLabel("<h2>Normalize TL Concept missing</h2>").withContentMode(ContentMode.HTML);
		MTextField cvTarget = new MTextField("Cv Notation:");
		MButton normalizeButton = new MButton("Normalize");
		normalizeButton.addClickListener(e -> {
			if (cvTarget.isEmpty()) {
				Notification.show("Target CV empty");
				return;
			}
			// find the latest version from specific vocabulary
			VocabularyDTO vocabulary = vocabularyService.getByNotation(cvTarget.getValue());
			if (vocabulary == null) {
				Notification.show("Target CV not found");
				return;
			}
			// get latest version
			List<VersionDTO> latestVersions = vocabulary.getLatestVersionGroup(false);

			Optional<VersionDTO> optSlVersion = latestVersions.stream()
					.filter(v -> v.getItemType().equals(ItemType.SL.toString())).findFirst();
			VersionDTO slVersion = null;

			if (!optSlVersion.isPresent()) {
				Notification.show("SL version not found");
				return;
			}
			slVersion = optSlVersion.get();
			// get latest published code
			List<CodeDTO> workflowCodes = codeService.findWorkflowCodesByVocabulary(vocabulary.getId());

			Map<String, ConceptDTO> slConceptsMap = slVersion.getConceptAsMap();

			// remove all concepts
			for (VersionDTO version : latestVersions) {
				if (version.getItemType().equals(ItemType.SL.toString()))
					continue;

				log.debug("Delete concept: {} TL: {}", cvTarget.getValue(), version.getLanguage());

				// remova all concepts TL
				for (ConceptDTO removeConcept : version.getConcepts())
					conceptService.delete(removeConcept.getId());

				version.setConcepts(Collections.emptySet());
			}

			// run through TL version and check if concepts exist
			for (VersionDTO version : latestVersions) {
				if (version.getItemType().equals(ItemType.SL.toString()))
					continue;

				log.debug("Checking: {} TL: {}", cvTarget.getValue(), version.getLanguage());

				for (CodeDTO eachCode : workflowCodes) {
					String notation = eachCode.getNotation();
					String title = eachCode.getTitleByLanguage(version.getLanguage());
					if (title == null || title.trim().isEmpty())
						continue;

					// check for SL concept
					ConceptDTO slConcept = slConceptsMap.get(notation);
					if (slConcept == null)
						continue;

					String definition = eachCode.getDefinitionByLanguage(version.getLanguage());

					ConceptDTO newConcept = new ConceptDTO();
					newConcept.setUri(WorkflowUtils.generateCodeUri(version.getUri(), version.getNotation(), notation,
							version.getLanguage()) + "/" + version.getNumber());
					newConcept.setNotation(notation);
					newConcept.setTitle(title);
					newConcept.setDefinition(definition);
					newConcept.setCodeId(eachCode.getId());
					newConcept.setSlConcept(slConcept.getId());
					newConcept.setVersionId(version.getId());
					newConcept.setPosition(slConcept.getPosition());

					log.debug("Storing Concept : {} TL: {}", notation, version.getLanguage());

					conceptService.save(newConcept);
				}

			}

		});

		normalisePanel.add(normalizeTlLabel, cvTarget, normalizeButton);
		return normalisePanel;
	}

	private MCssLayout normaliseMissingPublishedTLConcepts() {
		// Specific function to normalize code when TL missing during drop version
		MCssLayout normalisePanel = new MCssLayout().withFullWidth().withStyleName("panel-group");
		MLabel normalizeTlLabel = new MLabel("<h2>Normalize Missing Publishing TL Concept </h2>").withContentMode(ContentMode.HTML);
		MTextField cvTarget = new MTextField("Cv Notation:");
		MButton normalizeButton = new MButton("Normalize");
		normalizeButton.addClickListener(e -> {
			if (cvTarget.isEmpty()) {
				Notification.show("Target CV empty");
				return;
			}
			// find the latest version from specific vocabulary
			VocabularyDTO vocabulary = vocabularyService.getByNotation(cvTarget.getValue());
			if (vocabulary == null) {
				Notification.show("Target CV not found");
				return;
			}
			// get latest version
			List<VersionDTO> latestVersions = vocabulary.getLatestVersionGroup(true);

			Optional<VersionDTO> optSlVersion = latestVersions.stream()
					.filter(v -> v.getItemType().equals(ItemType.SL.toString())).findFirst();
			VersionDTO slVersion = null;

			if (!optSlVersion.isPresent()) {
				Notification.show("SL version not found");
				return;
			}
			slVersion = optSlVersion.get();
			// get latest published code
			List<CodeDTO> slCodes = codeService.findByVocabularyAndVersion( vocabulary.getId(), slVersion.getId());

			Map<String, ConceptDTO> slConceptsMap = slVersion.getConceptAsMap();

			// remove all concepts
//			for (VersionDTO version : latestVersions) {
//				if (version.getItemType().equals(ItemType.SL.toString()))
//					continue;
//
//				log.debug("Delete concept: {} TL: {}", cvTarget.getValue(), version.getLanguage());
//
//				// remova all concepts TL
//				for (ConceptDTO removeConcept : version.getConcepts())
//					conceptService.delete(removeConcept.getId());
//
//				version.setConcepts(Collections.emptySet());
//			}

			// run through TL version and check if concepts exist
			for (VersionDTO version : latestVersions) {
				if (version.getItemType().equals(ItemType.SL.toString()))
					continue;

				log.debug("Checking: {} TL: {}", cvTarget.getValue(), version.getLanguage());
				Map<String, ConceptDTO> conceptTlMap = version.getConceptAsMap();

				for (CodeDTO eachCode : slCodes) {
					ConceptDTO conceptDTO = conceptTlMap.get( eachCode.getNotation() );
					if( conceptDTO == null ) {
						String notation = eachCode.getNotation();
						String title = eachCode.getTitleByLanguage(version.getLanguage());
						if (title == null || title.trim().isEmpty())
							continue;

						// check for SL concept
						ConceptDTO slConcept = slConceptsMap.get(notation);
						if (slConcept == null)
							continue;

						String definition = eachCode.getDefinitionByLanguage(version.getLanguage());

						conceptDTO = new ConceptDTO();
						conceptDTO.setUri(WorkflowUtils.generateCodeUri(version.getUri(), version.getNotation(), notation,
								version.getLanguage()) + "/" + version.getNumber());
						conceptDTO.setNotation(notation);
						conceptDTO.setTitle(title);
						conceptDTO.setDefinition(definition);
						conceptDTO.setCodeId(eachCode.getId());
						conceptDTO.setSlConcept(slConcept.getId());
						conceptDTO.setVersionId(version.getId());
						conceptDTO.setPosition(slConcept.getPosition());
						conceptDTO.setParent( slConcept.getParent() );

						log.debug("Storing Concept : {} TL: {}", notation, version.getLanguage());

						conceptService.save(conceptDTO);
					} else{
						conceptDTO.setCodeId(eachCode.getId());
						conceptService.save(conceptDTO);
					}
				}

			}

		});

		normalisePanel.add(normalizeTlLabel, cvTarget, normalizeButton);
		return normalisePanel;
	}

	private MButton generateDropContent() {
		MButton dropContent = new MButton("Drop database content and index");
		dropContent.addStyleName(ValoTheme.BUTTON_DANGER);

		dropContent.addClickListener(e -> {
			ConfirmDialog.show(this.getUI(), "Confirm", "Are you sure you want to permanently drop the entire content?",
					"yes", "cancel",

					dialog -> {
						if (dialog.isConfirmed()) {
							dropContent();
						}
					});
		});
		return dropContent;
	}

	private void dropContent() {
		vocabularyService.findAll().forEach(v -> {
			for (VersionDTO version : v.getVersions()) {
				for (ConceptDTO concept : version.getConcepts())
					conceptService.delete(concept.getId());

				for (VocabularyChangeDTO vc : vocabularyChangeService.findAllByVocabularyVersionId(v.getId(),
						version.getId()))
					vocabularyChangeService.delete(vc.getId());

				versionService.delete(version.getId());
			}
			// remove all codes
			for (CodeDTO code : codeService.findByVocabulary(v.getId()))
				codeService.delete(code);

			// remove all vocabulary in DB
			vocabularyService.delete(v.getId());
		});
		vocabularySearchRepository.deleteAll();
		vocabularyPublishSearchRepository.deleteAll();
	}

	private MButton generateAssignSlToTl() {
		MButton assignConceptSlButton = new MButton("Assign concept TL with SL key");
		assignConceptSlButton.addStyleName(ValoTheme.BUTTON_DANGER);
		assignConceptSlButton.addClickListener(e -> {
			ConfirmDialog.show(this.getUI(), "Confirm", "Are you sure you want to assign concept TL with SL key?",
					"yes", "cancel",

					dialog -> {
						if (dialog.isConfirmed()) {
							assignSlToTl();
						}
					}

			);
		});
		return assignConceptSlButton;
	}

	private void assignSlToTl() {
		vocabularyService.findAll().forEach(v -> {
			Map<String, List<VersionDTO>> orderedLanguageVersionMap = versionService
					.getOrderedLanguageVersionMap(v.getId());
			for (VersionDTO version : v.getVersions()) {
				if (version.getItemType().equals(ItemType.TL.toString()))
					continue;
				// the version is SL
				// collect TLs version that related to SL
				List<VersionDTO> relatedTLversions = new ArrayList<>();
				for (Map.Entry<String, List<VersionDTO>> entry : orderedLanguageVersionMap.entrySet()) {
					for (VersionDTO eachVersion : entry.getValue()) {
						if (eachVersion.getUriSl() != null && eachVersion.getUriSl().equals(version.getUri()))
							relatedTLversions.add(eachVersion);
					}
				}
				if (!relatedTLversions.isEmpty()) {
					Map<String, ConceptDTO> slConceptMap = version.getConceptAsMap();
					for (VersionDTO tlVersion : relatedTLversions) {
						for (ConceptDTO tlConcept : tlVersion.getConcepts()) {
							ConceptDTO slConcept = slConceptMap.get(tlConcept.getNotation());
							if (slConcept != null) {
								tlConcept.setSlConcept(slConcept.getId());
								conceptService.save(tlConcept);
							}
						}
					}
				}
			}
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		authorizeViewAccess();

		Locale locale = UI.getCurrent().getLocale();
		updateMessageStrings(locale);
	}

	@Override
	public void afterViewChange(ViewChangeEvent arg0) {
		// Nothing to do yet
	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent arg0) {
		// Nothing to do yet
		return false;
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		actionAdminPanel.updateMessageStrings(locale);
	}

}
