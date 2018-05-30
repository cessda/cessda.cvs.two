package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.service.AgencyService;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.domain.enumeration.Status;
import eu.cessda.cvmanager.repository.search.VocabularySearchRepository;
import eu.cessda.cvmanager.service.StardatDDIService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.mapper.VocabularyMapper;
import eu.cessda.cvmanager.ui.component.ResponsiveBlock;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;

public class EditorCodeActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;
	
	private final StardatDDIService stardatDDIService;
	private final AgencyService agencyService;
	private final VocabularyService vocabularyService;
	private final VocabularyMapper vocabularyMapper;
	private final VocabularySearchRepository vocabularySearchRepository;
	
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	
	private MButton buttonAddCv = new MButton();
	
	public EditorCodeActionLayout(String titleHeader, String showHeader, I18N i18n, StardatDDIService stardatDDIService,
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
		
		updateMessageStrings(locale);
		
		getInnerContainer()
			.add(
				buttonAddCv
			);
	}

	private void doCvAdd( ClickEvent event ) {
		
		CVScheme newCvScheme = new CVScheme();
		newCvScheme.loadSkeleton(newCvScheme.getDefaultDialect());
		newCvScheme.createId();
		newCvScheme.setContainerId(newCvScheme.getId());
		newCvScheme.setStatus( Status.DRAFT.toString() );

		Window window = new DialogCVSchemeWindow(stardatDDIService, agencyService, vocabularyService, vocabularyMapper, vocabularySearchRepository, newCvScheme, null, i18n, null);
		getUI().addWindow(window);
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		buttonAddCv.withCaption(i18n.get("view.action.button.cvscheme.new", locale));
	}
}
