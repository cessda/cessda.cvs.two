package eu.cessda.cvmanager.ui.view.form;

import java.util.List;

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
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.model.AgencyMemberItem;
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
        
        userGrid.setHeight("250px");
        userGrid.setWidth("200px");
        userGrid.setColumns("username", "firstName", "lastName");
        
        userGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
            	setSelectedUser(event.getValue());
            }
        });
        
        HorizontalLayout buttons = new HorizontalLayout(save);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        save.addClickListener(e -> this.save());
        
        setSizeUndefined();
        addComponents(filterText, userGrid, name, buttons);
        
        filterText.setVisible( false );
        userGrid.setVisible( false );
    }

    public void setAgencyMemberItem(AgencyMemberItem agencyMemberItem) {
        this.userAgencyDTO = agencyMemberItem.getUserAgency();
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
	    	userAgencyService.save(userAgencyDTO);
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
