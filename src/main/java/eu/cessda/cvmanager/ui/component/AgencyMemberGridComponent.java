package eu.cessda.cvmanager.ui.component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gesis.wts.config.Constants;
import org.gesis.wts.domain.enumeration.AgencyRole;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.service.dto.RoleDTO;
import org.gesis.wts.service.dto.UserAgencyDTO;
import org.gesis.wts.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageMember;

public class AgencyMemberGridComponent extends CustomComponent {

	private static final long serialVersionUID = 1L;

	final static Logger log = LoggerFactory.getLogger(AgencyMemberGridComponent.class);

	private final I18N i18n;
	private final UserService userService;
	private final RoleService roleService;
	private final AgencyService agencyService;
	private final UserAgencyService userAgencyService;
	private final Locale locale;
	private final DialogAgencyManageMember dialogAgencyMember;
	private final UserDTO member;
	private final AgencyDTO agency;
	
	// contains of 2 layout, summary layout and detail layout
	private MCssLayout container = new MCssLayout();
	private MVerticalLayout mainLayout = new MVerticalLayout();
	private MCssLayout summaryLayout = new MCssLayout();
	private MHorizontalLayout detailLayout = new MHorizontalLayout();
	
	// summary layout
	private MLabel summaryName = new MLabel();
	private MLabel summaryRole = new MLabel();
	private MHorizontalLayout summaryButton = new MHorizontalLayout();
	private MButton deleteButton = new MButton();
	private MButton editButton = new MButton();
	
	// detail layout
	private MCssLayout roleBlock = new MCssLayout();
	private MCssLayout roleAddBlock = new MCssLayout();
	private MLabel roleLabel = new MLabel( "Role: " );
	private NativeSelect<AgencyRole> rolesCb = new NativeSelect<>();
	private MButton saveRole = new MButton();
	private MLabel languageLabel = new MLabel( "Language: " );
    private NativeSelect<Language> languageOption = new NativeSelect<>();
	private MButton saveLanguage = new MButton();
	
	private List<UserAgencyDTO> agencyMembers;
	private Map<String, List<String>> agencyRoleMap = new LinkedHashMap<>(); 
	private Grid<UserAgencyDTO> grid = new Grid<>(UserAgencyDTO.class);
	private UserAgencyDTO userAgency;

	
    private final Set<AgencyRole> userAgencyRoleTypes = new LinkedHashSet<>( Arrays.asList(AgencyRole.values()));
	
	public AgencyMemberGridComponent(DialogAgencyManageMember dialogAgencyMember, 
			UserDTO member, AgencyDTO agency, List<UserAgencyDTO> agencyMembers, 
			UserService userService, RoleService roleService,
			AgencyService agencyService, UserAgencyService userAgencyService,
			I18N i18n, Locale locale) {
		
		this.dialogAgencyMember = dialogAgencyMember;
		this.agencyMembers = agencyMembers;
		this.agency = agency;
		this.member = member;
		this.userService = userService;
		this.roleService = roleService;
		this.agencyService = agencyService;
		this.userAgencyService = userAgencyService;
		this.i18n = i18n;
		this.locale = locale;
				
		languageOption.setItems(Language.values());
        languageOption.setItemCaptionGenerator( new ItemCaptionGenerator<Language>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String apply(Language item) {
				return item.name() + " (" +item.getLanguage() + ")";
			}
		});
		
		detailLayout.setVisible( false );
		
		generateSummaryButton();
		
		initLayout();
				
		mainLayout
			.withFullWidth()
			.add( 
				summaryLayout.withFullWidth(),
				detailLayout.withFullWidth()
			);
		
		container
			.withStyleName("itemcontainer")
			.withFullWidth()
			.add(mainLayout);
		
		setCompositionRoot(container);
	}



	private void initLayout() {
		
		userAgency = new UserAgencyDTO();
		userAgency.setUserId( member.getId() );
		userAgency.setAgencyId( agency.getId());
		userAgency.setLanguage( null );
		userAgency.setAgencyRole( null );
		
		languageOption.setValue(null);
		languageOption.setVisible( false );
		rolesCb.setValue( null );
		
		generateSummaryName();
		generateSummaryRole();
		
		summaryLayout
			.add( 
				summaryName,
				summaryRole,
				summaryButton
			);
		
		generateDetailRole();
		
		detailLayout
			.add( 
				roleBlock
			);
	}

	private void generateDetailRole() {
	
		grid.setItems( agencyMembers );
		grid.setColumns( "firstName", "lastName", "agencyName", "agencyRole", "language");
		grid.setSizeFull();
		grid.setHeight("200px");
		grid.setSelectionMode( SelectionMode.NONE );
		grid.addComponentColumn( this::generateDeleteRoleButton);
		
		
		rolesCb.setItems( AgencyRole.values());
		rolesCb.setWidth("300px");
	
		rolesCb.addValueChangeListener( e -> {
        	if( e.getValue() != null ) {
        		if( e.getValue().equals(AgencyRole.VIEW) || e.getValue().equals(AgencyRole.ADMIN) )
        			languageOption.setVisible( false );
        		else 
        			languageOption.setVisible( true );
        		
        		userAgency.setAgencyRole( e.getValue() );
        	} else {
        		languageOption.setVisible( false );
        		userAgency.setAgencyRole( null );
        	}
        });
		
		languageOption.addValueChangeListener( e -> {
        	if( e.getValue() != null ) {
        		userAgency.setLanguage( e.getValue() );
        	} else
        		userAgency.setLanguage( null );
        });
		
		saveRole
			.withCaption( "+ Add" )
			.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "btn-spacing-normal")
			.addClickListener( e -> {
				if( userAgency.getAgencyRole() == null ) {
		    		Notification.show("Please select role!");
		    		return;
		    	}
		    	if( languageOption.isVisible() && userAgency.getLanguage() == null ) {
		    		Notification.show("Please select language!");
		    		return;
		    	}
				if( !isRoleExist()) {
					userAgencyService.save(userAgency);
					refresh();
				} else
					Notification.show( "Error: Role exist!" );
			});
		
		roleAddBlock
			.withFullWidth()
			.add(
				roleLabel,
				rolesCb,
				languageOption,
				saveRole
			);
		
		roleBlock
			.withFullWidth()
			.add( 
				grid,
				roleAddBlock
			);
	}
	
	private MButton generateDeleteRoleButton( UserAgencyDTO userRole) {
		MButton bDelete = new MButton()
			.withIcon( FontAwesome.TRASH)
			.withStyleName( ValoTheme.BUTTON_DANGER, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-large");
		
		bDelete.addClickListener( e -> {
			if( agencyMembers.size() > 1) {
				userAgencyService.delete( userRole.getId() );
				refresh();
			} else { // the last user-agency relation
				//remove from agency
				agencyMembers.stream().forEach( item -> userAgencyService.delete( item.getId()));
				// ask parent to refresh
				dialogAgencyMember.updateList();
			}
		});
		return bDelete;
	}
	

	private void generateSummaryButton() {
		editButton
			.withIcon( FontAwesome.EDIT)
			.withCaption( "Edit" )
			.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-normal")
			.addClickListener( e -> detailLayout.setVisible( !detailLayout.isVisible()));
		
		deleteButton
			.withIcon( FontAwesome.TRASH)
			.withCaption( "Delete" )
			.withStyleName( ValoTheme.BUTTON_DANGER, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-large")
			.addClickListener( e ->{
				ConfirmDialog.show( this.getUI(), "Confirm",
					"Are you sure you want to remove \"" + member.getFirstName() + " " +
					member.getLastName() + "\" from \"" + agency.getName() + "\"?", "yes",
					"cancel",
						dialog -> {
							if( dialog.isConfirmed() ) {
								//remove from agency
								agencyMembers.stream().forEach( item -> userAgencyService.delete( item.getId()));
								// ask parent to refresh
								dialogAgencyMember.updateList();
							}
						}
					);
			});
		
		summaryButton
			.withWidth("20%")
			.withStyleName( "pull-right" )
			.add( editButton, deleteButton);
	}

	private void generateSummaryRole() {
		StringBuilder sb = new StringBuilder();
		for( UserAgencyDTO agencyMember : agencyMembers) {
			if( agencyMember.getAgencyRole() != null )
				sb.append( agencyMember.getAgencyRole().toString() );
			if( agencyMember.getLanguage() != null ) {
				sb.append( "- " + agencyMember.getLanguage().name() + " (" +agencyMember.getLanguage() + ")</br>" );
			} else {
				sb.append( "</br>" );
			}
		}
		summaryRole
			.withWidth( "45%" )
			.withContentMode( ContentMode.HTML )
			.withValue( sb.toString());
	}

	private void generateSummaryName() {
		summaryName
			.withWidth( "35%" )
			.withContentMode( ContentMode.HTML )
			.withValue( "<strong>" +member.getFirstName() + " " + member.getLastName() + "</strong>");
	}
	
	private void refresh() {
		agencyMembers = userAgencyService.findByUser( member.getId() );
		initLayout();
	}
	
	private boolean isRoleExist() {
		for( UserAgencyDTO a: agencyMembers) {
			if( a.getAgencyRole().equals( userAgency.getAgencyRole())) {
				if( userAgency.getLanguage() == null) {
					if( a.getLanguage() == null )
						return true;
					else
						return false;
				} else { 
					if( a.getLanguage().equals( userAgency.getLanguage() ) )
						return true;
					else
						return false;
				}
			}
		}
		return false;
	}
}
