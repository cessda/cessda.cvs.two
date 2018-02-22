package eu.cessda.cvmanager.ui.view.admin.form;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.domain.enumeration.Language;
import eu.cessda.cvmanager.domain.enumeration.LanguageType;
import eu.cessda.cvmanager.service.LanguageRightService;
import eu.cessda.cvmanager.service.UserAgencyService;
import eu.cessda.cvmanager.service.dto.LanguageRightDTO;
import eu.cessda.cvmanager.service.dto.UserAgencyDTO;
import eu.cessda.cvmanager.ui.view.admin.ManageUserAgencyLanguageView;

public class UserAgencyLanguageForm extends FormLayout {

	private static final long serialVersionUID = -8529392797948718466L;
	
    private final LanguageRightService languageRightService;
    private final UserAgencyService userAgencyService;
        
    private LanguageRightDTO languageRightDTO;
    private ManageUserAgencyLanguageView manageUserAgencyLanguageView;
	
	private MTextField name = new MTextField("Name");
	private MTextField filterText = new MTextField();
	
    private NativeSelect<LanguageType> languageTypeOption = new NativeSelect<>();
    private NativeSelect<Language> languageOption = new NativeSelect<>();
    
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

	private Grid<UserAgencyDTO> userAgencyGrid = new Grid<>(UserAgencyDTO.class);

    public UserAgencyLanguageForm(ManageUserAgencyLanguageView manageUserAgencyView, UserAgencyService userAgencyService, LanguageRightService languageRightService) {
        this.manageUserAgencyLanguageView = manageUserAgencyView;
        this.languageRightService = languageRightService;
        this.userAgencyService = userAgencyService;
        
        name.withReadOnly( true );
        languageTypeOption.setCaption("Language Type");
        languageTypeOption.setItems( LanguageType.values());
        
        languageOption.setCaption("Language Option");
        languageOption.setItems(Language.values());
        
        filterText
        	.withCaption("Search")
        	.withPlaceholder("Find user - agency")
			.withValueChangeMode(ValueChangeMode.LAZY)
			.addValueChangeListener(e -> updateList());
        
        userAgencyGrid.setHeight("250px");
        userAgencyGrid.setColumns("id", "firstName", "lastName", "agencyName");
        
        userAgencyGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
            	setSelectedUserAgency(event.getValue());
            }
        });
        
        languageTypeOption.addValueChangeListener( e -> {
        	if( e.getValue() != null )
        		this.languageRightDTO.setLanguageType(e.getValue());
        });
        
        languageOption.addValueChangeListener( e -> {
        	if( e.getValue() != null )
        		this.languageRightDTO.setLanguage(e.getValue());
        });

        HorizontalLayout buttons = new HorizontalLayout(save, delete);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());
        
        
        setSizeUndefined();
        addComponents(filterText, userAgencyGrid, name, languageTypeOption, languageOption, buttons);
        
        filterText.setVisible( false );
        userAgencyGrid.setVisible( false );
    }

    public void setLanguageRightDTO(LanguageRightDTO LanguageRightDTO) {
        this.languageRightDTO = LanguageRightDTO;
        
        if( this.languageRightDTO.isPersisted() ) {
        	languageTypeOption.setValue( this.languageRightDTO.getLanguageType() );
        	languageOption.setValue( this.languageRightDTO.getLanguage());
        	
        	UserAgencyDTO userAgencyDto = new UserAgencyDTO();
        	userAgencyDto.setId( this.languageRightDTO.getUserAgencyId());
        	userAgencyDto.setAgencyName( this.languageRightDTO.getAgencyName() );
        	userAgencyDto.setFirstName( this.languageRightDTO.getFirstName());
        	userAgencyDto.setLastName( this.languageRightDTO.getLastName());
            setSelectedUserAgency(userAgencyDto);

        }else {
        	languageTypeOption.setValue( null );
        	languageOption.setValue( null );
        }
        

        // Show delete button for only customers already in the database
        delete.setVisible(LanguageRightDTO.isPersisted());
        setVisible(true);
        name.selectAll();
    }
    
    private void setSelectedUserAgency( UserAgencyDTO userAgencyDto) {
    	
    	if( userAgencyDto.isPersisted() ){
    		name.setValue( userAgencyDto.getFirstName() + " " + userAgencyDto.getLastName() + " - " + userAgencyDto.getAgencyName());
        	this.languageRightDTO.setUserAgencyId(userAgencyDto.getId());
    	}
        else {
        	name.setValue( "" );
        	this.languageRightDTO.setUserAgencyId( null );
        }
    }

    private void delete() {
    	languageRightService.delete(languageRightDTO.getId());
        manageUserAgencyLanguageView.updateList();
        setVisible(false);
    }

    private void save() {
    	languageRightService.save(languageRightDTO);
        manageUserAgencyLanguageView.updateList();
        setVisible(false);
    }
    
    private void updateList() {
    	String keyword = filterText.getValue();
    	if( keyword.length() > 1) {
    		manageUserAgencyLanguageView.getGrid().asSingleSelect().clear();
    		userAgencyGrid.setVisible( true );
			List<UserAgencyDTO> userAgencyDTOs = userAgencyService.findAll( filterText.getValue());
			userAgencyGrid.setItems(userAgencyDTOs);
    	} else {
    		userAgencyGrid.asSingleSelect().clear();
    		userAgencyGrid.setVisible( false );
    	}
	}
    
    public void setUserLayoutVisible(boolean visible) {
    	filterText.setVisible(visible);
    	filterText.setValue("");
    	name.setValue("");
    	languageTypeOption.setValue( null );
    	languageOption.setValue( null );
    }

}
