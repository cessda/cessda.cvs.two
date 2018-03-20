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
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.service.dto.RoleDTO;
import org.gesis.wts.service.dto.UserAgencyDTO;
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
	private final UserAgencyDTO agencyMember;
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
	private MCssLayout roleAddBlock = new MCssLayout();
	private MLabel roleLabel = new MLabel( "Role: " );
	private ComboBox<RoleDTO> rolesCb = new ComboBox<>();
	private MButton saveRole = new MButton();
	private MCssLayout languageAddBlock = new MCssLayout();
	private MLabel languageLabel = new MLabel( "Language: " );
    private NativeSelect<Language> languageOption = new NativeSelect<>();
	private MButton saveLanguage = new MButton();
	
    private final Set<String> userAgencyRoleTypes = new HashSet<>(Arrays.asList( Constants.USER_AGENCY_ROLE ));
	
	public AgencyMemberGridComponent(DialogAgencyManageMember dialogAgencyMember, UserAgencyDTO agencyMember, 
			AgencyDTO agency, UserService userService, RoleService roleService,
			AgencyService agencyService, UserAgencyService userAgencyService,
			I18N i18n, Locale locale) {
		
		this.dialogAgencyMember = dialogAgencyMember;
		this.agencyMember = agencyMember;
		this.agency = agency;
		this.userService = userService;
		this.roleService = roleService;
		this.agencyService = agencyService;
		this.userAgencyService = userAgencyService;
		this.i18n = i18n;
		this.locale = locale;
		
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
//		generateSummaryRole();
//		generateSummaryLanguage();
		
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
		
        languageOption.addValueChangeListener( e -> {
//        	if( e.getValue() != null )
//        		// todo SOMETHING
        });
		
		saveLanguage
			.withCaption( "+ Add" )
			.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "btn-spacing-normal")
			.addClickListener( e -> {
				
			});
		
		languageAddBlock
			.withFullWidth()
			.add(
				languageLabel,
				languageOption,
				saveLanguage
			);
		
		languageBlock
			.withFullWidth()
			.add( 
				languageAddBlock
			);
	}

	private void generateDetailRole() {
	
		
		Set<RoleDTO> filteredRole = roleService
								.findAll()
								.stream()
								.filter( p -> userAgencyRoleTypes.contains( p.getName()))
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
//	    			if(e.getValue() != null)
//	    			else
	    			});
		
		saveRole
			.withCaption( "+ Add" )
			.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "btn-spacing-normal")
			.addClickListener( e -> {
				
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
				roleAddBlock
			);
	}
	
//	private MButton generateDeleteRoleButton( UserAgencyRoleDTO userRole) {
//		MButton bDelete = new MButton()
//			.withIcon( FontAwesome.TRASH)
//			.withStyleName( ValoTheme.BUTTON_DANGER, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-large");
//		
//		bDelete.addClickListener( e -> {
//			userAgencyRoleService.delete( userRole.getId() );
//			refresh();
//		});
//		
//		return bDelete;
//	}
	

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
					"Are you sure you want to remove \"" + agencyMember.getFirstName() + " " +
					agencyMember.getLastName() + "\" from \"" + agencyMember.getAgencyName() + "\"?", "yes",
					"cancel",
						dialog -> {
							if( dialog.isConfirmed() ) {
								//remove from agency
								userAgencyService.delete(agencyMember.getId());
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

//	private void generateSummaryLanguage() {
//		StringBuilder sb = new StringBuilder();
//		summaryLanguage
//			.withWidth( "20%" )
//			.withContentMode( ContentMode.HTML )
//			.withValue( sb.toString());
//	}
//
//	private void generateSummaryRole() {
//		StringBuilder sb = new StringBuilder();
//		for( UserAgencyRoleDTO role : agencyMember.getUserAgencyRoles()) {
//			sb.append( role.getRole() + "</br>");
//		}
//		summaryRole
//			.withWidth( "35%" )
//			.withContentMode( ContentMode.HTML )
//			.withValue( sb.toString());
//	}

	private void generateSummaryName() {
		summaryName
			.withWidth( "25%" )
			.withContentMode( ContentMode.HTML )
			.withValue( "<strong>" +agencyMember.getFirstName() + " " + agencyMember.getLastName() + "</strong>");
	}
	
	private void refresh() {
//		List<UserAgencyRoleDTO> agencyRoles = userAgencyRoleService.findByUserAgency(agencyMember.getUserAgency().getId());
//		agencyMember.setUserAgencyRoles(agencyRoles);
//		
//		List<LanguageRightDTO> languageRights = languageRightService.findByUserAgency(agencyMember.getUserAgency().getId());
//		agencyMember.setLanguageRights(languageRights);
//		
		initLayout();
	}
}
