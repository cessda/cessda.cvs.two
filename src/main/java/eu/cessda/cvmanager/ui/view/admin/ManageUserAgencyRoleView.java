package eu.cessda.cvmanager.ui.view.admin;

import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;


import eu.cessda.cvmanager.security.SecurityService;
import eu.cessda.cvmanager.service.AgencyService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.service.LanguageRightService;
import eu.cessda.cvmanager.service.RoleService;
import eu.cessda.cvmanager.service.UserAgencyRoleService;
import eu.cessda.cvmanager.service.UserAgencyService;
import eu.cessda.cvmanager.service.UserService;
import eu.cessda.cvmanager.service.dto.UserAgencyDTO;
import eu.cessda.cvmanager.service.dto.UserAgencyRoleDTO;
import eu.cessda.cvmanager.ui.view.admin.form.AgencyForm;
import eu.cessda.cvmanager.ui.view.admin.form.UserAgencyForm;
import eu.cessda.cvmanager.ui.view.admin.form.UserAgencyRoleForm;

@UIScope
@SpringView(name = ManageUserAgencyRoleView.VIEW_NAME)
public class ManageUserAgencyRoleView extends CvManagerAdminView {

	private static final long serialVersionUID = 5095824532249073046L;
	public static final String VIEW_NAME = "manage-user-agency-role";
	private Locale locale = UI.getCurrent().getLocale();
	
	// autowired
	private final UserAgencyRoleService userAgencyRoleService;

	// components
	private MLabel pageTitle = new MLabel();
	private MVerticalLayout layout = new MVerticalLayout();
	private Grid<UserAgencyRoleDTO> grid = new Grid<>(UserAgencyRoleDTO.class);
	private MTextField filterText = new MTextField();
	private UserAgencyRoleForm form;

	public ManageUserAgencyRoleView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			CvManagerService cvManagerService, SecurityService securityService, UserAgencyService userAgencyService,
			UserAgencyRoleService userAgencyRoleService, UserService userService, RoleService roleService) {
		super(i18n, eventBus, configService, cvManagerService, securityService, ManageUserAgencyRoleView.VIEW_NAME);
		eventBus.subscribe(this, ManageUserAgencyRoleView.VIEW_NAME);
		
		this.userAgencyRoleService = userAgencyRoleService;
		this.form = new UserAgencyRoleForm(this, userAgencyService, userService, userAgencyRoleService, roleService);
	}

	@PostConstruct
	public void init() {
//		LoginView.NAVIGATETO_VIEWNAME = ManageAgencyView.VIEW_NAME;
		
		pageTitle.withContentMode(ContentMode.HTML)
			.withValue("<h1>Manage User - Agency - Role</h1>");
		
		layout.withSpacing(false)
			.withMargin(false)
			.withFullSize();

		filterText.withPlaceholder("filter by user name / agency name ...")
			.withWidth("300px")
			.withValueChangeMode(ValueChangeMode.LAZY)
			.addValueChangeListener(e -> updateList());

        Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
        clearFilterTextBtn.setDescription("Clear the current filter");
        clearFilterTextBtn.addClickListener(e -> filterText.clear());
        
        CssLayout filtering = new CssLayout();
        filtering.addComponents(filterText, clearFilterTextBtn);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        
        MButton addBtn = new MButton(" + Add new user - role");
        addBtn.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-normal");
        addBtn.addClickListener(e -> {
            grid.asSingleSelect().clear();
            form.setUserRoleLayoutVisible( true );
            form.setUserAgencyRoleDTO(new UserAgencyRoleDTO());
        });
        
        MButton addBtn2 = new MButton(" + Add new user&agency - role");
        addBtn2.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-normal");
        addBtn2.addClickListener(e -> {
            grid.asSingleSelect().clear();
            form.setUserAgencyRoleLayoutVisible( true );
            form.setUserAgencyRoleDTO(new UserAgencyRoleDTO());
        });
        
        MCssLayout toolbar = new MCssLayout(filtering, addBtn, addBtn2);
        toolbar.withFullWidth();

        grid.setColumns("id", "firstName", "lastName", "agency", "role");

        HorizontalLayout main = new HorizontalLayout(grid, form);
        main.setSizeFull();
        grid.setSizeFull();
        main.setExpandRatio(grid, 1);

        layout.addComponents(pageTitle, toolbar, main);
        // fetch list of Customers from service and assign it to Grid
        updateList();

        rightContainer.add(layout).withExpand(layout,1);

        form.setVisible(false);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                form.setVisible(false);
            } else {
            	form.setUserRoleLayoutVisible( false );
            	form.setUserAgencyRoleLayoutVisible( false );
                form.setUserAgencyRoleDTO(event.getValue());
            }
        });
	}

	@Override
	public void enter(ViewChangeEvent event) {
		locale = UI.getCurrent().getLocale();
		updateMessageStrings(locale);
        updateList();
        form.updateSetRole();
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

	@Override
	public void updateMessageStrings(Locale locale) {

		actionAdminPanel.updateMessageStrings(locale);
	}

	public void updateList() {
		List<UserAgencyRoleDTO> agencyDTOs = userAgencyRoleService.findAll();
		// copy name for user-agency to user
		for( UserAgencyRoleDTO uard : agencyDTOs) {
			if(uard.getAgency() != null) {
				uard.setFirstName( uard.getUaFirstName());
				uard.setLastName( uard.getUaLastName());
			}
		}
		grid.setItems(agencyDTOs);
	}

	public Grid<UserAgencyRoleDTO> getGrid() {
		return grid;
	}

	
}
