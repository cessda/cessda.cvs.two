package eu.cessda.cvmanager.ui.component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.gesis.wts.config.Constants;
import org.gesis.wts.domain.enumeration.Language;
import org.gesis.wts.domain.enumeration.LanguageType;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.LanguageRightService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyRoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.service.dto.LanguageRightDTO;
import org.gesis.wts.service.dto.RoleDTO;
import org.gesis.wts.service.dto.UserAgencyRoleDTO;
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
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.model.AgencyMemberItem;
import eu.cessda.cvmanager.ui.view.window.DialogAgencyManageMember;

public class AgencyMemberGridComponent extends CustomComponent {

	private static final long serialVersionUID = 1L;

	final static Logger log = LoggerFactory.getLogger(AgencyMemberGridComponent.class);

	private final I18N i18n;
	private final UserService userService;
	private final RoleService roleService;
	private final AgencyService agencyService;
	private final UserAgencyService userAgencyService;
	private final UserAgencyRoleService userAgencyRoleService;
	private final LanguageRightService languageRightService;
	private final Locale locale;
	private final DialogAgencyManageMember dialogAgencyMember;
	private final AgencyMemberItem agencyMemberItem;
	private final AgencyDTO agency;
	
	// contains of 2 layout, summary layout and detail layout
	private MCssLayout container = new MCssLayout();
	private MVerticalLayout mainLayout = new MVerticalLayout();
	private MCssLayout summaryLayout = new MCssLayout();
	private MHorizontalLayout detailLayout = new MHorizontalLayout();
	
	// summary layout
	private MLabel summaryName = new MLabel();
	private MLabel summaryRole = new MLabel();
	private MLabel summaryLanguage = new MLabel();
	private MHorizontalLayout summaryButton = new MHorizontalLayout();
	private MButton deleteButton = new MButton();
	private MButton editButton = new MButton();
	
	// detail layout
	private MCssLayout roleBlock = new MCssLayout();
	private MCssLayout languageBlock = new MCssLayout();
	private Grid<UserAgencyRoleDTO> roleGrid = new Grid<>(UserAgencyRoleDTO.class);
	private Grid<LanguageRightDTO> languageGrid = new Grid<>(LanguageRightDTO.class);
	private MCssLayout roleAddBlock = new MCssLayout();
	private MLabel roleLabel = new MLabel( "Role: " );
	private ComboBox<RoleDTO> rolesCb = new ComboBox<>();
	private MButton saveRole = new MButton();
	private MCssLayout languageAddBlock = new MCssLayout();
	private MLabel languageLabel = new MLabel( "Language: " );
	private NativeSelect<LanguageType> languageTypeOption = new NativeSelect<>();
    private NativeSelect<Language> languageOption = new NativeSelect<>();
	private MButton saveLanguage = new MButton();
	
    private final Set<String> userAgencyRoleTypes = new HashSet<>(Arrays.asList( Constants.USER_AGENCY_ROLE ));
    private UserAgencyRoleDTO newUserRole = new UserAgencyRoleDTO();
    private LanguageRightDTO newLanguageRight = new LanguageRightDTO();
	
	public AgencyMemberGridComponent(DialogAgencyManageMember dialogAgencyMember, AgencyMemberItem agencyMemberItem, 
			AgencyDTO agency, UserService userService, RoleService roleService,
			AgencyService agencyService, UserAgencyService userAgencyService,
			UserAgencyRoleService userAgencyRoleService, LanguageRightService languageRightService,
			I18N i18n, Locale locale) {
		
		this.dialogAgencyMember = dialogAgencyMember;
		this.agencyMemberItem = agencyMemberItem;
		this.agency = agency;
		this.userService = userService;
		this.roleService = roleService;
		this.agencyService = agencyService;
		this.userAgencyService = userAgencyService;
		this.userAgencyRoleService = userAgencyRoleService;
		this.languageRightService = languageRightService;
		this.i18n = i18n;
		this.locale = locale;
		
		newUserRole.setUserAgencyId(agencyMemberItem.getUserAgency().getId());
		newLanguageRight.setUserAgencyId(agencyMemberItem.getUserAgency().getId());
		
		languageTypeOption.setItems( LanguageType.values());
		languageOption.setItems(Language.values());
		
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
		
		generateSummaryName();
		generateSummaryRole();
		generateSummaryLanguage();
		
		summaryLayout
			.add( 
				summaryName,
				summaryRole,
				summaryLanguage,
				summaryButton
			);
		
		generateDetailRole();
		generateDetailLanguage();
		
		detailLayout
			.add( 
				roleBlock,
				languageBlock
			);
	}

	private void generateDetailLanguage() {
		List<LanguageRightDTO> languageRightDTOs = agencyMemberItem.getLanguageRights();
		
		languageGrid.setItems(languageRightDTOs);
		languageGrid.setSizeFull();
		languageGrid.setHeight("200px");
		languageGrid.setColumns( "languageType", "language" );
		languageGrid.setSelectionMode( SelectionMode.SINGLE );
		languageGrid.addComponentColumn( this::generateDeleteLanguageButton);
		
		languageTypeOption.addValueChangeListener( e -> {
        	if( e.getValue() != null )
        		newLanguageRight.setLanguageType(e.getValue());
        });
        
        languageOption.addValueChangeListener( e -> {
        	if( e.getValue() != null )
        		newLanguageRight.setLanguage(e.getValue());
        });
		
		saveLanguage
			.withCaption( "+ Add" )
			.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "btn-spacing-normal")
			.addClickListener( e -> {
				if( newLanguageRight.getLanguage() != null && newLanguageRight.getLanguageType() != null ) {
					languageRightService.save( newLanguageRight );
					newLanguageRight.setLanguage( null );
					newLanguageRight.setLanguageType( null );
					languageOption.setValue( null );
					languageTypeOption.setValue( null );
					refresh();
				}
			});
		
		languageAddBlock
			.withFullWidth()
			.add(
				languageLabel,
				languageTypeOption,
				languageOption,
				saveLanguage
			);
		
		languageBlock
			.withFullWidth()
			.add( 
				languageGrid,
				languageAddBlock
			);
	}

	private void generateDetailRole() {
		List<UserAgencyRoleDTO> agencyDTOs = agencyMemberItem.getUserAgencyRoles();
		
		roleGrid.setItems(agencyDTOs);
		roleGrid.setSizeFull();
		roleGrid.setHeight("200px");
		roleGrid.setColumns( "role" );
		roleGrid.setSelectionMode( SelectionMode.NONE );
		roleGrid.addComponentColumn( this::generateDeleteRoleButton);
		
		Set<Long> currentRoleIds = agencyMemberItem.getUserAgencyRoles()
									.stream().map( i -> i.getRoleId())
									.collect( Collectors.toSet());
		
		Set<RoleDTO> filteredRole = roleService
								.findAll()
								.stream()
								.filter( p -> userAgencyRoleTypes.contains( p.getName()))
								.filter( p -> !currentRoleIds.contains( p.getId()))
								.collect( Collectors.toSet());
		
		rolesCb.setItems( filteredRole );
		rolesCb.setWidth("300px");
		rolesCb.setItemCaptionGenerator(new ItemCaptionGenerator<RoleDTO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String apply(RoleDTO item) {
				return item.getName().substring(5);
			}
		});
		rolesCb.addValueChangeListener( 
	    		e -> {
	    			if(e.getValue() != null)
	    				newUserRole.setRoleId( e.getValue().getId());
	    			else
	    				newUserRole.setRoleId( null );
	    			});
		
		saveRole
			.withCaption( "+ Add" )
			.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "btn-spacing-normal")
			.addClickListener( e -> {
				if( newUserRole.getRoleId() != null ) {
					userAgencyRoleService.save( newUserRole );
					newUserRole.setRoleId( null );
					rolesCb.setValue( null );
					refresh();
				}
			});
		
		roleAddBlock
			.withFullWidth()
			.add(
				roleLabel,
				rolesCb,
				saveRole
			);
		
		roleBlock
			.withFullWidth()
			.add( 
				roleGrid, 
				roleAddBlock
			);
	}
	
	private MButton generateDeleteRoleButton( UserAgencyRoleDTO userRole) {
		MButton bDelete = new MButton()
			.withIcon( FontAwesome.TRASH)
			.withStyleName( ValoTheme.BUTTON_DANGER, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-large");
		
		bDelete.addClickListener( e -> {
			userAgencyRoleService.delete( userRole.getId() );
			refresh();
		});
		
		return bDelete;
	}
	
	private MButton generateDeleteLanguageButton( LanguageRightDTO languageRight) {
		MButton bDelete = new MButton()
			.withIcon( FontAwesome.TRASH)
			.withStyleName( ValoTheme.BUTTON_DANGER, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-large");
		
		bDelete.addClickListener( e -> {
			languageRightService.delete( languageRight.getId() );
			refresh();
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
					"Are you sure you want to remove \"" + agencyMemberItem.getUserAgency().getFirstName() + " " +
					agencyMemberItem.getUserAgency().getLastName() + "\" from \"" + agencyMemberItem.getUserAgency().getAgencyName() + "\"?", "yes",
					"cancel",
						dialog -> {
							if( dialog.isConfirmed() ) {
								// remove any language and role first
								for( LanguageRightDTO delLanguage : agencyMemberItem.getLanguageRights()) {
									languageRightService.delete( delLanguage.getId());
								}
								for( UserAgencyRoleDTO delRole : agencyMemberItem.getUserAgencyRoles()) {
									userAgencyRoleService.delete( delRole.getId() );
								}
								//remove from agency
								userAgencyService.delete(agencyMemberItem.getUserAgency().getId());
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

	private void generateSummaryLanguage() {
		StringBuilder sb = new StringBuilder();
		for( LanguageRightDTO language : agencyMemberItem.getLanguageRights()) {
			sb.append( (language.getLanguageType() == LanguageType.SOURCE ? "[SL] ":"[TL] ") + language.getLanguage().toString() + "</br>");
		}
		summaryLanguage
			.withWidth( "20%" )
			.withContentMode( ContentMode.HTML )
			.withValue( sb.toString());
	}

	private void generateSummaryRole() {
		StringBuilder sb = new StringBuilder();
		for( UserAgencyRoleDTO role : agencyMemberItem.getUserAgencyRoles()) {
			sb.append( role.getRole() + "</br>");
		}
		summaryRole
			.withWidth( "35%" )
			.withContentMode( ContentMode.HTML )
			.withValue( sb.toString());
	}

	private void generateSummaryName() {
		summaryName
			.withWidth( "25%" )
			.withContentMode( ContentMode.HTML )
			.withValue( "<strong>" +agencyMemberItem.getUserAgency().getFirstName() + " " + agencyMemberItem.getUserAgency().getLastName() + "</strong>");
	}
	
	private void refresh() {
		List<UserAgencyRoleDTO> agencyRoles = userAgencyRoleService.findByUserAgency(agencyMemberItem.getUserAgency().getId());
		agencyMemberItem.setUserAgencyRoles(agencyRoles);
		
		List<LanguageRightDTO> languageRights = languageRightService.findByUserAgency(agencyMemberItem.getUserAgency().getId());
		agencyMemberItem.setLanguageRights(languageRights);
		
		initLayout();
	}
}
