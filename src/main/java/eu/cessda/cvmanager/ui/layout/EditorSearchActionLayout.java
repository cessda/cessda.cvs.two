package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import eu.cessda.cvmanager.service.manager.WorkspaceManager;
import org.gesis.wts.service.AgencyService;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;

import com.vaadin.ui.Window;

import eu.cessda.cvmanager.service.dto.VersionDTO;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.component.ResponsiveBlock;
import eu.cessda.cvmanager.ui.view.window.DialogCVSchemeWindow;

public class EditorSearchActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;

	private final transient WorkspaceManager workspaceManager;
	private final transient AgencyService agencyService;
	private final transient I18N i18n;
	private final transient UIEventBus eventBus;
	
	private MButton buttonAddCv = new MButton();
	
	public EditorSearchActionLayout(String titleHeader, String showHeader, WorkspaceManager workspaceManager, I18N i18n,
									AgencyService agencyService, UIEventBus eventBus) {
		super(titleHeader, showHeader, i18n);
		this.workspaceManager = workspaceManager;
		this.i18n = i18n;
		this.agencyService = agencyService;
		this.eventBus = eventBus;

		init();
	}

	private void init() {
		buttonAddCv
			.withFullWidth()
			.addClickListener( this::doCvAdd );
		
		updateMessageStrings( getLocale() );
		
		getInnerContainer()
			.add(
				buttonAddCv
			);
	}

	private void doCvAdd() {
		Window window = new DialogCVSchemeWindow(workspaceManager, i18n, agencyService, VocabularyDTO.createDraft(),
				VersionDTO.createDraft(), null, null, eventBus );
		getUI().addWindow(window);
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		buttonAddCv.withCaption(i18n.get("view.action.button.cvscheme.new"));
	}
}
