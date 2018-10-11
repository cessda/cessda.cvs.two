package eu.cessda.cvmanager.ui.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.stardat.entity.DDIElement;
import org.gesis.wts.security.SecurityService;
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

import eu.cessda.cvmanager.domain.enumeration.ItemType;
import eu.cessda.cvmanager.domain.enumeration.Status;
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
import eu.cessda.cvmanager.service.impl.ImportServiceImpl;

@UIScope
@SpringView(name = ManageImportView.VIEW_NAME)
public class ManageImportView extends CvManagerAdminView {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "manage-import";
	private Locale locale = UI.getCurrent().getLocale();
	private final VocabularyService vocabularyService;
	private final StardatDDIService stardatDDIService;
	private final CodeService codeService;
	private final VersionService versionService;
	private final ConceptService conceptService;
	private final VocabularyChangeService vocabularyChangeService;
	
	// autowired
	private final ImportServiceImpl importServiceImpl;

	// components
	private MLabel pageTitle = new MLabel();
	private MVerticalLayout layout = new MVerticalLayout();

	public ManageImportView(I18N i18n, EventBus.UIEventBus eventBus, 
			SecurityService securityService, ImportServiceImpl importServiceImpl,
			BCryptPasswordEncoder encrypt, VocabularyService vocabularyService,
			StardatDDIService stardatDDIService, CodeService codeService,
			VersionService versionService, VocabularyChangeService vocabularyChangeService,
			ConceptService conceptService) {
		super(VIEW_NAME, i18n, eventBus, securityService, CvManagerAdminView.ActionType.DEFAULT.toString());
		eventBus.subscribe(this, ManageImportView.VIEW_NAME);
		this.vocabularyService = vocabularyService;
		this.stardatDDIService = stardatDDIService;
		this.codeService = codeService;
		this.conceptService = conceptService;
		this.versionService = versionService;
		this.vocabularyChangeService = vocabularyChangeService;
		
		this.importServiceImpl = importServiceImpl;
	}

	@PostConstruct
	public void init() {
		pageTitle.withContentMode(ContentMode.HTML)
			.withValue("<h1>Manage Import</h1>");
		
		layout.withSpacing(false)
			.withMargin(false)
			.withFullSize();
		
		MButton importStardatDDI = new MButton( "Imports stardatDDI to DB" );
		importStardatDDI.addClickListener( e -> importServiceImpl.importFromStardat());
		
		MButton reIndexButton = new MButton( "Re-index DB" );
		reIndexButton.addClickListener( e -> {
			vocabularyService.findAll().forEach( v -> {
				vocabularyService.index(v);
				vocabularyService.indexPublish(v, null);
			});
		});
		
		MButton dropContent = new MButton( "Drop database content and index" );
		dropContent.addStyleName( ValoTheme.BUTTON_DANGER);
		dropContent.addClickListener( e -> {
			ConfirmDialog.show( this.getUI(), "Confirm",
					"Are you sure you want to permanently drop the entire content?", "yes",
					"cancel",
			
						dialog -> {
							if( dialog.isConfirmed() ) {
								vocabularyService.findAll().forEach( v -> {
									for(VersionDTO version : v.getVersions()) {
										if( version.getItemType().equals(ItemType.SL.toString()) && version.getStatus().equals(Status.PUBLISHED.toString())) {
											List<DDIStore> ddiSchemes = stardatDDIService.findByIdAndElementType(version.getUri(), DDIElement.CVSCHEME);
											CVScheme scheme = new CVScheme(ddiSchemes.get(0));
											stardatDDIService.deleteScheme( scheme );
										}
										for( ConceptDTO concept : version.getConcepts())
											conceptService.delete( concept.getId());
										
										for( VocabularyChangeDTO vc : vocabularyChangeService.findAllByVocabularyVersionId( v.getId(), version.getId()))
											vocabularyChangeService.delete( vc.getId());
										
										versionService.delete( version.getId());
									}
									// remove all codes
									for( CodeDTO code : codeService.findByVocabulary( v.getId()))
										codeService.delete(code);
									
									// remove all vocabulary in DB
									vocabularyService.delete( v.getId());
								});
							}
						}

					);
		});

        layout.addComponents(pageTitle, 
        		importStardatDDI , 
        		new MLabel().withFullWidth(),
        		reIndexButton,
        		new MLabel().withFullWidth(),
        		new MLabel().withFullWidth(),
        		dropContent);
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
