package eu.cessda.cvmanager.ui.view.window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.ConceptService;
import eu.cessda.cvmanager.service.I18N;
import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.service.VersionService;
import eu.cessda.cvmanager.service.VocabularyChangeService;
import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.manager.WorkflowManager;
import eu.cessda.cvmanager.ui.layout.CvComparatorLayout;
import eu.cessda.cvmanager.ui.layout.DialogMSLicenseLayout;
import eu.cessda.cvmanager.ui.view.EditorDetailsView;
import eu.cessda.cvmanager.ui.view.PublicationDetailsView;
import eu.cessda.cvmanager.utils.VersionUtils;

public class DialogManageStatusWindow extends MWindow {

	private static final long serialVersionUID = -8944364070898136792L;
	private static final Logger log = LoggerFactory.getLogger(DialogManageStatusWindow.class);

	private final UIEventBus eventBus;
	private final VersionService versionService;
	private final VocabularyChangeService vocabularyChangeService;
	private final ConceptService conceptService;
	private final LicenceService licenceService;
	private final WorkflowManager workflowManager;

	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
	private Language selectedLanguage;
	private Language sourceLanguage;
	private VersionDTO currentVersion;

	private MCssLayout layout = new MCssLayout();

	private MCssLayout changeListBlock = new MCssLayout();
	private MLabel changeListTitle = new MLabel();
	private MGrid<VocabularyChangeDTO> changesGrid = new MGrid<>(VocabularyChangeDTO.class);

	private MCssLayout discussionBlock = new MCssLayout();
	private MLabel discussionTitle = new MLabel();
	private TextArea discussionArea = new TextArea();
	private MButton buttonDiscussionSave = new MButton("Save notes");

	private MCssLayout statusBlock = new MCssLayout();
	private MLabel statusTitle = new MLabel("Status");
	private MLabel statusInfo = new MLabel("Change CV status from to");
	private MButton buttonReviewInitial = new MButton("Initial review");
	private MButton buttonReviewFinal = new MButton("Final review");
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
	private MCssLayout tlCloneInfoLayout = new MCssLayout();
	private MCssLayout versionButtonLayout = new MCssLayout();
	private MLabel licenseLabel = new MLabel();

	private MLabel versionSeparator1 = new MLabel("<strong>.</strong>").withContentMode(ContentMode.HTML);
	private MLabel versionSeparator2 = new MLabel("<strong>.</strong>").withContentMode(ContentMode.HTML);
	private MTextField versionNumberField1 = new MTextField().withWidth("30px");
	private MTextField versionNumberField2 = new MTextField().withWidth("30px");
	private MTextField versionNumberField3 = new MTextField().withWidth("30px");

	private MButton buttonPublishCv = new MButton("Publish");
	private MButton buttonSave = new MButton("Save");
	private MButton cancelButton = new MButton("Cancel", e -> this.close());

	private List<VersionDTO> latestTlVersions = new ArrayList<>();
	private String versionNumberSL = "1.0";
	private String versionNumberTL = "1.0.1";
	private String versionNumberPastSl = "0.9";
	private String versionNumberPastTl = "1.0.0";
	private VersionDTO latestPublishedSl;

	private MCssLayout comparatorBlock = new MCssLayout();
	private MCssLayout comparatorContainer = new MCssLayout().withFullWidth();
	private MCssLayout comparatorContent = new MCssLayout().withFullWidth();
	private MLabel comparatorBlockHead = new MLabel();
	private CvComparatorLayout comparatorLayout;
	private MButton comparatorLayoutToggle = new MButton("Show comparison with previous version");

	private List<LicenceDTO> licenses;
	private DialogMSLicenseLayout licanseContent;

	public DialogManageStatusWindow(ConceptService conceptService, VersionService versionService,
			VocabularyDTO vocabularyDTO, VersionDTO versionDTO, Language selectedLanguage, Language sourceLanguage,
			AgencyDTO agencyDTO, UIEventBus eventBus, VocabularyChangeService vocabularyChangeService,
			LicenceService licenceService, WorkflowManager workflowManager) {
		super("Manage Status " + (sourceLanguage.equals(selectedLanguage) ? " SL " : " TL ")
				+ selectedLanguage.name().toLowerCase());
		this.conceptService = conceptService;
		this.versionService = versionService;
		this.licenceService = licenceService;

		this.agency = agencyDTO;
		this.vocabulary = vocabularyDTO;
		this.currentVersion = versionDTO;
		this.sourceLanguage = sourceLanguage;
		this.selectedLanguage = selectedLanguage;

		this.eventBus = eventBus;
		this.vocabularyChangeService = vocabularyChangeService;
		this.workflowManager = workflowManager;

		init();
	}

	private void init() {
		versionNumberField1.addValueChangeListener(e -> {
			((TextField) e.getComponent()).setValue(e.getValue().replaceAll("[^\\d.]", ""));
		});
		versionNumberField2.addValueChangeListener(e -> {
			((TextField) e.getComponent()).setValue(e.getValue().replaceAll("[^\\d.]", ""));
		});
		versionNumberField3.addValueChangeListener(e -> {
			((TextField) e.getComponent()).setValue(e.getValue().replaceAll("[^\\d.]", ""));
		});
		List<VocabularyChangeDTO> changes = null;
		comparatorLayout = new CvComparatorLayout(conceptService);
		comparatorBlockHead.withFullWidth().withStyleName("section-header").withValue("Compare versions");
		comparatorContainer.withStyleName("comparator-container").add(comparatorLayoutToggle, comparatorContent);
		comparatorBlock.withStyleName("section-block").withFullWidth().add(comparatorBlockHead, comparatorContainer);

		changeListTitle.withFullWidth().withStyleName("section-header").withValue("Change logs");
		if (currentVersion.isInitialVersion())
			changeListBlock.setVisible(false);
		else {
			changes = vocabularyChangeService.findAllByVocabularyVersionId(vocabulary.getId(), currentVersion.getId());
			changesGrid.setItems(changes);
			changesGrid.withFullWidth().withHeight("240px").setColumns("date", "changeType", "description");

			changeListBlock.withStyleName("section-block").withFullWidth().add(changeListTitle, changesGrid);
		}

		discussionTitle.withFullWidth().withStyleName("section-header")
				.withValue(I18N.get("dialog.status.section.discusion.header"));

		discussionArea.setWidth("100%");
		discussionArea.setValue(currentVersion.getDiscussionNotes() == null ? "" : currentVersion.getDiscussionNotes());

		buttonDiscussionSave.withStyleName("action-button2").addClickListener(e -> {
			currentVersion.setDiscussionNotes(discussionArea.getValue());
			currentVersion = versionService.save(currentVersion);
//				vocabulary = vocabularyService.findOne( currentVersion.getVocabularyId() );
			Notification.show("Notes/Discussion is saved!");
		});

		discussionBlock.withStyleName("section-block").withFullWidth().add(discussionTitle, discussionArea,
				buttonDiscussionSave);

		statusTitle.withFullWidth().withStyleName("section-header");

		statusInfo.withFullWidth();

		buttonReviewInitial.withStyleName("action-button2").withVisible(false)
				.addClickListener(this::forwardCvWorkflowConfirmation);
		buttonReviewFinal.withStyleName("action-button2").withVisible(false)
				.addClickListener(this::forwardCvWorkflowConfirmation);
		buttonStatusCancel.withStyleName("action-button2");

		statusButtonLayout.withStyleName("button-layout").add(buttonStatusCancel, buttonReviewInitial,
				buttonReviewFinal);

		statusBlock.withStyleName("section-block").withFullWidth().add(statusTitle, statusInfo, statusButtonLayout);

		if (currentVersion.getStatus().equals(Status.DRAFT.toString())) {
			statusBlock.setVisible(true);
			versionBlock.setVisible(false);
			buttonReviewInitial.setVisible(true);
			buttonReviewFinal.setVisible(false);
			comparatorBlock.setVisible(false);
			discussionArea.addStyleName("height-200");
			statusInfo.setValue("Change CV' " + currentVersion.getItemType() + " " + "\"" + currentVersion.getTitle()
					+ "\"" + " from DRAFT to INITIAL_REVIEW");
		} else if (currentVersion.getStatus().equals(Status.INITIAL_REVIEW.toString())) {
			statusBlock.setVisible(true);
			versionBlock.setVisible(false);
			buttonReviewInitial.setVisible(false);
			buttonReviewFinal.setVisible(true);
			comparatorBlock.setVisible(false);
			discussionArea.addStyleName("height-200");
			statusInfo.setValue("Change CV' " + currentVersion.getItemType() + " " + "\"" + currentVersion.getTitle()
					+ "\"" + " from INITIAL_REVIEW to FINAL_REVIEW");
		} else if (currentVersion.getStatus().equals(Status.FINAL_REVIEW.toString())) {
			statusBlock.setVisible(false);
			buttonDiscussionSave.setVisible(false);
			versionBlock.setVisible(true);
			discussionArea.addStyleName("height-200");
			comparatorBlock.setVisible(true);
			// prepare the version number
			// get latest published
			vocabulary.getLatestVersionByLanguage(vocabulary.getSourceLanguage(), null, Status.PUBLISHED.toString())
					.ifPresent(slPublish -> {
						versionNumberSL = slPublish.getNumber();
						versionNumberPastSl = versionNumberSL;
						if (currentVersion.getItemType().equals(ItemType.SL.toString())) {
							int lastDotIndex = versionNumberSL.lastIndexOf(".");
							String lastNumber = versionNumberSL.substring(lastDotIndex + 1);
							versionNumberSL = versionNumberSL.substring(0, lastDotIndex + 1)
									+ (Integer.parseInt(lastNumber) + 1);
						} else {
							latestPublishedSl = slPublish;
							versionNumberTL = versionNumberSL + ".1";
							versionNumberPastTl = versionNumberSL + ".0";
							vocabulary.getLatestVersionByLanguage(currentVersion.getLanguage(), null,
									Status.PUBLISHED.toString()).ifPresent(tlPublish -> {
										String latestTLPublishNumber = tlPublish.getNumber();
										if (VersionUtils.compareVersion(latestTLPublishNumber, versionNumberSL) > 0) {
											int lastDotIndex2 = latestTLPublishNumber.lastIndexOf(".");
											String lastNumber2 = latestTLPublishNumber.substring(lastDotIndex2 + 1);
											versionNumberTL = latestTLPublishNumber.substring(0, lastDotIndex2 + 1)
													+ (Integer.parseInt(lastNumber2) + 1);
											versionNumberPastTl = latestTLPublishNumber;
										}

									});
						}
					});

			if (sourceLanguage.equals(selectedLanguage)) {
				versionNumberField3.setVisible(false);
				versionSeparator2.setVisible(false);
				if (currentVersion.getNumber() != null) {
					versionNumberSL = currentVersion.getNumber();
				}
				int indexDot = versionNumberSL.indexOf(".");
				versionNumberField1.setValue(versionNumberSL.substring(0, indexDot));
				versionNumberField2.setValue(versionNumberSL.substring(indexDot + 1, versionNumberSL.length()));
			} else {
				versionNumberField1.setVisible(false);
				versionNumberField2.setVisible(false);
				int indexDot = versionNumberTL.lastIndexOf(".");
				versionSeparator1.setValue("<strong>" + versionNumberTL.substring(0, indexDot) + "</strong>");
				versionNumberField3.setValue(versionNumberTL.substring(indexDot + 1, versionNumberTL.length()));
			}

			// If publishing SL
			if (currentVersion.getItemType().equals(ItemType.SL.toString())) {
				// get available TL, get language first and then get the latest TL
				// The latest TL will be listed as the target TL to be cloned
				for (String lang : VocabularyDTO.getLanguagesFromVersions(vocabulary.getVersions())) {
					if (lang.equals(sourceLanguage.getIso()))
						continue;
					latestTlVersions.add(vocabulary.getLatestVersionByLanguage(lang).get());
				}
			}

			// version changes extracts
			if (currentVersion.isInitialVersion()) {
				versionChanges.setVisible(false);
				versionHistoryLayout.setVisible(false);
				versionChangesLabel.setVisible(false);
				comparatorBlock.setVisible(false);
			} else {
				if (currentVersion.getVersionChanges() != null && !currentVersion.getVersionChanges().isEmpty()) {
					versionChanges.setValue(currentVersion.getVersionChanges());
				} else {
					StringBuilder versionChangesContent = new StringBuilder();
					for (VocabularyChangeDTO vc : changes) {
						versionChangesContent.append(vc.getChangeType() + ": " + vc.getDescription() + "\n");
					}
					versionChanges.setValue(versionChangesContent.toString());
				}
			}

			// initialize license
			licenses = licenceService.findAll();
			licanseContent = new DialogMSLicenseLayout(agency, currentVersion, licenses);
		}

		buttonPublishCv.withStyleName("action-button2").addClickListener(this::forwardToPublish);
		buttonSave.withStyleName("action-button2").addClickListener(this::saveWithoutPublish);
		cancelButton.addStyleNames("action-button2");

		versionTitle.withFullWidth().withStyleName("section-header").withValue("Publish and Version");

		versionInfo.withContentMode(ContentMode.HTML).withFullWidth().withValue(
				"<strong>\n" + I18N.get(sourceLanguage.equals(selectedLanguage) ? "window.status.publishversion.text.sl"
						: "window.status.publishversion.text.tl") + "</strong>");

		versionHistoryLayout.withFullWidth().withHeight("200px").withStyleName("yscroll", "white-bg")
				.add(new MLabel("Version history").withStyleName("section-header").withFullWidth());

		versionHistoryLayout
				.add(new MLabel(currentVersion.getSummary() == null ? "no prior version" : currentVersion.getSummary())
						.withContentMode(ContentMode.HTML));

		versionNotesLabel.withFullWidth().withStyleName("section-header margintop15px").withValue("Version notes");

		versionChangesLabel.withFullWidth().withStyleName("section-header margintop15px").withValue("Version changes");

		licenseLabel.withFullWidth().withStyleName("section-header margintop15px").withValue("License");

		versionNotes.setWidth("100%");
		versionNotes.setHeight("160px");

		versionChanges.setWidth("100%");
		versionChanges.setHeight("160px");

		versionNumberLabel.withStyleName("section-header", "pull-left").withValue("Version number: ");

		if (currentVersion.getVersionNotes() != null)
			versionNotes.setValue(currentVersion.getVersionNotes());

		versionButtonLayout.withStyleName("button-layout").add(buttonPublishCv, buttonSave, cancelButton);

		tlCloneInfoLayout.withFullWidth();
		if (!latestTlVersions.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (VersionDTO ver : latestTlVersions) {
				Language verLang = Language.getByIso(ver.getLanguage());
				sb.append(verLang.getFormatted() + " " + (ver.getNumber() == null ? "" : ver.getNumber()) + " ("
						+ ver.getStatus() + ") <br/>");
			}

			tlCloneInfoLayout.add(
					new MLabel().withFullWidth().withContentMode(ContentMode.HTML)
							.withValue("<strong>The following TL items will be cloned as draft : </strong>"),
					new MLabel().withFullWidth().withContentMode(ContentMode.HTML).withValue(sb.toString()));
		}

		versionBlock.withStyleName("section-block").withFullWidth().add(versionTitle, versionHistoryLayout,
				versionNotesLabel, versionNotes, versionChangesLabel, versionChanges, new MLabel().withFullWidth(), // separator
				versionNumberLabel, versionNumberField1, versionSeparator1, versionNumberField2, versionSeparator2,
				versionNumberField3, tlCloneInfoLayout, versionInfo, versionButtonLayout);

		if (currentVersion.getStatus().equals(Status.FINAL_REVIEW.toString())) {
			versionBlock.addComponent(licanseContent, 6);
			versionBlock.addComponent(licenseLabel, 6);
		}

		comparatorContent.add(comparatorLayout);

		comparatorLayout.setVisible(false);

		if (currentVersion.isInitialVersion()) {
			comparatorLayoutToggle.setVisible(false);
			versionNotes.setVisible(false);
			versionNotesLabel.setVisible(false);
			versionInfo.withValue("<strong>" + I18N.get("window.status.publishversion.initial.text") + "</strong>");
		} else {
			comparatorLayoutToggle.addClickListener(e -> {
				if (comparatorLayout.isVisible()) {
					comparatorLayout.setVisible(false);
					e.getButton().setCaption("Show comparison with previous version");
				} else {
					if (!comparatorLayout.isVersionCompared()) {
						VersionDTO prevVersion = vocabulary.getVersionById(currentVersion.getPreviousVersion());
						comparatorLayout.compareVersion(prevVersion, currentVersion);
					}
					comparatorLayout.setVisible(true);
					e.getButton().setCaption("Hide comparison with previous version");
				}

			});
		}
		layout.withFullWidth().withStyleName("dialog-content").add(changeListBlock, discussionBlock, comparatorBlock,
				statusBlock, versionBlock);

		if (currentVersion.getStatus().equals(Status.DRAFT.toString())
				|| currentVersion.getStatus().equals(Status.INITIAL_REVIEW.toString())) {
			if (currentVersion.isInitialVersion())
				this.withHeight("460px");
			else
				this.withHeight("745px");

		} else {
			if (currentVersion.isInitialVersion())
				this.withHeight("640px");
			else
				this.withHeight("800px");
		}

		this.withWidth(Page.getCurrent().getBrowserWindowWidth() * 0.98 + "px").withModal(true).withContent(layout);
	}

	private void saveWithoutPublish() {
		currentVersion.setDiscussionNotes(discussionArea.getValue());
		currentVersion.setVersionNotes(versionNotes.getValue());
		currentVersion.setVersionChanges(versionChanges.getValue());
		currentVersion.setNumber(getVersionNumber());
		currentVersion = versionService.save(currentVersion);
		Notification.show("Changes are saved!");
		close();
	}

	private String getVersionNumber() {
		if (sourceLanguage.equals(selectedLanguage)) {
			return versionNumberField1.getValue() + "." + versionNumberField2.getValue();
		} else {
			int indexDot = versionNumberTL.lastIndexOf(".");
			return versionNumberTL.substring(0, indexDot) + "." + versionNumberField3.getValue();
		}
	}

	private boolean isVersionNumberEmpty() {
		if (sourceLanguage.equals(selectedLanguage)) {
			if ((versionNumberField1.getValue() != null && !versionNumberField1.isEmpty())
					&& (versionNumberField2.getValue() != null && !versionNumberField2.isEmpty()))
				return false;
			return true;
		} else {
			if ((versionNumberField3.getValue() != null && !versionNumberField3.isEmpty()))
				return false;
			return true;
		}
	}

	private void forwardToPublish() {
		// check if publishing TL, if all codes are translated
		if (latestPublishedSl != null && !vocabulary.getSourceLanguage().equals(currentVersion.getLanguage())) {
			Set<String> missingTranslationTL = getMissingTranslatedCode(latestPublishedSl, currentVersion);
			if (!missingTranslationTL.isEmpty()) {
				ConfirmDialog.show(this.getUI(), "Codes Translation Missing",
						"The descriptive term translations are missing on the following codes \""
								+ String.join("\", \"", missingTranslationTL) + "\". "
								+ "Translating all descriptive terms is mandatory for publication. "
								+ "Use export/download if you want to produce a copy of an unfinished translation.",
						"Ok", "Close", dialog -> {
							if (dialog.isConfirmed()) {
								closeDialog();
							}
						});
				return;
			}
		}

		if (versionChanges.isVisible() && (versionChanges.getValue() == null || versionChanges.getValue().isEmpty())) {
			Notification.show("Version changes can not be empty");
			return;
		}

		if (isVersionNumberEmpty()) {
			Notification.show("Version number can not be empty");
			return;
		}

		if (sourceLanguage.equals(selectedLanguage)) {
			log.info(getVersionNumber() + "  " + versionNumberPastSl + "  "
					+ VersionUtils.compareVersion(getVersionNumber(), versionNumberPastSl));

			if (VersionUtils.compareVersion(getVersionNumber(), versionNumberPastSl) <= 0) {
				Notification.show("Version number is lower or simillar with the last version");
				return;
			}
		} else {
			if (VersionUtils.compareVersion(getVersionNumber(), versionNumberPastTl) <= 0) {
				Notification.show("Version number is lower or simillar with the last version");
				return;
			}
		}

		forwardCvWorkflowConfirmation();
	}

	private void forwardCvWorkflowConfirmation() {
		Status currentCvStatus = currentVersion.getEnumStatus();
		Status nextCvStatus = workflowManager.getForwardStatus(currentCvStatus);

		ConfirmDialog.show(this.getUI(), "Confirm", I18N.get(
				"dialog.confirm.status." + currentCvStatus.toString().toLowerCase() + ".to."
						+ nextCvStatus.toString().toLowerCase(),
				(sourceLanguage.equals(selectedLanguage) ? "SL " : "TL ") + "\"" + currentVersion.getTitle() + "\"",
				currentCvStatus.toString(), nextCvStatus.toString()), I18N.get("dialog.button.yes"),
				I18N.get("dialog.button.cancel"), dialog -> {
					if (dialog.isConfirmed()) {
						doForwardCvWorkflow();
						closeDialog();
					}
				});
	}

	private void doForwardCvWorkflow() {
		currentVersion = workflowManager.forwardStatus(vocabulary, currentVersion, agency, latestTlVersions,
				getVersionNumber(), versionNotes.getValue(), versionChanges.getValue());

		if (currentVersion.getStatus().equals(Status.PUBLISHED.toString()))
			UI.getCurrent().getNavigator().navigateTo(PublicationDetailsView.VIEW_NAME + "/" + vocabulary.getNotation()
					+ "?url=" + currentVersion.getUri());
		else
			eventBus.publish(EventScope.UI, EditorDetailsView.VIEW_NAME, this,
					new CvManagerEvent.Event(EventType.CVSCHEME_UPDATED, null));

	}

	public void closeDialog() {
		this.close();
	}

	private Set<String> getMissingTranslatedCode(VersionDTO versionSL, VersionDTO versionTL) {
		if (versionSL == null || versionSL.getConcepts() == null || versionTL == null
				|| versionTL.getConcepts() == null)
			return Collections.emptySet();
		if (versionSL.getConcepts().size() != versionTL.getConcepts().size()) {
			Set<String> notationSLs = versionSL.getConcepts().stream().map(c -> c.getNotation())
					.collect(Collectors.toSet());
			Set<String> notationTLs = versionTL.getConcepts().stream().map(c -> c.getNotation())
					.collect(Collectors.toSet());
			notationSLs.removeAll(notationTLs);
			return notationSLs;
		}
		return Collections.emptySet();
	}

}
