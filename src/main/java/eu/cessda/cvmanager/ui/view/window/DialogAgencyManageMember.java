package eu.cessda.cvmanager.ui.view.window;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.service.dto.UserAgencyDTO;
import org.gesis.wts.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.ui.component.AgencyMemberGridComponent;
import eu.cessda.cvmanager.ui.view.form.AgencyMemberForm;

public class DialogAgencyManageMember extends MWindow implements Translatable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(DialogAgencyManageMember.class);

	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;
	private final UserService userService;
	private final RoleService roleService;
	private final AgencyService agencyService;
	private final UserAgencyService userAgencyService;
	private final BCryptPasswordEncoder encrypt;
	private final Locale locale;
	
	private MCssLayout layout = new MCssLayout();
	private Grid<UserDTO> grid = new Grid<>(UserDTO.class);
	private AgencyMemberForm form;
	private AgencyDTO agency;
	private MButton addBtn = new MButton(" + Add new members");

	public DialogAgencyManageMember(UIEventBus eventBus, AgencyDTO agency, UserService userService,
			RoleService roleService, AgencyService agencyService, UserAgencyService userAgencyService,
			BCryptPasswordEncoder encrypt, I18N i18n, Locale locale) {
		
		super( "Manage "  + agency.getName() + " members, roles and languages");
		
		this.eventBus = eventBus;
		this.agency = agency;
		this.userService = userService;
		this.roleService = roleService;
		this.agencyService = agencyService;
		this.userAgencyService = userAgencyService;
		this.encrypt = encrypt;
		this.i18n = i18n;
		this.locale = locale;
		
		this.form = new AgencyMemberForm( this, userAgencyService, userService, agencyService, encrypt, i18n, locale);
		
		initLayout();
	}
	
	private void initLayout() {
		
        addBtn.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-normal");
        addBtn.addClickListener(e -> {
        	addBtn.setVisible( false );
            form.setUserLayoutVisible( true );
            form.activateAddUserForm( false );
            
            UserAgencyDTO uAgency = new UserAgencyDTO();
            uAgency.setAgencyId( agency.getId());
            uAgency.setAgencyName( agency.getName() );
                        
            form.setUserAgencyDTO( uAgency );
        });
        
        
        MCssLayout toolbar = new MCssLayout(addBtn);
        toolbar.withFullWidth();

        HorizontalLayout main = new HorizontalLayout(grid, form);
        main.setSizeFull();
        grid.setSizeFull();
        main.setExpandRatio(grid, 1);
        // fetch list of Customers from service and assign it to Grid
        updateList();

        form.setVisible(false);
        
        grid.addStyleNames(ValoTheme.TABLE_BORDERLESS, "undefined-height", "search-grid");
		grid.setHeaderVisible(false);
		grid.setSelectionMode( SelectionMode.NONE );
		
		layout
			.withHeight("96%")
			.withWidth("100%")
			.withStyleName("dialog-content")
			.add( 
					toolbar, main
			);
		this
			.withHeight("650px")
			.withWidth("1200px")
			.withModal( true )
			.withContent(layout);

		updateMessageStrings(locale);
	}

	public void updateList() {
		// first get all useragency member
		List<UserDTO> members = userService.findAllByAgencyId( agency.getId() );
		members = members.stream().filter( m -> m.getRoles() != null && !m.getRoles().isEmpty())
				.collect( Collectors.toList());
		
		grid.setItems( members );
		
		grid.removeAllColumns();

		grid.addColumn( member -> {
			List<UserAgencyDTO> uas = userAgencyService.findByUser( member.getId() ) .stream()
						.filter( ua -> ua.getAgencyId().equals( agency.getId()))
						.collect( Collectors.toList());
			return new AgencyMemberGridComponent( this, member, agency, uas,
					userService, roleService, agencyService, userAgencyService,
					i18n, locale);
		}, new ComponentRenderer()).setId("agencyMember");
		
		grid.getColumn("agencyMember").setExpandRatio(1);
	}

	public Grid<UserDTO> getGrid() {
		return grid;
	}

	public void setGrid(Grid<UserDTO> grid) {
		this.grid = grid;
	}
	
	

	public MButton getAddBtn() {
		return addBtn;
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
}
