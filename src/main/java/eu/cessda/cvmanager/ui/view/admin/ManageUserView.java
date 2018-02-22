package eu.cessda.cvmanager.ui.view.admin;

import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;


import eu.cessda.cvmanager.security.SecurityService;
import eu.cessda.cvmanager.service.AgencyService;
import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.service.UserService;
import eu.cessda.cvmanager.service.dto.UserDTO;
import eu.cessda.cvmanager.ui.view.admin.form.AgencyForm;
import eu.cessda.cvmanager.ui.view.admin.form.UserForm;

@UIScope
@SpringView(name = ManageUserView.VIEW_NAME)
public class ManageUserView extends CvManagerAdminView {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "manage-user";
	private Locale locale = UI.getCurrent().getLocale();
	
	// autowired
	private final UserService userService;

	// components
	private MLabel pageTitle = new MLabel();
	private MVerticalLayout layout = new MVerticalLayout();
	private Grid<UserDTO> grid = new Grid<>(UserDTO.class);
	private MTextField filterText = new MTextField();
	private UserForm form;

	public ManageUserView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			CvManagerService cvManagerService, SecurityService securityService, UserService userService,
			BCryptPasswordEncoder encrypt) {
		super(i18n, eventBus, configService, cvManagerService, securityService, ManageUserView.VIEW_NAME);
		eventBus.subscribe(this, ManageUserView.VIEW_NAME);
		
		this.userService = userService;
		this.form = new UserForm(this, this.userService, encrypt);
	}

	@PostConstruct
	public void init() {
//		LoginView.NAVIGATETO_VIEWNAME = ManageAgencyView.VIEW_NAME;
		
		pageTitle.withContentMode(ContentMode.HTML)
			.withValue("<h1>Manage User</h1>");
		
		layout.withSpacing(false)
			.withMargin(false)
			.withFullSize();

		filterText.withPlaceholder("filter by name / username ...")
			.withWidth("300px")
			.withValueChangeMode(ValueChangeMode.LAZY)
			.addValueChangeListener(e -> updateList());

        Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
        clearFilterTextBtn.setDescription("Clear the current filter");
        clearFilterTextBtn.addClickListener(e -> filterText.clear());
        
        CssLayout filtering = new CssLayout();
        filtering.addComponents(filterText, clearFilterTextBtn);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        
        MButton addBtn = new MButton("+ Add new user");
        addBtn.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-normal");
        addBtn.addClickListener(e -> {
            grid.asSingleSelect().clear();
            form.setUserDTO(new UserDTO());
        });
        
        MCssLayout toolbar = new MCssLayout(filtering, addBtn);
        toolbar.withFullWidth();

        grid.setColumns("id", "firstName", "lastName", "username");
        
        HorizontalLayout main = new HorizontalLayout(grid, form);
        main.setSizeFull();
        grid.setSizeFull();
        main.setExpandRatio(grid, 1);

        layout.addComponents(pageTitle, toolbar, main);
        
        updateList();

        rightContainer.add(layout).withExpand(layout,1);

        form.setVisible(false);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                form.setVisible(false);
            } else {
                form.setUserDTO(event.getValue());
            }
        });
	}

	@Override
	public void enter(ViewChangeEvent event) {
		locale = UI.getCurrent().getLocale();
		updateMessageStrings(locale);
		updateList();
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
		List<UserDTO> agencyDTOs = userService.findAll(filterText.getValue());
		grid.setItems(agencyDTOs);
	}

}
