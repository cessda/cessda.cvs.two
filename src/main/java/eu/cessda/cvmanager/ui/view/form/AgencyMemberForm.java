package eu.cessda.cvmanager.ui.view.form;

import java.util.List;

import org.gesis.wts.domain.enumeration.AgencyRole;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.service.dto.UserAgencyDTO;
import org.gesis.wts.service.dto.UserDTO;
import org.gesis.wts.ui.view.admin.ManageUserAgencyView;
import org.vaadin.viritin.fields.MTextField;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageMember;

public class AgencyMemberForm extends FormLayout {

	private static final long serialVersionUID = -1221682959975551083L;
	private final UserAgencyService userAgencyService;
    private final AgencyService agencyService;
    private final UserService userService;
            
    private UserAgencyDTO userAgencyDTO;
    private DialogAgencyManageMember dialogAgencyManageMember;
	
	private MTextField name = new MTextField("Name");
	private MTextField filterText = new MTextField();
	
	private NativeSelect<AgencyRole> roleOption = new NativeSelect<>( "Role" );
    private NativeSelect<Language> languageOption = new NativeSelect<>( "Language" );
	
    private Button save = new Button("Save");

	private Grid<UserDTO> userGrid = new Grid<>(UserDTO.class);


    public AgencyMemberForm(DialogAgencyManageMember dialogAgencyManageMember, UserAgencyService userAgencyService, UserService userService, AgencyService agencyService) {
        this.dialogAgencyManageMember = dialogAgencyManageMember;
        this.userAgencyService = userAgencyService;
        this.agencyService = agencyService;
        this.userService = userService;
        
        name.withReadOnly( true );
        
        filterText
        	.withCaption("Search")
        	.withPlaceholder("Find user")
			.withValueChangeMode(ValueChangeMode.LAZY)
			.addValueChangeListener(e -> updateList());
        
        roleOption.setItems( AgencyRole.values());
        languageOption.setItems(Language.values());
        languageOption.setVisible( false );
        languageOption.setItemCaptionGenerator( new ItemCaptionGenerator<Language>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String apply(Language item) {
				return item.name() + " (" +item.getLanguage() + ")";
			}
		});
        
        userGrid.setHeight("250px");
        userGrid.setWidth("200px");
        userGrid.setColumns("username", "firstName", "lastName");
        
        userGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
            	setSelectedUser(event.getValue());
            }
        });
        
        roleOption.addValueChangeListener( e -> {
        	if( e.getValue() != null ) {
        		if( e.getValue().equals(AgencyRole.ADMIN) )
        			languageOption.setVisible( false );
        		else
        			languageOption.setVisible( true );
    			this.userAgencyDTO.setAgencyRole( e.getValue() );
        	} else {
        		languageOption.setVisible( false );
        		this.userAgencyDTO.setAgencyRole( null );
        	}
        });
        
        languageOption.addValueChangeListener( e -> {
        	if( e.getValue() != null ) {
        		this.userAgencyDTO.setLanguage( e.getValue() );
        	} else
        		this.userAgencyDTO.setLanguage( null );
        });
        
        HorizontalLayout buttons = new HorizontalLayout(save);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        save.addClickListener(e -> this.save());
        
        setSizeUndefined();
        addStyleName("show-caption");
        addComponents(filterText, userGrid, name, roleOption, languageOption, buttons);
        
        filterText.setVisible( false );
        userGrid.setVisible( false );
    }

    public void setUserAgencyDTO(UserAgencyDTO agencyMember) {
        this.userAgencyDTO = agencyMember;
        AgencyDTO agencyDto = new AgencyDTO();
        agencyDto.setId(userAgencyDTO.getAgencyId());
        agencyDto.setName(userAgencyDTO.getAgencyName());
        
        UserDTO userDto = new UserDTO();
        userDto.setId( userAgencyDTO.getUserId() );
        userDto.setFirstName( userAgencyDTO.getFirstName() );
        userDto.setLastName( userAgencyDTO.getLastName() );
        
        setSelectedUser(userDto);

        setVisible(true);
    }
    
    private void setSelectedUser( UserDTO userDto) {    	
    	if( userDto.isPersisted() ){
    		name.setValue( userDto.getFirstName() + " " + userDto.getLastName());
        	this.userAgencyDTO.setUserId(userDto.getId());
    	}
        else {
        	name.setValue( "" );
        	this.userAgencyDTO.setUserId( null );
        }
    }

    private void delete() {
    	userAgencyService.delete(userAgencyDTO.getId());
        dialogAgencyManageMember.updateList();
        setVisible(false);
    }

    private void save() {
    	if( userAgencyDTO.getUserId() == null ) {
    		Notification.show("Please select one of the user on the table above");
    	} else {
    		if( userAgencyDTO.getAgencyRole() == null ) {
        		Notification.show("Please select role!");
        		return;
        	}
        	if( languageOption.isVisible() && userAgencyDTO.getLanguage() == null ) {
        		Notification.show("Please select language!");
        		return;
        	}
    		
	    	userAgencyService.save(userAgencyDTO);
	    	
	    	userAgencyDTO.setAgencyRole( null );
	    	userAgencyDTO.setLanguage( null );
	    	roleOption.setValue( null );
	    	languageOption.setValue( null );
	        dialogAgencyManageMember.updateList();
	        setVisible(false);
    	}
    }
    
    private void updateList() {
    	String keyword = filterText.getValue();
    	if( keyword.length() > 1) {
    		userGrid.setVisible( true );
			List<UserDTO> userDTOs = userService.findAll( filterText.getValue());
			userGrid.setItems(userDTOs);
    	} else {
    		userGrid.setVisible( false );
    	}
	}
    
    public void setUserLayoutVisible(boolean visible) {
    	filterText.setVisible(visible);
    	filterText.setValue("");
    	name.setValue("");
    }

}
