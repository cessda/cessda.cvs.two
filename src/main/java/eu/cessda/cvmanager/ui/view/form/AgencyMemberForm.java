package eu.cessda.cvmanager.ui.view.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.gesis.wts.domain.enumeration.AgencyRole;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.service.dto.UserAgencyDTO;
import org.gesis.wts.service.dto.UserDTO;
import org.gesis.wts.ui.view.admin.ManageUserAgencyView;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageMember;
import eu.cessda.cvmanager.utils.CvManagerSecurityUtils;

public class AgencyMemberForm extends FormLayout {

	private static final long serialVersionUID = -1221682959975551083L;
	private final UserAgencyService userAgencyService;
    private final AgencyService agencyService;
    private final UserService userService;
    private final BCryptPasswordEncoder encrypt;
            
    private UserAgencyDTO userAgencyDTO;
    private DialogAgencyManageMember dialogAgencyManageMember;
	
    private MLabel infoLabel = new MLabel().withContentMode( ContentMode.HTML );
    
	private MTextField name = new MTextField("Name");
	private MTextField filterText = new MTextField();
	
	private NativeSelect<AgencyRole> roleOption = new NativeSelect<>( "Role" );
    private NativeSelect<Language> languageOption = new NativeSelect<>( "Language" );
	
    private Button save = new Button("Save");
    private MButton cancelBtn = new MButton("Cancel");
    private MButton addUserBtn = new MButton( "Add new user" );
    
    private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");
    private TextField username = new TextField("User Name");
    private TextField password = new TextField("Password");
    private boolean isNewUser;

	private Grid<UserDTO> userGrid = new Grid<>(UserDTO.class);
	private UserDTO userDTO;
	private Binder<UserDTO> binder = new Binder<>(UserDTO.class);


    public AgencyMemberForm(DialogAgencyManageMember dialogAgencyManageMember, UserAgencyService userAgencyService, UserService userService, 
    		AgencyService agencyService, BCryptPasswordEncoder encrypt, I18N i18n, Locale locale) {
        this.dialogAgencyManageMember = dialogAgencyManageMember;
        this.userAgencyService = userAgencyService;
        this.agencyService = agencyService;
        this.userService = userService;
        this.encrypt = encrypt;
        
        infoLabel.setValue("<h2>Add new agency member</h2> You can search existing user, and then set user' specific role.");
        
        name.withReadOnly( true );
        name.setPlaceholder("select search result");
        
        
        filterText
        	.withCaption("Search")
        	.withPlaceholder("Find user")
			.withValueChangeMode(ValueChangeMode.LAZY)
			.addValueChangeListener(e -> updateList());
        
        List<AgencyRole> agencyRoles = new ArrayList<AgencyRole>(Arrays.asList( AgencyRole.values()));
        // remove Admin and View role
        agencyRoles.remove( AgencyRole.ADMIN );
        agencyRoles.remove( AgencyRole.VIEW );
        
        roleOption.setItems( agencyRoles );
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
        
        HorizontalLayout buttons = new HorizontalLayout(save, cancelBtn);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        save.addClickListener(e -> this.save());
        
        cancelBtn.addClickListener( e -> {
        	this.setVisible( false );
        	dialogAgencyManageMember.getAddBtn().setVisible( true );
        });
        
        addUserBtn.addClickListener( e -> {
            // initialize new user
        	isNewUser = true;
        	
            userDTO = new UserDTO();
            binder.bindInstanceFields(this);
            binder.setBean(userDTO);
            
        	password.setValue( CvManagerSecurityUtils.generateSecureRandomPassword(10));
            
        	activateAddUserForm( true );
        });
        
        setSizeUndefined();
        setMargin( false );
        setWidth("300px");
        addStyleName("show-caption");
        addComponents(
        		infoLabel, 
//        		addUserBtn, 
        		filterText, 
        		userGrid, 
        		name, 
        		firstName, lastName, username, password,
        		roleOption, languageOption, buttons);
        
        addUserBtn.setVisible( false );
        filterText.setVisible( false );
        userGrid.setVisible( false );
        
        firstName.setVisible( false );
        lastName.setVisible( false );
        username.setVisible( false );
        password.setVisible( false );
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
    	if(isNewUser) {
    		if(!isNewUserInputValid())
        		return;
    		userDTO.setEnable( true );
    		userDTO.setLocked( false );
    		userDTO.setRandomUsername( UUID.randomUUID().toString() );
    		userDTO.setPassword(encrypt.encode( userDTO.getPassword()));
    		userDTO = userService.save(userDTO);
    		userAgencyDTO.setUserId( userDTO.getId() );
    	}
    	if( userAgencyDTO.getUserId() == null ) {
    		Notification.show("Please select one of the user on the user search result table");
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
	        dialogAgencyManageMember.getAddBtn().setVisible( true );
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
    	if( visible )
    		isNewUser = false;
    	addUserBtn.setVisible(visible);
    	filterText.setVisible(visible);
    	filterText.setValue("");
    	name.setValue("");
    	roleOption.setValue( null );
    }
    
    public void activateAddUserForm( boolean visible ) {
    	setUserLayoutVisible(!visible);
    	name.setVisible(!visible);
    	
    	// will be visible if true
    	firstName.setVisible( visible );
        lastName.setVisible( visible );
        username.setVisible( visible );
        password.setVisible( visible );
    }
    
    private boolean isNewUserInputValid() {
    	userDTO.setLastName( lastName.getValue() );
    	userDTO.setUsername( username.getValue() );
    	userDTO.setPassword( password.getValue() );
    	
    	binder
    		.forField( lastName )
    		.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.bind( u -> u.getLastName(),
				(u, value) -> u.setLastName(value));
    	
    	binder
			.forField( username )
			.withValidator( u -> !userService.existByUsername( u ), "username is already exist" )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 2 characters", 2, 250 ))	
			.bind( u -> u.getUsername(),
				(u, value) -> u.setUsername(value));
    	
    	binder
			.forField( password )
			.withValidator( new StringLengthValidator( "* required field, require an input with at least 6 characters", 6, 250 ))	
			.bind( u -> u.getPassword(),
				(u, value) -> u.setPassword(value));
    	
    	binder.validate();
		return binder.isValid();
    }

}
