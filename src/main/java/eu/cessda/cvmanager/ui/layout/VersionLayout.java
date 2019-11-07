package eu.cessda.cvmanager.ui.layout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.vaadin.ui.TextArea;
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

    private enum LayoutMode {READ, EDIT};

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
        versionMap = VersionDTO.generateVersionMap(vocabulary.getVersions());

    }

    public void refreshContent(VersionDTO version) {
        this.removeAllComponents();
        if (version.getItemType().equals(ItemType.SL.toString())) {
            this.add(new MLabel("<h3>Source</h3>").withFullWidth().withContentMode(ContentMode.HTML));
        } else {
            this.add(new MLabel("<h3>Translation</h3>").withFullWidth().withContentMode(ContentMode.HTML));
        }


        for (Map.Entry<String, List<VersionDTO>> eachVersions : versionMap.entrySet()) {
            boolean expandLayout = true;
            if (eachVersions.getKey().startsWith(ItemType.SL.toString())) {
                for (VersionDTO orderedVer : eachVersions.getValue()) {
                    boolean showSlVersion = false;
                    // only shows version in specific language
                    if (!version.getLanguage().equals(orderedVer.getLanguage()))
                        continue;
                    // do not show if version number not exist
                    if (orderedVer.getNumber() == null)
                        continue;
                    //only show equal or lower version
                    if (version.getNumber() == null || VersionUtils.compareVersion(orderedVer.getNumber(), version.getNumber()) <= 0)
                        showSlVersion = true;

                    if (orderedVer.getStatus().equals(Status.PUBLISHED.toString()) && showSlVersion) {
                        this.add(generateVersion(orderedVer, expandLayout));
                        expandLayout = false;
                    }
                }
            } else {
                for (VersionDTO orderedVer : eachVersions.getValue()) {
                    boolean showTlVersion = false;
                    // only shows its versions
                    if (!version.getLanguage().equals(orderedVer.getLanguage()))
                        continue;

                    if (orderedVer.getNumber() == null)
                        continue;
                    //only show equal or lower version
                    if (version.getNumber() == null || VersionUtils.compareVersion(orderedVer.getNumber(), version.getNumber()) <= 0)
                        showTlVersion = true;

                    if (orderedVer.getStatus().equals(Status.PUBLISHED.toString()) && showTlVersion) {
                        this.add(generateVersion(orderedVer, showTlVersion));
                        expandLayout = false;
                    }
                }
            }
        }
    }

    public MCssLayout generateVersion(VersionDTO versionDTO, boolean expand) {
        MCssLayout versionLayout = new MCssLayout();
        MCssLayout panelHead = new MCssLayout();
        MButton toggleButton = new MButton().withStyleName("nostyle-button", "pull-left");
        MLabel infoVersion = new MLabel().withContentMode(ContentMode.HTML);
        MLabel noteVersionLabel = new MLabel().withContentMode(ContentMode.HTML).withFullWidth().withVisible(false);
        MLabel changeVersionLabel = new MLabel().withContentMode(ContentMode.HTML).withFullWidth().withVisible(false);
        MLabel noteVersion = new MLabel().withContentMode(ContentMode.HTML).withFullWidth().withVisible(false);
        MLabel changeVersion = new MLabel().withContentMode(ContentMode.HTML).withFullWidth().withVisible(false);
        String cvUrl = null;

        MCssLayout infoLayout = new MCssLayout().withFullSize().withVisible(false);
        TextArea versionNotesTf = new TextArea();
        TextArea changeVersionTf = new TextArea();
        MButton editSwitchButton = new MButton("Edit").withVisible(false);
        MCssLayout buttonLayout = new MCssLayout().withFullWidth();
        MButton saveButton = new MButton("Save");
        MButton cancelButton = new MButton("Cancel");

        // set initial view to read mode
        switchMode(noteVersionLabel, noteVersion, versionNotesTf, changeVersionLabel, changeVersion, changeVersionTf,
                editSwitchButton, buttonLayout, versionDTO, LayoutMode.READ);

        MButton comparatorLayoutToggleButton = new MButton("Show changes from previous version");
        comparatorLayoutToggleButton
                .withStyleName(ValoTheme.BUTTON_LINK + " pull-left")
                .withVisible(false);

        CvComparatorLayout comparatorLayout = new CvComparatorLayout(conceptService);
        comparatorLayout
                .withStyleName("compare-version")
                .withVisible(false);

        comparatorLayoutToggleButton.addClickListener(e -> {
            if (comparatorLayout.isVisible()) {
                comparatorLayout.setVisible(false);
                e.getButton().setCaption("Show comparison with previous version");
            } else {
                if (!comparatorLayout.isVersionCompared()) {
                    VersionDTO prevVersion = vocabulary.getVersionById(versionDTO.getPreviousVersion());
                    comparatorLayout.compareVersion(prevVersion, versionDTO, true);
                    comparatorLayout.showChangeLog(false);
                    versionLayout.add(comparatorLayout);
                    comparatorLayout.setVisible(true);
                }
                comparatorLayout.setVisible(true);
                e.getButton().setCaption("Hide comparison with previous version");
            }
        });

        toggleButton
                .withIcon(VaadinIcons.PLUS)
                .addClickListener(e -> {
                    switchMode(noteVersionLabel, noteVersion, versionNotesTf, changeVersionLabel, changeVersion, changeVersionTf,
                            editSwitchButton, buttonLayout, versionDTO, LayoutMode.READ);
                    if (e.getButton().getIcon().equals(VaadinIcons.PLUS)) {
                        e.getButton().setIcon(VaadinIcons.MINUS);
                        comparatorLayoutToggleButton.setVisible(true);
                        infoLayout.setVisible(true);
                    } else {
                        e.getButton().setIcon(VaadinIcons.PLUS);
                        noteVersionLabel.setVisible(false);
                        changeVersionLabel.setVisible(false);
                        comparatorLayoutToggleButton
                                .withCaption("Show comparison with previous version")
                                .withVisible(false);
                        comparatorLayout.setVisible(false);
                        infoLayout.setVisible(false);
                        editSwitchButton.setVisible(false);
                    }
                });

        panelHead
                .withFullWidth()
                .add(toggleButton, infoVersion);

        try {
            cvUrl = baseUrl + URLEncoder.encode(versionDTO.getUri(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            cvUrl = baseUrl + versionDTO.getUri();
            log.error(e.getMessage());
            log.debug("Stacktrace: ", e);
        }

        infoVersion
                .withValue("<h2>" +
                        "<a href='" + cvUrl + "'>" + versionDTO.getLanguage() + ": " + versionDTO.getNumber() + "</a> " +
                        " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Date of publication: " + versionDTO.getPublicationDate() + "</h2>");

        noteVersionLabel.withValue("<h2>Version notes</h2>");
        noteVersion.withValue(versionDTO.getVersionNotes());
        changeVersionLabel.withValue("<h2>Changes since previous version</h2>");

        if (versionDTO.getVersionChanges() != null && !versionDTO.getVersionChanges().isEmpty())
            changeVersion.withValue(versionDTO.getVersionChanges().replaceAll("(\r\n|\n)", "<br />"));

        editSwitchButton
                .withStyleName("pull-right")
                .addClickListener(e -> switchMode(noteVersionLabel, noteVersion, versionNotesTf, changeVersionLabel, changeVersion, changeVersionTf,
                        editSwitchButton, buttonLayout, versionDTO, LayoutMode.EDIT));

        versionNotesTf.setWidth("100%");
        versionNotesTf.setHeight("200px");
        if (versionDTO.getVersionNotes() != null)
            versionNotesTf.setValue(versionDTO.getVersionNotes());

        changeVersionTf.setWidth("100%");
        changeVersionTf.setHeight("400px");
        if (versionDTO.getVersionChanges() != null)
            changeVersionTf.setValue( versionDTO.getVersionChanges());

        saveButton
                .withStyleName("pull-right", ValoTheme.BUTTON_PRIMARY)
                .addClickListener(e -> {
                    if (versionNotesTf.getValue().isEmpty()) {
                        versionDTO.setVersionNotes("");
                    } else {
                        versionDTO.setVersionNotes(ParserUtils.toXHTML(versionNotesTf.getValue()));
                    }
                    if (changeVersionTf.getValue().isEmpty()) {
                        versionDTO.setVersionChanges("");
                    } else {
                        versionDTO.setVersionChanges( changeVersionTf.getValue());
                    }
                    versionService.save(versionDTO);
                    noteVersion.setValue( versionDTO.getVersionNotes());
                    changeVersion.setValue( versionDTO.getVersionChanges().replaceAll("(\r\n|\n)", "<br />") );


                    switchMode(noteVersionLabel, noteVersion, versionNotesTf, changeVersionLabel, changeVersion, changeVersionTf,
                            editSwitchButton, buttonLayout, versionDTO, LayoutMode.READ);
                });

        cancelButton
                .withStyleName("pull-right")
                .addClickListener(e -> switchMode(noteVersionLabel, noteVersion, versionNotesTf, changeVersionLabel, changeVersion, changeVersionTf,
                        editSwitchButton, buttonLayout, versionDTO, LayoutMode.READ));

        buttonLayout
                .add(saveButton, cancelButton);

        infoLayout.add(
                noteVersionLabel,
                noteVersion,
                versionNotesTf,
                changeVersionLabel,
                changeVersion,
                changeVersionTf,
                editSwitchButton,
                buttonLayout
        );

		if( versionDTO.isInitialVersion() ) {
            versionLayout
                .withStyleName("version-item")
                .add(
                        panelHead,
                        infoLayout
                );
		}
		else {
			versionLayout
			.withStyleName( "version-item" )
			.add(
                panelHead,
                infoLayout,
                comparatorLayoutToggleButton
			);
		}

        if (expand)
            toggleButton.click();

        return versionLayout;
    }

    private void switchMode(MLabel noteVersionLabel, MLabel noteVersion, TextArea versionNotesTf,
                            MLabel changeVersionLabel, MLabel changeVersion, TextArea changeVersionTf,
                            MButton editSwitchButton, MCssLayout buttonLayout, VersionDTO versionDTO,
                            LayoutMode layoutMode) {
        if (layoutMode.equals(LayoutMode.READ)) {
            noteVersion.setVisible(true);
            changeVersion.setVisible(true);
            noteVersionLabel.setVisible(false);
            changeVersionLabel.setVisible(false);

            if (noteVersion.getValue() != null && !noteVersion.getValue().isEmpty())
                noteVersionLabel.setVisible(true);
            if (changeVersion.getValue() != null && !changeVersion.getValue().isEmpty())
                changeVersionLabel.setVisible(true);

            if (CvManagerSecurityUtils.isAuthenticated() && CvManagerSecurityUtils.isCurrentUserAllowToEditMetadata(agency, versionDTO))
                editSwitchButton.setVisible(true);
            else
                editSwitchButton.setVisible(false);

            versionNotesTf.setVisible(false);
            changeVersionTf.setVisible(false);
            buttonLayout.setVisible(false);
        } else {
            noteVersionLabel.setVisible(true);
            changeVersionLabel.setVisible(true);
            versionNotesTf.setVisible(true);
            changeVersionTf.setVisible(true);
            buttonLayout.setVisible(true);
            noteVersion.setVisible(false);
            changeVersion.setVisible(false);
            editSwitchButton.setVisible(false);
        }
    }

    @Override
    public void updateMessageStrings(Locale locale) {

    }

    private CvComparatorLayout generateCompareLayout(VersionDTO currentVersion) {
        CvComparatorLayout comparatorLayout = new CvComparatorLayout(conceptService);
        comparatorLayout.withStyleName("compare-version");
        VersionDTO prevVersion = vocabulary.getVersionById(currentVersion.getPreviousVersion());
        comparatorLayout.compareVersion(prevVersion, currentVersion, true);
        return comparatorLayout;
    }


}
