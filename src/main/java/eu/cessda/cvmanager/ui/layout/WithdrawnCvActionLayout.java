package eu.cessda.cvmanager.ui.layout;

import java.util.Locale;

import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;

import eu.cessda.cvmanager.service.VocabularyService;
import eu.cessda.cvmanager.service.dto.VocabularyDTO;
import eu.cessda.cvmanager.ui.component.ResponsiveBlock;
import eu.cessda.cvmanager.ui.view.EditorSearchView;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;

public class WithdrawnCvActionLayout extends ResponsiveBlock{
	private static final long serialVersionUID = 2436346372920594014L;
	
	private final VocabularyService vocabularyService;
	private VocabularyDTO vocabulary;
		
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	
	private MButton buttonRestore = new MButton();
	private MLabel separatorLabel = new MLabel("<hr style=\"margin:5px\"/>").withContentMode( ContentMode.HTML );
	private MButton buttonCompleteDelete = new MButton();

	public WithdrawnCvActionLayout(String titleHeader, String showHeader, I18N i18n, VocabularyService vocabularyService) {
		super(titleHeader, showHeader, i18n);
		this.i18n = i18n;
		this.vocabularyService = vocabularyService;
		init();
	}

	private void init() {
		buttonRestore
			.withFullWidth()
			.withStyleName("action-button")
			.withVisible( false )
			.addClickListener( this::doCvAdd );
		
		buttonCompleteDelete
			.withFullWidth()
			.withStyleName("action-button", ValoTheme.BUTTON_DANGER)
			.withVisible( false )
			.addClickListener( this::doCvEdit );
		
		separatorLabel.withFullWidth().setVisible( false );
				
		getInnerContainer()
			.add(
				buttonRestore,
				separatorLabel,
				buttonCompleteDelete
			);
	}

	private void doCvAdd( ClickEvent event ) {
		ConfirmDialog.show( this.getUI(), "Confirm",
			"Are you sure you want to restore the CV \"" + vocabulary.getNotation() + "\" ?", "yes",
			"cancel",
					
				dialog -> {
					if( dialog.isConfirmed() ) {
						vocabularyService.restore(vocabulary);
						UI.getCurrent().getNavigator().navigateTo( EditorSearchView.VIEW_NAME );
					}
				}

			);
	}
	
	private void doCvEdit( ClickEvent event ) {
		ConfirmDialog.show( this.getUI(), "Confirm",
			"Are you sure you want to permanently delete the CV \"" + vocabulary.getNotation() + "\" ?", "yes",
			"cancel",
					
				dialog -> {
					if( dialog.isConfirmed() ) {
						vocabularyService.completeDelete(vocabulary);
						UI.getCurrent().getNavigator().navigateTo( EditorSearchView.VIEW_NAME );
					}
				}
			);
	}
	
	
	
	@Override
	public void updateMessageStrings(Locale locale) {
		buttonRestore.withCaption( "Restore" );
		buttonCompleteDelete.withCaption( "Permanently Delete" );

	}

	public VocabularyDTO getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(VocabularyDTO vocabulary) {
		this.vocabulary = vocabulary;
	}

	public MButton getButtonAddCv() {
		return buttonRestore;
	}

	public MButton getButtonEditCv() {
		return buttonCompleteDelete;
	}

	public boolean hasActionRight() {
		
		boolean hasAction = false;
		if( !CvManagerSecurityUtils.isAuthenticated() ) {
			setVisible( false );
		}
		else {
			buttonRestore.setVisible( false );
			buttonCompleteDelete.setVisible( false );
			separatorLabel.setVisible( false );

			updateMessageStrings(locale);
			
			setVisible( true );
			hasAction = true;
			// check add CV button
			if( CvManagerSecurityUtils.isCurrentUserSystemAdmin()) {
				buttonRestore.setVisible( true );
				separatorLabel.setVisible( true );
				buttonCompleteDelete.setVisible( true );
			}
		}
		return hasAction;
	}
	
	
}
