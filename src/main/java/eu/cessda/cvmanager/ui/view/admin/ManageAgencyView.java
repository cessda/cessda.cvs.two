package eu.cessda.cvmanager.ui.view.admin;

import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
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
import eu.cessda.cvmanager.service.dto.AgencyDTO;
import eu.cessda.cvmanager.ui.view.admin.form.AgencyForm;

@UIScope
@SpringView(name = ManageAgencyView.VIEW_NAME)
public class ManageAgencyView extends CvManagerAdminView {

	private static final long serialVersionUID = 6904286186508174249L;
	public static final String VIEW_NAME = "manage-agency";
	private Locale locale = UI.getCurrent().getLocale();
	
	// autowired
	private final AgencyService agencyService;

	// components
	private MLabel pageTitle = new MLabel();
	private MVerticalLayout layout = new MVerticalLayout();
	private Grid<AgencyDTO> grid = new Grid<>(AgencyDTO.class);
	private MTextField filterText = new MTextField();
	private AgencyForm form;

	public ManageAgencyView(I18N i18n, EventBus.UIEventBus eventBus, ConfigurationService configService,
			CvManagerService cvManagerService, SecurityService securityService, AgencyService agencyService) {
		super(i18n, eventBus, configService, cvManagerService, securityService, ManageAgencyView.VIEW_NAME);
		eventBus.subscribe(this, ManageAgencyView.VIEW_NAME);
		
		this.agencyService = agencyService;
		this.form =  new AgencyForm(this, this.agencyService);
	}

	@PostConstruct
	public void init() {
//		LoginView.NAVIGATETO_VIEWNAME = ManageAgencyView.VIEW_NAME;
		
		pageTitle.withContentMode(ContentMode.HTML)
			.withValue("<h1>Manage Agency</h1>");
		
		layout.withSpacing(false)
			.withMargin(false)
			.withFullSize();

		filterText.withPlaceholder("filter by name / description ...")
			.withValueChangeMode(ValueChangeMode.LAZY)
			.addValueChangeListener(e -> updateList());

        Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
        clearFilterTextBtn.setDescription("Clear the current filter");
        clearFilterTextBtn.addClickListener(e -> filterText.clear());
        
        CssLayout filtering = new CssLayout();
        filtering.addComponents(filterText, clearFilterTextBtn);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        
        Button addCustomerBtn = new Button("Add new agency");
        addCustomerBtn.addClickListener(e -> {
            grid.asSingleSelect().clear();
            form.setAgencyDTO(new AgencyDTO());
        });
        
        HorizontalLayout toolbar = new HorizontalLayout(filtering, addCustomerBtn);

        grid.setColumns("id", "name", "description");
        
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
                form.setAgencyDTO(event.getValue());
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
		List<AgencyDTO> agencyDTOs = agencyService.findAll(filterText.getValue());
		grid.setItems(agencyDTOs);
	}

}
