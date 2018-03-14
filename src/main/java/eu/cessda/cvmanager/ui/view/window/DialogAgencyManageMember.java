package eu.cessda.cvmanager.ui.view.window;

import java.util.List;
import java.util.Locale;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.LanguageRightService;
import org.gesis.wts.service.UserAgencyRoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.service.dto.UserAgencyDTO;
import org.gesis.wts.ui.view.admin.form.UserAgencyForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.data.Binder;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.Language;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.view.DetailView;
import eu.cessda.cvmanager.ui.view.form.AgencyMemberForm;

public class DialogAgencyManageMember extends MWindow implements Translatable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(DialogAgencyManageMember.class);

	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;
	private final UserService userService;
	private final AgencyService agencyService;
	private final UserAgencyService userAgencyService;
	private final UserAgencyRoleService userAgencyRoleService;
	private final LanguageRightService languageRightService;
	private final Locale locale;
	
	private MCssLayout layout = new MCssLayout();
	private Grid<UserAgencyDTO> grid = new Grid<>(UserAgencyDTO.class);
	private MTextField filterText = new MTextField();
	private AgencyMemberForm form;
	private AgencyDTO agency;

	private Button storeCode = new Button("Save");

	public DialogAgencyManageMember(UIEventBus eventBus, AgencyDTO agency, UserService userService,
			AgencyService agencyService, UserAgencyService userAgencyService,
			UserAgencyRoleService userAgencyRoleService, LanguageRightService languageRightService,
			I18N i18n, Locale locale) {
		
		super( "Manage "  + agency.getName() + " members, roles and languages");
		
		this.eventBus = eventBus;
		this.agency = agency;
		this.userService = userService;
		this.agencyService = agencyService;
		this.userAgencyService = userAgencyService;
		this.userAgencyRoleService = userAgencyRoleService;
		this.languageRightService = languageRightService;
		this.i18n = i18n;
		this.locale = locale;
		
		this.form = new AgencyMemberForm( this, userAgencyService, userService, agencyService);
		
		initLayout();
	}
	
	private void initLayout() {
		MButton addBtn = new MButton(" + Add new member");
        addBtn.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-normal");
        addBtn.addClickListener(e -> {
            grid.asSingleSelect().clear();
            form.setUserLayoutVisible( true );
            form.setUserAgencyDTO(new UserAgencyDTO());
        });
        
        MCssLayout toolbar = new MCssLayout(addBtn);
        toolbar.withFullWidth();

        grid.setColumns("id", "firstName", "lastName", "agencyName");

        HorizontalLayout main = new HorizontalLayout(grid, form);
        main.setSizeFull();
        grid.setSizeFull();
        main.setExpandRatio(grid, 1);
        // fetch list of Customers from service and assign it to Grid
        updateList();

        form.setVisible(false);
        
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                form.setVisible(false);
            } else {
            	form.setUserLayoutVisible( false );
                form.setUserAgencyDTO(event.getValue());
            }
        });
		
		layout
			.withFullSize()
			.withStyleName("dialog-content")
			.add( 
					toolbar, main
			);

		this
			.withHeight("650px")
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);

		updateMessageStrings(locale);
	}

	public void updateList() {
		List<UserAgencyDTO> agencyDTOs = userAgencyService.findAll( filterText.getValue());
		grid.setItems(agencyDTOs);
	}

	public Grid<UserAgencyDTO> getGrid() {
		return grid;
	}

	public void setGrid(Grid<UserAgencyDTO> grid) {
		this.grid = grid;
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
}
