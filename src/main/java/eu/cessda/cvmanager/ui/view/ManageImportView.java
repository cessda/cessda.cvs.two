package eu.cessda.cvmanager.ui.view;

import java.time.LocalDateTime;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.gesis.wts.security.SecurityService;
import org.gesis.wts.security.SecurityUtils;
import org.gesis.wts.security.UserDetails;
import org.gesis.wts.ui.view.admin.CvManagerAdminView;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ImportService;
import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyChangeDTO;

@UIScope
@SpringView(name = ManageImportView.VIEW_NAME)
public class ManageImportView extends CvManagerAdminView {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "manage-import";
	private Locale locale = UI.getCurrent().getLocale();
	private final VocabularyService vocabularyService;
	
	// autowired
	private final ImportService importService;

	// components
	private MLabel pageTitle = new MLabel();
	private MVerticalLayout layout = new MVerticalLayout();

	public ManageImportView(I18N i18n, EventBus.UIEventBus eventBus, 
			SecurityService securityService, ImportService importService,
			BCryptPasswordEncoder encrypt, VocabularyService vocabularyService) {
		super(VIEW_NAME, i18n, eventBus, securityService, CvManagerAdminView.ActionType.DEFAULT.toString());
		eventBus.subscribe(this, ManageImportView.VIEW_NAME);
		this.vocabularyService = vocabularyService;
		
		this.importService = importService;
	}

	@PostConstruct
	public void init() {
		pageTitle.withContentMode(ContentMode.HTML)
			.withValue("<h1>Manage Import</h1>");
		
		layout.withSpacing(false)
			.withMargin(false)
			.withFullSize();
		
		MButton importStardatDDI = new MButton( "Imports stardatDDI to DB" );
		importStardatDDI.addClickListener( e -> importService.importFromStardat());
		
		MButton dropContent = new MButton( "Drop database content and index" );
		dropContent.addStyleName( ValoTheme.BUTTON_DANGER);
		dropContent.addClickListener( e -> {
			ConfirmDialog.show( this.getUI(), "Confirm",
					"Are you sure you want to permanently drop the entire content?", "yes",
					"cancel",
			
						dialog -> {
							if( dialog.isConfirmed() ) {
								vocabularyService.findAll().forEach( v -> {
									vocabularyService.delete( v.getId());
								});
							}
						}

					);
		});

        layout.addComponents(pageTitle, importStardatDDI , dropContent);
        rightContainer.add(layout).withExpand(layout,1);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		authorizeViewAccess();
		
		locale = UI.getCurrent().getLocale();
		updateMessageStrings(locale);
	}

	@Override
	public void afterViewChange(ViewChangeEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		actionAdminPanel.updateMessageStrings(locale);
	}


}
