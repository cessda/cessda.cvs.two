package eu.cessda.cvmanager.ui.view.admin.form;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.UserService;
import eu.cessda.cvmanager.service.dto.UserDTO;
import eu.cessda.cvmanager.ui.view.admin.ManageUserView;

public class UserForm extends FormLayout {

    private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");
    private TextField username = new TextField("User Name");
    private TextField password = new TextField("Password");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private final UserService userService;
	private final BCryptPasswordEncoder encrypt;
	
    private UserDTO userDTO;
    private ManageUserView manageUserView;
    private Binder<UserDTO> binder = new Binder<>(UserDTO.class);

    public UserForm(ManageUserView manageAgencyView, UserService agencyService, BCryptPasswordEncoder encrypt) {
        this.manageUserView = manageAgencyView;
        this.userService = agencyService;
        this.encrypt = encrypt;

        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        addComponents(firstName, lastName, username, password, buttons);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        binder.bindInstanceFields(this);

        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
        binder.setBean(userDTO);
        
        password.setValue("");
        if( userDTO.isPersisted() )
        	password.setVisible( false );
        else
        	password.setVisible( true );

        // Show delete button for only customers already in the database
        delete.setVisible(userDTO.isPersisted());
        setVisible(true);
        firstName.selectAll();
    }

    private void delete() {
    	userService.delete(userDTO.getId());
        manageUserView.updateList();
        setVisible(false);
    }

    private void save() {
    	if( userDTO.isEnable() == null )
    		userDTO.setEnable( true );
    	if( userDTO.isLocked() == null )
    		userDTO.setLocked( false );
    	if( userDTO.getRandomUsername() == null )
    		userDTO.setRandomUsername( UUID.randomUUID().toString() );
    	if( password.isVisible()) {
    		userDTO.setPassword(encrypt.encode( userDTO.getPassword()));
    	}
    	userService.save(userDTO);
        manageUserView.updateList();
        setVisible(false);
    }

}
