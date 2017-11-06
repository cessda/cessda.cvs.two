package eu.cessda.cvmanager.ui.view;

import org.gesis.security.SecurityService;
import org.gesis.security.db.DBservices;
import org.gesis.security.db.User;
import org.gesis.security.util.LoginSucceedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.label.MLabel;

import com.vaadin.annotations.Title;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.cessda.cvmanager.service.ConfigurationService;

/**
 * This is the login view. If the login operation is succeeded, this view throws
 * an event of type "org.gesis.security.util.LoginSucceedEvent".
 * 
 * @author Karam
 */
@Title("LOGIN")
@SpringView(name = LoginView.NAME)
@ViewScope
@Component(value = LoginView.NAME)
public class LoginView extends VerticalLayout implements View {

	private static final long serialVersionUID = -6564339199938804743L;

	/**
	 * The Bean and view name of the current view.
	 */
	public final static String NAME = "userlogin";

	/**
	 * This is the Bean name of the view that you must navigate to after the
	 * current view. The default value is: "" (an empty string). This value must
	 * be set according to the needs of your system. More precisely, you should
	 * set this value before navigating to this view.
	 */
	public static String NAVIGATETO_VIEWNAME = "";

	private FormLayout loginFormLayout;
	private TextField username;
	private PasswordField password;
	private Button login;
	private CheckBox rememberMe;

	@Autowired
	private SecurityService sec;

	@Autowired
	private DBservices dbService;

	@Autowired
	EventBus.UIEventBus eventBus;

	@Autowired
	private ConfigurationService configService;
	
	private MLabel headerTitle = new MLabel();
	private MLabel subHeaderTitle = new MLabel();

	public LoginView() {
	}

	@Override
	public void enter(ViewChangeEvent event) {

		// user name and password form
		this.loginFormLayout = new FormLayout();

		HorizontalLayout infoUserDisabled = new HorizontalLayout();
		Label userDisabledLabel = new Label(
				"<h3>Your account is not yet verified. Please check e-mail from Xecon and clcik the link to verify</h3>",
				ContentMode.HTML);
		userDisabledLabel.setWidthUndefined();

		// infoUserDisabled.withFullWidth().add(userDisabledLabel).withAlign(userDisabledLabel,
		// Alignment.MIDDLE_CENTER);
		infoUserDisabled.setSizeFull();
		infoUserDisabled.addComponent(userDisabledLabel);
		infoUserDisabled.setComponentAlignment(userDisabledLabel, Alignment.MIDDLE_CENTER);

		infoUserDisabled.setVisible(false);
		
		headerTitle.withStyleName( "headertitle" );
		headerTitle.withValue("Log in");
		
		subHeaderTitle.withValue("Please log in to manage controlled vocabularies in the CESSDA CV-Manager.");
		
		this.addComponents( headerTitle, subHeaderTitle );

		this.addComponents(this.loginFormLayout, infoUserDisabled);

		// user name
		this.username = new TextField();
		this.username.setCaption("User name:");

		this.username.focus();
		this.loginFormLayout.addComponent(this.username);
		// password
		this.password = new PasswordField();
		this.password.setCaption("Password:");

		this.loginFormLayout.addComponent(this.password);

		// login
		this.login = new Button("Login");
		this.login.addClickListener(e -> {

			// check for disabled user
			User user = dbService.getUserByUsername(this.username.getValue());
			if (user != null) {
				if (!user.isEnabled()) {
					infoUserDisabled.setVisible(true);
					return;
				}
			}

			boolean ok = this.sec.login(this.username.getValue(), this.password.getValue(), this.rememberMe.getValue());

			if (ok) {
				// Now when the session is reinitialized, we can enable
				// websocket communication. Or we could have just
				// used WEBSOCKET_XHR and skipped this step completely.
				this.getUI().getPushConfiguration().setTransport(Transport.WEBSOCKET);
				// this.getUI().getPushConfiguration().setPushMode(PushMode.AUTOMATIC);

				// throw an event when the login is succeeded
				eventBus.publish(this, new LoginSucceedEvent());

				this.getUI().getNavigator().navigateTo(LoginView.NAVIGATETO_VIEWNAME);
			}
		});
		this.login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		this.loginFormLayout.addComponent(this.login);

		// remember me check box
		this.rememberMe = new CheckBox();
		this.rememberMe.setCaption("Remember me");
		this.loginFormLayout.addComponent(this.rememberMe);

		HorizontalLayout infoUserDeleted = new HorizontalLayout();
		Label userDeleteLabel = new Label(
				"<h3>Please click <a href='" + configService.getServerContextPath()
						+ "/#!Registration'>here</a> to create a new account. Accounts created before June 2017 were deleted.</h3>",
				ContentMode.HTML);
		userDeleteLabel.setWidthUndefined();

		// infoUserDeleted.withFullWidth().add(userDeleteLabel).withAlign(userDeleteLabel,
		// Alignment.MIDDLE_CENTER);
		infoUserDeleted.setSizeFull();
		infoUserDeleted.addComponent(userDeleteLabel);
		infoUserDeleted.setComponentAlignment(userDeleteLabel, Alignment.MIDDLE_CENTER);

		this.addComponents(this.loginFormLayout/*, infoUserDeleted*/);
		this.setSizeFull();
		this.loginFormLayout.setSizeUndefined();
		// this.setHeight("100%");
		this.setComponentAlignment(this.loginFormLayout, Alignment.MIDDLE_CENTER);
		this.setMargin(true);
		this.setSpacing(true);

		// remember me login
		if (sec.rememberMeLogin()) {

			// Now when the session is reinitialized, we can enable
			// websocket communication. Or we could have just
			// used WEBSOCKET_XHR and skipped this step completely.
			this.getUI().getPushConfiguration().setTransport(Transport.WEBSOCKET);

			// throw an event when the login is succeeded
			eventBus.publish(this, new LoginSucceedEvent());

			this.getUI().getNavigator().navigateTo(LoginView.NAVIGATETO_VIEWNAME);

		}

	}
}
