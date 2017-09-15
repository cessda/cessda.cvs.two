package eu.cessda.cvmanager.ui.view;

import java.util.Locale;

import org.gesis.security.SecurityService;
import org.gesis.security.db.DBservices;
import org.gesis.security.db.User;
import org.springframework.stereotype.Component;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.cessda.cvmanager.service.PasswordValidator;
import eu.cessda.cvmanager.service.db.UserData;
import eu.cessda.cvmanager.service.db.UserDataRepository;
import eu.cessda.cvmanager.ui.CVManagerUI;
import eu.cessda.cvmanager.ui.view.entities.UserDataEntity;

@SpringView(name = UserProfileView.VIEW_NAME)
@Component(value = UserProfileView.VIEW_NAME)
public class UserProfileView extends VerticalLayout implements View {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3758309295161858792L;

	public static final String VIEW_NAME = "Profile";
	private EventBus.UIEventBus eventBus;
	private DBservices db;
	private UserDataRepository userDataRepository;
	private SecurityService securityService;

	private Locale local = UI.getCurrent().getLocale();

	private boolean passwordMatch;
	private boolean usernameExist;
	private String username;

	private AdvertiserPanelLayout advLayout;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout profilePanel = new HorizontalLayout();
	private CssLayout profileLayout = new CssLayout();

	private Label headerTitle = new Label();
	private Label lEmail = new Label();
	private Label lFirstname = new Label();
	private Label lLastname = new Label();
	private Label lAffiliation = new Label();
	private Label lPersistentIdentifier = new Label();
	private Label lChangePasswordInfo = new Label();
	private Label lPassword = new Label();
	private Label lRePassword = new Label();

	private TextField email = new TextField();
	private TextField firstname = new TextField();
	private TextField lastname = new TextField();
	private TextField affiliation = new TextField();
	private TextField persistentIdentifier = new TextField();
	private PasswordField password = new PasswordField();
	private PasswordField rePassword = new PasswordField();

	private Button updateProfileButton = new Button();
	private Button updatePasswordButton = new Button();

	private User user;
	private UserData userData;

	private UserDataEntity userDataEntity = new UserDataEntity();
	private Binder<UserDataEntity> binder = new Binder<UserDataEntity>();

	private FocusListener focusListener = e -> eventBus.publish(this, new Label(getUi().getMessageByLocaleService()
			.getMessage("form.registration." + e.getComponent().getCaption() + ".help")));

	// autowired
	public UserProfileView(SecurityService ss, UIEventBus eventBus, DBservices db, UserDataRepository udr,
			UserDataRepository userDataRepository) {
		this.securityService = ss;
		this.eventBus = eventBus;
		this.db = db;
		this.userDataRepository = udr;
		this.userDataRepository = userDataRepository;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		eventBus.subscribe(this);

		username = securityService.getLoggedInUsername();
		user = db.getUserByUsername(username);
		userData = userDataRepository.findByUsername(username);

		if (userData == null) {
			userData = new UserData();
			userData.setFirstname(user.getFirstName());
			userData.setLastname(user.getLastName());
		} else {
			if (userData.getFirstname() == null)
				userData.setFirstname(user.getFirstName());
			if (userData.getLastname() == null)
				userData.setLastname(user.getLastName());
		}

		headerTitle.setStyleName("headertitle");

		advLayout = new AdvertiserPanelLayout(eventBus);
		advLayout.setSpacing(true);

		// lEmail.withStyleName("required").withContentMode(ContentMode.HTML)
		// .setValue(getUi().getMessageByLocaleService().getMessage("form.registration.email"));
		lEmail.setStyleName("required");
		lEmail.setContentMode(ContentMode.HTML);
		lEmail.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.email"));

		binder.forField(email)
				.withValidator(
						new EmailValidator(getUi().getMessageByLocaleService().getMessage("field.email.validator")))
				.asRequired(getUi().getMessageByLocaleService().getMessage("field.email.validator"))
				.bind(UserDataEntity::getEmail, UserDataEntity::setEmail);

		// email.withValidator(new
		// EmailValidator(getUi().getMessageByLocaleService().getMessage("field.email.validator")))
		// .withRequired(true)
		// .withRequiredError(getUi().getMessageByLocaleService().getMessage("field.email.validator"))
		// .withFullWidth().setValidationVisible(true);
		email.setCaption("email");
		email.addFocusListener(focusListener);

		email.addValueChangeListener(e -> {
			if (!email.getValue().equals(securityService.getLoggedInUsername())) {
				if (db.getUserByUsername(email.getValue()) != null) {
					Notification.show("Email is already registered");
					email.addStyleName("error");
					usernameExist = true;
					lEmail.setValue(
							getUi().getMessageByLocaleService().getMessage("form.registration.email.existerror"));
				} else {
					lEmail.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.email"));
					email.removeStyleName("error");
					usernameExist = false;
				}
			} else {
				lEmail.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.email"));
				email.removeStyleName("error");
				usernameExist = false;
			}
		});
		if (user.getUsername() != null)
			email.setValue(user.getUsername());

		// lFirstname.withStyleName("required")
		// .setValue(getUi().getMessageByLocaleService().getMessage("form.registration.firstname"));
		lFirstname.setStyleName("required");
		lFirstname.setContentMode(ContentMode.HTML);
		lFirstname.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.firstname"));

		firstname.setCaption("firstname");

		firstname.addFocusListener(focusListener);
		firstname.setValue(userData.getFirstname());
		binder.forField(firstname)
				.withValidator(new StringLengthValidator("Please enter your firstname (2 letters min)", 2, 250))
				.bind(UserDataEntity::getFirstName, UserDataEntity::setFirstName);
		// lLastname.withStyleName("required")
		// .setValue(getUi().getMessageByLocaleService().getMessage("form.registration.lastname"));
		lLastname.setStyleName("required");
		lLastname.setContentMode(ContentMode.HTML);
		lLastname.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.lastname"));

		lastname.setWidth("100%");

		binder.forField(lastname)
				.withValidator(new StringLengthValidator("Please enter your firstname (2 letters min)", 2, 250))
				.bind(UserDataEntity::getLastName, UserDataEntity::setLastName);
		lastname.setCaption("lastname");

		lastname.addFocusListener(focusListener);
		lastname.setValue(userData.getLastname());

		lAffiliation.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.institute"));

		// affiliation = new AutocompleteTextField();
		// affiliation.withSizeFull().withCaption("inistitute").withSuggestionProvider(suggestionProvider).withMinChars(3)
		// .withSuggestionLimit(20).withFocusListener(focusListener);
		// if (userData.getAffiliation() != null)
		// affiliation.setValue(userData.getAffiliation());

		lPersistentIdentifier.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.identifier"));

		// persistentIdentifier.withFullWidth().setCaption("identifier");
		persistentIdentifier.setSizeFull();
		persistentIdentifier.setCaption("identifier");

		persistentIdentifier.addFocusListener(focusListener);
		if (userData.getIdentifier() != null)
			persistentIdentifier.setValue(userData.getIdentifier());

		// lChangePasswordInfo.withContentMode(ContentMode.HTML)
		// .setValue(getUi().getMessageByLocaleService().getMessage("form.registration.changepassword"));

		lChangePasswordInfo.setContentMode(ContentMode.HTML);
		lChangePasswordInfo
				.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.changepassword"));

		lPassword.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.newpassword"));

		password.setSizeFull();
		binder.forField(password).withValidator(new PasswordValidator(
				"The password provided is not valid. Please enter min. 8 characters with at least one digit."));

		// password.setValidationVisible(false);
		password.setCaption("pass");
		password.addFocusListener(focusListener);
		password.addValueChangeListener(e -> {
			// password.setValidationVisible(true);
			rePassword.setStyleName("error");
			lRePassword
					.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.renewpassword.error"));
			// if (password.isValid())
			// password.setValidationVisible(false);
		});

		// lRePassword.withContentMode(ContentMode.HTML)
		// .setValue(getUi().getMessageByLocaleService().getMessage("form.registration.renewpassword"));

		lRePassword.setContentMode(ContentMode.HTML);
		lRePassword.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.renewpassword"));

		rePassword.setCaption("repass");
		rePassword.addFocusListener(focusListener);
		rePassword.addValueChangeListener(e -> {

			if (password.isEmpty())
				return;

			if (!rePassword.getValue().equals(password.getValue())) {
				rePassword.setStyleName("error");
				lRePassword.setValue(
						getUi().getMessageByLocaleService().getMessage("form.registration.renewpassword.error"));
				passwordMatch = false;
			} else {
				rePassword.removeStyleName("error");
				lRePassword.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.renewpassword"));
				passwordMatch = true;
			}

		});

		updateProfileButton.setStyleName("uploadButton margintop15px");
		updateProfileButton.setCaption("Update profile");
		updateProfileButton.addClickListener(this::updateProfile);

		updatePasswordButton.setStyleName("uploadButton margintop15px");
		updatePasswordButton.setCaption("Update password");
		updatePasswordButton.addClickListener(this::updatePassword);

		profileLayout.setStyleName("advline uploadform");
		profileLayout.setSizeFull();
		profileLayout.addComponents(lEmail, email, lFirstname, firstname, lLastname, lastname, lAffiliation,
				affiliation, lPersistentIdentifier, persistentIdentifier, updateProfileButton, lChangePasswordInfo,
				lPassword, password, lRePassword, rePassword, updatePasswordButton);
		profileLayout.addLayoutClickListener(e -> {
			if (e.getClickedComponent() instanceof com.vaadin.ui.TextField
					|| e.getClickedComponent() instanceof com.vaadin.ui.ComboBox
					|| e.getClickedComponent() instanceof com.vaadin.ui.DateField
					|| e.getClickedComponent() instanceof com.vaadin.ui.TextArea)
				this.eventBus.publish(this, e.getRelativeY());
			else
				this.eventBus.publish(this, new Label(""));
		});

		profilePanel.setWidth(100, Unit.PERCENTAGE);
		profilePanel.addComponents(profileLayout, advLayout);
		profilePanel.setExpandRatio(profileLayout, 0.69f);
		profilePanel.setExpandRatio(advLayout, 0.3f);

		mainLayout.setWidth("60%");
		mainLayout.setStyleName("mainlayout");
		mainLayout.setMargin(new MarginInfo(false, false, false, false));
		mainLayout.setSpacing(true);
		mainLayout.addComponents(headerTitle, profilePanel);

		this.setSizeFull();
		addComponent(mainLayout);
		setComponentAlignment(mainLayout, Alignment.MIDDLE_CENTER);
	}

	public void updateProfile(ClickEvent event) {
		// if (!email.isValid() || !firstname.isValid() || !lastname.isValid())
		// {
		// Notification.show("There is invalid input");
		// return;
		// }

		if (usernameExist) {
			Notification.show("Unable to update email, email is already registered");
			return;
		}

		if (!email.getValue().equals(securityService.getLoggedInUsername())) {
			try {
				db.changeUsername(securityService.getLoggedInUsername(), email.getValue());
				username = email.getValue();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		userData.setUsername(email.getValue());
		userData.setFirstname(firstname.getValue());
		userData.setLastname(lastname.getValue());
		userData.setAffiliation(affiliation.getValue());
		userData.setIdentifier(persistentIdentifier.getValue());

		userDataRepository.save(userData);
		Notification.show("Profile updated");
	}

	public void updatePassword(ClickEvent event) {
		if (passwordMatch) {
			try {
				db.changePassword(username, password.getValue());
			} catch (Exception e) {
				Notification.show("Unable to update password, please try again in few minutes");
				return;
			}
			userData.setPassword(password.getValue());
			userDataRepository.save(userData);

			Notification.show("Password updated");
			// reset
			password.setValue("");
			rePassword.setValue("");
			passwordMatch = false;
			lRePassword.setValue(getUi().getMessageByLocaleService().getMessage("form.registration.renewpassword"));
			rePassword.removeStyleName("error");
			// password.setValidationVisible(false);
		} else {
			Notification.show("Password not valid and/or password retype not match");
		}
	}

	public CVManagerUI getUi() {
		return (CVManagerUI) UI.getCurrent();
	}

}