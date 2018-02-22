package eu.cessda.cvmanager.ui.view.admin.form;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.config.Constants;
import eu.cessda.cvmanager.domain.Role;
import eu.cessda.cvmanager.service.AgencyService;
import eu.cessda.cvmanager.service.RoleService;
import eu.cessda.cvmanager.service.UserAgencyRoleService;
import eu.cessda.cvmanager.service.UserAgencyService;
import eu.cessda.cvmanager.service.UserService;
import eu.cessda.cvmanager.service.dto.AgencyDTO;
import eu.cessda.cvmanager.service.dto.RoleDTO;
import eu.cessda.cvmanager.service.dto.UserAgencyDTO;
import eu.cessda.cvmanager.service.dto.UserAgencyRoleDTO;
import eu.cessda.cvmanager.service.dto.UserDTO;
import eu.cessda.cvmanager.ui.view.admin.ManageUserAgencyRoleView;
import eu.cessda.cvmanager.ui.view.admin.ManageUserAgencyView;

public class UserAgencyRoleForm extends FormLayout {

	private static final long serialVersionUID = -1221682959975551083L;
	private final UserAgencyService userAgencyService;
    private final UserAgencyRoleService userAgencyRoleService;
    private final UserService userService;
    private final RoleService roleService;
    private final Set<String> userRoleTypes = new HashSet<>(Arrays.asList( Constants.USER_ROLE ));
    private final Set<String> userAgencyRoleTypes = new HashSet<>(Arrays.asList( Constants.USER_AGENCY_ROLE ));
            
    private UserAgencyRoleDTO userAgencyRoleDTO;
    private ManageUserAgencyRoleView manageAgencyRoleView;
	
	private MTextField filterTextUser = new MTextField();
	private MTextField filterTextUserAgency = new MTextField();
	
	private MTextField name = new MTextField("Name");
	private MTextField agency = new MTextField("Agency");
	private ComboBox<RoleDTO> roles = new ComboBox<>(); 
	
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

	private Grid<UserDTO> userGrid = new Grid<>(UserDTO.class);
	private Grid<UserAgencyDTO> userAgencyGrid = new Grid<>(UserAgencyDTO.class);
	
	private Set<RoleDTO> roleDTOs = new LinkedHashSet<>();

    public UserAgencyRoleForm(ManageUserAgencyRoleView manageUserAgencyRoleView, UserAgencyService userAgencyService, 
    		UserService userService, UserAgencyRoleService userAgencyRoleService, RoleService roleService) {
        this.manageAgencyRoleView = manageUserAgencyRoleView;
        this.userAgencyService = userAgencyService;
        this.userAgencyRoleService = userAgencyRoleService;
        this.userService = userService;
        this.roleService = roleService;
        
        name.withReadOnly( true );
        agency.withReadOnly( true );
        
        updateSetRole();
        
        filterTextUser
        	.withCaption("Search")
        	.withPlaceholder("Find user")
			.withValueChangeMode(ValueChangeMode.LAZY)
			.addValueChangeListener(e -> updateListUser());
        
        filterTextUserAgency
			.withCaption("Search")
			.withPlaceholder("Find user-agency")
			.withValueChangeMode(ValueChangeMode.LAZY)
			.addValueChangeListener(e -> updateListUserAgency());
        
        userGrid.setHeight("250px");
        userGrid.setColumns("id", "firstName", "lastName", "username");
        
        userGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
            	setSelectedUser(event.getValue());
            }
        });
        
        userAgencyGrid.setHeight("250px");
        userAgencyGrid.setColumns("id", "firstName", "lastName", "agencyName");
        
        userAgencyGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
            	setSelectedUserAgency(event.getValue());
            }
        });
        roles.setCaption("Role");
        roles.setWidth("250px");
        roles.setItemCaptionGenerator(new ItemCaptionGenerator<RoleDTO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String apply(RoleDTO item) {
				return item.getName().substring(5);
			}
		});
        HorizontalLayout buttons = new HorizontalLayout(save, delete);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());
        
        setSizeUndefined();
        addComponents(filterTextUser, userGrid, filterTextUserAgency, userAgencyGrid, name, agency, roles, buttons);
        
        filterTextUser.setVisible( false );
        userAgencyGrid.setVisible( false );
        userGrid.setVisible( false );
    }

	public void setUserAgencyRoleDTO(UserAgencyRoleDTO userAgencyRoleDTO) {
        this.userAgencyRoleDTO = userAgencyRoleDTO;
        
        if( userAgencyRoleDTO.isPersisted() ) {
        
	        RoleDTO roleDTO = null;
	        if( userAgencyRoleDTO.getRoleId() != null ) {
		        roleDTO = new RoleDTO();
		    	roleDTO.setId( userAgencyRoleDTO.getRoleId());
		    	roleDTO.setName(userAgencyRoleDTO.getRole());
	        }
	    	
	        if( userAgencyRoleDTO.getUserId() != null ) {
	        	UserDTO userDto = new UserDTO();
	        	userDto.setId( userAgencyRoleDTO.getUserId() );
	        	userDto.setFirstName( userAgencyRoleDTO.getFirstName() );
	        	userDto.setLastName( userAgencyRoleDTO.getLastName() );
	        	setSelectedUser( userDto );
	        	
	        	setRoleOption(roleDTO, userRoleTypes);
	
	        } else {
	        	UserAgencyDTO userAgencyDTO = new UserAgencyDTO();
	        	userAgencyDTO.setId( userAgencyRoleDTO.getUserAgencyId() );
	        	userAgencyDTO.setFirstName( userAgencyRoleDTO.getUaFirstName() );
	        	userAgencyDTO.setLastName( userAgencyRoleDTO.getUaLastName() );
	        	userAgencyDTO.setAgencyName( userAgencyRoleDTO.getAgency() );
	        	setSelectedUserAgency( userAgencyDTO );
	        	
	        	setRoleOption(roleDTO, userAgencyRoleTypes);
	        }
        }
        // Show delete button for only customers already in the database
        delete.setVisible(userAgencyRoleDTO.isPersisted());
        setVisible(true);
        name.selectAll();
    }
    
    private void setSelectedUser( UserDTO userDto) {    	
    	if( userDto.isPersisted() ){
    		name.setValue( userDto.getFirstName() + " " + userDto.getLastName());
        	this.userAgencyRoleDTO.setUserId(userDto.getId());
        	this.userAgencyRoleDTO.setUserAgencyId(null);
    	}
        else {
        	name.setValue( "" );
        	this.userAgencyRoleDTO.setUserId( null );
        	this.userAgencyRoleDTO.setUserAgencyId(null);
        }
    }
    
    private void setSelectedUserAgency( UserAgencyDTO userAgencyDto) {    	
    	if( userAgencyDto.isPersisted() ){
    		name.setValue( userAgencyDto.getFirstName() + " " + userAgencyDto.getLastName());
    		agency.setValue( userAgencyDto.getAgencyName() );
        	this.userAgencyRoleDTO.setUserAgencyId(userAgencyDto.getId());
        	this.userAgencyRoleDTO.setUserId( null );
    	}
        else {
        	name.setValue( "" );
        	agency.setValue( "" );
        	this.userAgencyRoleDTO.setUserId(null);
        	this.userAgencyRoleDTO.setUserAgencyId( null );
        }
    }

    private void delete() {
    	userAgencyRoleService.delete(userAgencyRoleDTO.getId());
        manageAgencyRoleView.updateList();
        setVisible(false);
    }

    private void save() {
    	userAgencyRoleService.save(userAgencyRoleDTO);
        manageAgencyRoleView.updateList();
        setVisible(false);
    }
    
    private void updateListUser() {
    	String keyword = filterTextUser.getValue();
    	if( keyword.length() > 1) {
    		manageAgencyRoleView.getGrid().asSingleSelect().clear();
    		userGrid.setVisible( true );
			List<UserDTO> userDTOs = userService.findAll( filterTextUser.getValue());
			userGrid.setItems(userDTOs);
    	} else {
    		userGrid.asSingleSelect().clear();
    		userGrid.setVisible( false );
    	}
	}
    
    private void updateListUserAgency() {
    	String keyword = filterTextUserAgency.getValue();
    	if( keyword.length() > 1) {
    		manageAgencyRoleView.getGrid().asSingleSelect().clear();
    		userAgencyGrid.setVisible( true );
			List<UserAgencyDTO> userAgencyDTOs = userAgencyService.findAll( filterTextUserAgency.getValue());
			userAgencyGrid.setItems(userAgencyDTOs);
    	} else {
    		userAgencyGrid.asSingleSelect().clear();
    		userAgencyGrid.setVisible( false );
    	}
	}
    
    public void updateSetRole() {
    	roleDTOs.clear();
    	roleDTOs.addAll( roleService.findAll() );
    }
        
    public void setUserRoleLayoutVisible(boolean visible) {
    	filterTextUser.setValue("");
    	filterTextUser.setVisible(visible);
    	filterTextUserAgency.setValue("");
    	filterTextUserAgency.setVisible( false );
    	agency.setValue("");
    	agency.setVisible( false );
    	name.setValue("");
    	setRoleOption(null, userRoleTypes);
    }

    public void setUserAgencyRoleLayoutVisible(boolean visible) {
    	filterTextUser.setValue("");
    	filterTextUser.setVisible(false);
    	filterTextUserAgency.setValue("");
    	filterTextUserAgency.setVisible( visible );
    	agency.setValue("");
    	agency.setVisible(visible);
    	name.setValue("");
    	setRoleOption(null, userAgencyRoleTypes);
    }
    
	private void setRoleOption(RoleDTO roleDTO, Set<String> roleOptions) {
		// role for user only
		Set<RoleDTO> filteredRoles = new LinkedHashSet<>();
		filteredRoles.addAll( 
			roleDTOs.stream()
				.filter( p -> roleOptions.contains( p.getName()))
				.collect(Collectors.toSet())
				);
		roles.clear();
		roles.setItems( filteredRoles );
		roles.setValue(roleDTO);
        roles.addValueChangeListener( 
    		e -> {
    			if(e.getValue() != null)
    				this.userAgencyRoleDTO.setRoleId( e.getValue().getId());
    			});
	}
}
