package eu.cessda.cvmanager.ui.view.admin.form;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.AgencyService;
import eu.cessda.cvmanager.service.UserAgencyService;
import eu.cessda.cvmanager.service.UserService;
import eu.cessda.cvmanager.service.dto.AgencyDTO;
import eu.cessda.cvmanager.service.dto.UserAgencyDTO;
import eu.cessda.cvmanager.service.dto.UserDTO;
import eu.cessda.cvmanager.ui.view.admin.ManageAgencyView;
import eu.cessda.cvmanager.ui.view.admin.ManageUserAgencyView;

public class UserAgencyForm extends FormLayout {

	private static final long serialVersionUID = -8529392797948718466L;
	
    private final UserAgencyService userAgencyService;
    private final AgencyService agencyService;
    private final UserService userService;
    
    private MVerticalLayout userLayout = new MVerticalLayout();
//    private FormLayout formLayout = new FormLayout();
    
    private UserAgencyDTO userAgencyDTO;
    private ManageUserAgencyView manageAgencyView;
	
	private MTextField name = new MTextField("Name");
	private MTextField filterText = new MTextField();
	
    private ComboBox<AgencyDTO> agency = new ComboBox<>();
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

	private Grid<UserDTO> userGrid = new Grid<>(UserDTO.class);
    private UserDTO selectedUserDto;


    public UserAgencyForm(ManageUserAgencyView manageUserAgencyView, UserAgencyService userAgencyService, UserService userService, AgencyService agencyService) {
        this.manageAgencyView = manageUserAgencyView;
        this.userAgencyService = userAgencyService;
        this.agencyService = agencyService;
        this.userService = userService;
        
        name.withReadOnly( true );
        agency.setCaption("Agency");
        
        filterText.withPlaceholder("Find user")
			.withValueChangeMode(ValueChangeMode.LAZY)
			.addValueChangeListener(e -> updateList());
        
        userGrid.setColumns("id", "firstName", "lastName", "username");
        
        userGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
            	setSelectedUser(event.getValue());
            }
        });
        
        agency.setItems( this.agencyService.findAll());
        agency.setItemCaptionGenerator(new ItemCaptionGenerator<AgencyDTO>() {
			@Override
			public String apply(AgencyDTO item) {
				return item.getId() + "-" + item.getName();
			}
		});
        agency.addValueChangeListener( e -> {
        	if( e.getValue() != null )
        		this.userAgencyDTO.setAgencyId( e.getValue().getId() );
        });

        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        
//        formLayout.setSizeUndefined();
//        formLayout.addComponents(name, agency, buttons);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());
        
        userLayout.add(filterText, userGrid);
        
//        layout.add( filterText, userGrid, formLayout );
        setSizeUndefined();
        addComponents(userLayout, name, agency, buttons);
        
//        setCompositionRoot( layout );
        userGrid.setVisible( false );
    }

    public void setUserAgencyDTO(UserAgencyDTO userAgencyDTO) {
        this.userAgencyDTO = userAgencyDTO;
        AgencyDTO agencyDto = new AgencyDTO();
        agencyDto.setId(userAgencyDTO.getAgencyId());
        agencyDto.setName(userAgencyDTO.getAgencyName());
        
        UserDTO userDto = new UserDTO();
        userDto.setId( userAgencyDTO.getUserId() );
        userDto.setFirstName( userAgencyDTO.getFirstName() );
        userDto.setLastName( userAgencyDTO.getLastName() );
        
        if( agencyDto.isPersisted() )
        	agency.setValue( agencyDto );
        else
        	agency.setValue( null );
        
        setSelectedUser(userDto);

        // Show delete button for only customers already in the database
        delete.setVisible(userAgencyDTO.isPersisted());
        setVisible(true);
        name.selectAll();
    }
    
    private void setSelectedUser( UserDTO userDto) {
    	this.selectedUserDto = userDto;
    	
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
        manageAgencyView.updateList();
        setVisible(false);
    }

    private void save() {
    	userAgencyService.save(userAgencyDTO);
        manageAgencyView.updateList();
        setVisible(false);
    }
    
    private void updateList() {
    	String keyword = filterText.getValue();
    	if( keyword.length() > 1) {
    		manageAgencyView.getGrid().asSingleSelect().clear();
    		userGrid.setVisible( true );
			List<UserDTO> userDTOs = userService.findAll( filterText.getValue());
			userGrid.setItems(userDTOs);
    	} else {
    		userGrid.asSingleSelect().clear();
    		userGrid.setVisible( false );
    	}
	}
    
    public void setUserLayoutVisible(boolean visible) {
    	userLayout.setVisible(visible);
    	name.setValue("");
    	agency.setValue( null );
    }

}
