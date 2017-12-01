package eu.cessda.cvmanager.ui.view;

import java.util.Locale;

import org.gesis.security.SecurityService;
import org.gesis.security.db.DBservices;
import org.gesis.security.db.User;
import org.gesis.security.util.LoginSucceedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.navigator.MView;

import com.vaadin.annotations.Title;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
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
import com.vaadin.ui.UI;
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
public class LoginView extends MVerticalLayout implements MView, Translatable {

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
	
	@Autowired
	private I18N i18n;
	
	private MLabel headerTitle = new MLabel();
	private MLabel subHeaderTitle = new MLabel();
	protected MVerticalLayout mainContainer = new MVerticalLayout();


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
		

		// user name
		this.username = new TextField();


		this.username.focus();
		this.loginFormLayout.addComponent(this.username);
		// password
		this.password = new PasswordField();


		this.loginFormLayout.addComponent(this.password);

		// login
		this.login = new Button();
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
		
		this.loginFormLayout.setSizeUndefined();


		mainContainer
			.withWidth( "1170px" )
			.withStyleName( "mainlayout" )
			.withSpacing( true )
			.withMargin( new MarginInfo( false, false, false, false ) )
			.add( headerTitle, 
				subHeaderTitle, 
				this.loginFormLayout
			);
		
		this
			.withHeightUndefined()
			.add( mainContainer );

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
		updateMessageStrings(UI.getCurrent().getLocale());
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		headerTitle.withValue(i18n.get("view.login.title", locale));
		subHeaderTitle.withValue(i18n.get("view.login.content", locale));
		username.setCaption(i18n.get("view.login.label.username", locale) + ":");
		password.setCaption(i18n.get("view.login.label.password", locale) + ":");
		rememberMe.setCaption(i18n.get("view.login.label.remmeberme", locale));
		login.setCaption(i18n.get("view.login.button.login", locale));
	}

	@Override
	public void afterViewChange(ViewChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
