package eu.cessda.cvmanager.ui.view;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.domain.Role;
import eu.cessda.cvmanager.domain.User;
import eu.cessda.cvmanager.security.DBservices;
import eu.cessda.cvmanager.security.RoleType;
import eu.cessda.cvmanager.security.SecurityService;
import eu.cessda.cvmanager.ui.layout.AdvertiserPanelLayout;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteTextField;


@SpringView( name = RegistrationView.VIEW_NAME )
@Component( value = RegistrationView.VIEW_NAME )
public class RegistrationView extends MVerticalLayout implements View, Translatable {

	private static final long serialVersionUID = -992143792695699876L;

	public static final String VIEW_NAME = "Registration";
	
	private Locale locale = UI.getCurrent().getLocale();

	private final I18N i18n;
	private final DBservices dbService;
	private final UIEventBus eventBus;
	private final SecurityService securityService;
//	private final EmailService emailService;
	
	private boolean passwordMatch;
	private boolean usernameExist;
	private String username;
	
	private AdvertiserPanelLayout advLayout;
	private MVerticalLayout mainLayout = new MVerticalLayout();
	private MHorizontalLayout profilePanel = new MHorizontalLayout();
	private MCssLayout profileLayout = new MCssLayout();

	private MLabel headerTitle = new MLabel();
	private MLabel subTitle = new MLabel();
	private MLabel lEmail = new MLabel();
	private MLabel lFirstname = new MLabel();
	private MLabel lLastname = new MLabel();
//	private MLabel lAffiliation = new MLabel();
	private MLabel lPersistentIdentifier = new MLabel();
	private MLabel lChangePasswordInfo = new MLabel();
	private MLabel lPassword = new MLabel();
	private MLabel lRePassword = new MLabel();
	
	private Map<String, String> organizationMap;
	private String[] organizations;

	private MTextField email = new MTextField();
	private MTextField firstname = new MTextField();
	private MTextField lastname = new MTextField();
//	private AutocompleteTextField affiliation = new AutocompleteTextField();
//	private ComboBox affiliation = new ComboBox();
	private MTextField persistentIdentifier = new MTextField();
	private PasswordField password = new PasswordField();
	private PasswordField rePassword = new PasswordField();
	private CheckBox checkAgreement = new CheckBox( "* I agree to the terms of service" );


	private MButton registerButton = new MButton();

//	private ZBWCollectionSuggestionProvider suggestionProvider = new ZBWCollectionSuggestionProvider();
	
	private final FocusListener focusListener;
	
	// autowired
	public RegistrationView(I18N i,SecurityService ss, UIEventBus eventBus, DBservices db/*, EmailService es*/) {
		this.i18n = i;
		this.eventBus = eventBus;
		this.securityService= ss;
		this.dbService = db;
//		this.emailService = es;
		
		focusListener = e -> eventBus.publish( this,
				new Label( i18n.get( "form.registration." + e.getComponent().getCaption() + ".help" ) ) );
	
		updateMessageStrings( locale );
	}

	@Override
	public void enter(ViewChangeEvent event) {
		eventBus.subscribe( this );

		headerTitle.setStyleName( "headertitle" );

		advLayout = new AdvertiserPanelLayout( eventBus, i18n );
		advLayout.setSpacing( true );

		lEmail
				.withStyleName( "required" )
				.withContentMode( ContentMode.HTML )
				.setValue( i18n.get( "form.registration.email" ) );

		email
//				.withValidator( new EmailValidator( i18n.get( "field.email.validator" ) ) )
//				.withRequired( true )
//				.withRequiredError( i18n.get( "field.email.validator" ) )
				.withFullWidth()
				.withAutocompleteOff();
//				.setValidationVisible( false );
		email.setCaption( "email" );
		email.addFocusListener( focusListener );
//		email.setTextChangeEventMode( TextChangeEventMode.EAGER );
		email.addValueChangeListener( e -> {
//			if ( email.isValid() )
//			{
				if ( dbService.getUserByUsername( email.getValue() ) != null )
				{
					Notification.show( "Email is already registered" );
					email.addStyleName( "error" );
					usernameExist = true;
					lEmail.setValue( i18n.get( "form.registration.email.existerror" ) );
				}
				else
				{
					lEmail.setValue( i18n.get( "form.registration.email" ) );
					email.removeStyleName( "error" );
					usernameExist = false;
				}
//			}
//			else
//			{
//				lEmail.setValue( i18n.get( "form.registration.email" ) );
//				email.removeStyleName( "error" );
//				usernameExist = false;
//			}
		} );


		lFirstname
				.withStyleName( "required" )
				.setValue( i18n.get( "form.registration.firstname" ) );

		firstname
				.withWidth( "100%" );
//				.withValidator(
//						new StringLengthValidator( "Please enter your firstname (2 letters min)", 2, 250, true ) )
//				.setImmediate( true );
		firstname.setCaption( "firstname" );
//		firstname.setValidationVisible( false );
		firstname.addFocusListener( focusListener );

		lLastname
				.withStyleName( "required" )
				.setValue( i18n.get( "form.registration.lastname" ) );

		lastname
				.withWidth( "100%" );
//				.withValidator(
//						new StringLengthValidator( "Please enter your lastname (2 letters min)", 2, 250, true ) )
//				.setImmediate( true );
		lastname.setCaption( "lastname" );
//		lastname.setValidationVisible( false );
		lastname.addFocusListener( focusListener );

//		lAffiliation
//			.withStyleName( "required" )
//			.setValue( i18n.get( "form.registration.institute" ) );

//		affiliation = new AutocompleteTextField();
//		affiliation
//				.withSizeFull()
//				.withCaption( "inistitute" )
//				.withSuggestionProvider( suggestionProvider )
//				.withMinChars( 3 )
//				.withSuggestionLimit( 20 )
//				.withFocusListener( focusListener );
//		affiliation.setSizeFull();
//		affiliation.setRequired( true );
//		affiliation.setNewItemsAllowed( true );
//		affiliation.setTextInputAllowed( true );
//		affiliation.setImmediate( true );
//		affiliation.setNullSelectionAllowed( false );
//		ComponentUtils.setComboboxItems(affiliation, organizationMap);

		lPersistentIdentifier.setValue( i18n.get( "form.registration.identifier" ) );

		persistentIdentifier
				.withFullWidth()
				.setCaption( "identifier" );
		persistentIdentifier.addFocusListener( focusListener );

		lChangePasswordInfo
				.withContentMode( ContentMode.HTML )
				.setValue( i18n.get( "form.registration.changepassword" ) );

		lPassword
			.withStyleName( "required" )
			.setValue( i18n.get( "form.registration.password" ) );

		password.setSizeFull();
//				.withValidator( new PasswordValidator(
//						"The password provided is not valid. Please enter min. 8 characters with at least one digit." ) )
//				.setImmediate( true );
//		password.setValidationVisible( false );
		password.setCaption( "pass" );
//		password.setRequired( true );
		password.addFocusListener( focusListener );
//		password.addTextChangeListener( e -> {
//			password.setValidationVisible( true );
//			if ( password.isValid() )
//				password.setValidationVisible( false );
//		} );

		lRePassword
				.withContentMode( ContentMode.HTML )
				.setValue( i18n.get( "form.registration.renewpassword" ) );

		rePassword.setSizeFull();
//				.withStyleName( "required" )
//				.withFullWidth()
//				.setImmediate( true );
		rePassword.setCaption( "repass" );
		rePassword.addFocusListener( focusListener );
//		rePassword.addTextChangeListener( e -> {
//			if ( password.isValid() )
//			{
//				if ( password.isEmpty() )
//					return;
//
//				if ( !e.getText().equals( password.getValue() ) )
//				{
//					rePassword.withStyleName( "error" );
//					lRePassword.setValue( i18n.get( "form.registration.renewpassword.error" ) );
//					passwordMatch = false;
//				}
//				else
//				{
//					rePassword.removeStyleName( "error" );
//					lRePassword.setValue( i18n.get( "form.registration.renewpassword" ) );
//					passwordMatch = true;
//				}
//			}
//		} );
//		
		checkAgreement.setCaptionAsHtml( true );
		checkAgreement.setCaption( "* I agree that my personal data will be used by CESSDA ERIC in providing this service." );

//		checkAgreement.setRequired( true );

		registerButton
				.withStyleName( "uploadButton margintop15px" )
				.withCaption( "Register" )
				.addClickListener( this::doRegistration );

		profileLayout
				.withStyleName( "advline uploadform" )
				.withFullWidth()
				.addComponents(
						lEmail, email,
						lFirstname, firstname,
						lLastname, lastname,
//						lAffiliation, affiliation,
						//lPersistentIdentifier, persistentIdentifier,
						//lChangePasswordInfo,
						lPassword, password,
						lRePassword, rePassword,
						checkAgreement,
						registerButton );
		profileLayout.addLayoutClickListener( e -> {
			if ( e.getClickedComponent() instanceof com.vaadin.ui.TextField ||
					e.getClickedComponent() instanceof com.vaadin.ui.ComboBox ||
					e.getClickedComponent() instanceof com.vaadin.ui.DateField ||
					e.getClickedComponent() instanceof com.vaadin.ui.TextArea ||
					e.getClickedComponent() instanceof com.vaadin.ui.PasswordField)
				this.eventBus.publish( this, e.getRelativeY() );
			else
				this.eventBus.publish( this, new Label( "" ) );
		} );

		profilePanel
				.withWidth( 100, Unit.PERCENTAGE )
				.add( profileLayout, advLayout )
				.withExpand( profileLayout, 0.69f )
				.withExpand( advLayout, 0.3f );

		mainLayout
				.withWidth( "60%" )
				.withStyleName( "mainlayout" )
				.withMargin( new MarginInfo( false, false, false, false ) )
				.withSpacing( true )
				.add(
						headerTitle,
						profilePanel );

		this
				.withFullWidth()
				.add( mainLayout )
				.withAlign( mainLayout, Alignment.MIDDLE_CENTER );
	}
	
	private void doRegistration() {
		if( !isUserInputValid() )
			return;
		
		String token = UUID.randomUUID().toString();
		
		Role role = new Role();
		role.setName( RoleType.ROLE_USER.name() );
		role.setDescription( RoleType.ROLE_USER.toString() );
		
		HashSet<Role> roles = new HashSet<>();
		roles.add(role);
		
		String randomPass = UUID.randomUUID().toString().substring( 0, 16 );

		User user = new User();
		user.setUsername(email.getValue());
		user.setFirstName(firstname.getValue());
		user.setLastName(lastname.getValue());
		user.setPassword(password.getValue());
		user.setToken(token);
		user.setRandomUsername( UUID.randomUUID().toString() );
		user.setEnable( true );
		user.setLocked(false);
		user.setRoles(roles);
		
		dbService.addUser(user);
		
		// send email
//		emailService.sendRegistrationEmail(token, user.getUsername());
		
		// Notification.show( "User is successfully added ..." );
//		this.getUI().getNavigator().navigateTo( RegistrationSuccessView.VIEW_NAME );
		this.getUI().getNavigator().navigateTo( HomeView.VIEW_NAME );
	}
	
	
	private boolean isUserInputValid() {
		boolean valid = true;
//		if ( !email.isValid() )
//		{
//			email.setValidationVisible( true );
//			Notification.show( "Email is invalid" );
//			valid = false;
//		}
//		if (!firstname.isValid() )
//		{
//			firstname.setValidationVisible( true );
//			Notification.show( "First name is mandatory" );
//			valid = false;
//		}
//		
//		if (!lastname.isValid()  )
//		{
//			lastname.setValidationVisible( true );
//			Notification.show( "Last name is mandatory" );
//			valid = false;
//		}
//		
//		if ( !affiliation.isValid() )
//		{
//			affiliation.setValidationVisible( true );
//			Notification.show( "Organisation is mandatory" );
//			valid = false;
//		}
//		
//		if ( !password.isValid() )
//		{
//			password.setValidationVisible( true );
//			Notification.show( "Password is mandatory" );
//			valid = false;
//		}
//
//		if ( usernameExist )
//		{
//			Notification.show( "Unable to update email, email is already registered" );
//			valid = false;
//		}
//		
//		if( !passwordMatch ) {
//			Notification.show( "Password does not match" );
//			valid = false;
//		}
//		
//		if( !checkAgreement.isValid())
//		{
//			Notification.show( "Please accept the terms of service" );
//			valid = false;
//		}
		
		return valid;
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		UI.getCurrent().getPage().setTitle(  i18n.get("page.title.home", locale)  );
		organizations = i18n.get( "organization.list", locale ).split( "," );
		headerTitle.setValue( "Registration" );
	}

}
