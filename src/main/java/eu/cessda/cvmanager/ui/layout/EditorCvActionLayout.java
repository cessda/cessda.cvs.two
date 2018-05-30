package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.component.ResponsiveBlock;
import eu.cessda.cvmanager.ui.view.CvView;
import eu.cessda.cvmanager.ui.view.window.DialogAddLanguageWindow;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;

public class EditorCvActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;
	
	private final StardatDDIService stardatDDIService;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final VocabularyMapper vocabularyMapper;
	private final VocabularySearchRepository vocabularySearchRepository;
	
	private AgencyDTO agency;
	private VocabularyDTO vocabulary;
		
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private Language selectedLanguage;
	
	private CVScheme cvScheme;
	
	private MButton buttonAddCv = new MButton();
	private MButton buttonEditCv = new MButton();
	private MButton buttonAddTranslation = new MButton();
	
	private MButton buttonValidateCv= new MButton();
	private MButton buttonFinaliseReview = new MButton();
	private MButton buttonPublishCv = new MButton();
	private MButton buttonUnpublishCv = new MButton();
	
	
	public EditorCvActionLayout(String titleHeader, String showHeader, I18N i18n, StardatDDIService stardatDDIService,
			AgencyService agencyService, VocabularyService vocabularyService, VocabularyMapper vocabularyMapper,
			VocabularySearchRepository vocabularySearchRepository) {
		super(titleHeader, showHeader, i18n);
		this.i18n = i18n;
		this.stardatDDIService = stardatDDIService;
		this.agencyService = agencyService;
		this.vocabularyService = vocabularyService;
		this.vocabularyMapper = vocabularyMapper;
		this.vocabularySearchRepository = vocabularySearchRepository;
		
		init();
	}

	private void init() {
		buttonAddCv
			.withFullWidth()
			.addClickListener( this::doCvAdd );
		
		buttonEditCv
			.withFullWidth()
			.addClickListener( this::doCvEdit );
		
		buttonAddTranslation
			.withFullWidth()
			.addClickListener( this::doCvAddTranslation );
		
		updateMessageStrings(locale);
		
		getInnerContainer()
			.add(
				buttonAddCv,
				buttonEditCv,
				buttonAddTranslation
			);
	}

	private void doCvAdd( ClickEvent event ) {
		
		CVScheme newCvScheme = new CVScheme();
		newCvScheme.loadSkeleton(newCvScheme.getDefaultDialect());
		newCvScheme.createId();
		newCvScheme.setContainerId(newCvScheme.getId());
		newCvScheme.setStatus( Status.DRAFT.toString() );

		Window window = new DialogCVSchemeWindow(stardatDDIService, agencyService, vocabularyService, vocabularyMapper, vocabularySearchRepository, newCvScheme, vocabulary, i18n, null);
		getUI().addWindow(window);
	}
	
	private void doCvEdit( ClickEvent event ) {
		Window window = new DialogCVSchemeWindow(stardatDDIService, agencyService, vocabularyService, vocabularyMapper, vocabularySearchRepository, cvScheme, vocabulary, i18n, selectedLanguage);
		getUI().addWindow(window);
	}
	
	private void doCvAddTranslation(ClickEvent event ) {
		
		Window window = new DialogAddLanguageWindow(stardatDDIService, cvScheme, vocabulary, agency, vocabularyService);
		getUI().addWindow(window);
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		buttonAddCv.withCaption(i18n.get("view.action.button.cvscheme.new", locale));
		buttonEditCv.withCaption(i18n.get("view.action.button.cvscheme.edit", locale));
		buttonAddTranslation.withCaption( i18n.get("view.action.button.cvscheme.translation", locale));
	}

	public AgencyDTO getAgency() {
		return agency;
	}

	public void setAgency(AgencyDTO agency) {
		this.agency = agency;
	}

	public VocabularyDTO getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(VocabularyDTO vocabulary) {
		this.vocabulary = vocabulary;
	}

	public Language getSelectedLanguage() {
		return selectedLanguage;
	}

	public void setSelectedLanguage(Language selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	public CVScheme getCvScheme() {
		return cvScheme;
	}

	public void setCvScheme(CVScheme cvScheme) {
		this.cvScheme = cvScheme;
	}

	public MButton getButtonAddCv() {
		return buttonAddCv;
	}

	public MButton getButtonEditCv() {
		return buttonEditCv;
	}

	public MButton getButtonAddTranslation() {
		return buttonAddTranslation;
	}
	
	public boolean hasActionRight() {
		boolean hasAction = false;
		if( !SecurityUtils.isAuthenticated() ) {
			setVisible( false );
		}
		else {
			setVisible( true );
			hasAction = true;
			// check add CV button
			if( !SecurityUtils.isCurrentUserAllowCreateCvSl() )
				buttonAddCv.setVisible( false );
			else
				buttonAddCv.setVisible( true );
			
			// TODO check edit CV button
			if( !SecurityUtils.isCurrentUserAllowCreateCvSl() )
				buttonEditCv.setVisible( false );
			else
				buttonEditCv.setVisible( true );
			
			if( !SecurityUtils.isCurrentUserAllowCreateCvTl(getAgency()) )
				buttonAddTranslation.setVisible( false );
			else
				buttonAddTranslation.setVisible( true );
			
		}
		
		return hasAction;
	}
}
