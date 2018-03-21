package eu.cessda.cvmanager.ui.view.window;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.gesis.stardat.ddiflatdb.client.DDIStore;
import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVScheme;
import org.gesis.wts.domain.User;
import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.service.dto.UserAgencyDTO;
import org.gesis.wts.service.dto.UserDTO;
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
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.Language;
import eu.cessda.cvmanager.event.CvManagerEvent;
import eu.cessda.cvmanager.event.CvManagerEvent.EventType;
import eu.cessda.cvmanager.service.CvManagerService;
import eu.cessda.cvmanager.ui.component.AgencyGridComponent;
import eu.cessda.cvmanager.ui.component.AgencyMemberGridComponent;
import eu.cessda.cvmanager.ui.view.DetailView;
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
	private final Locale locale;
	
	private MCssLayout layout = new MCssLayout();
	private Grid<UserDTO> grid = new Grid<>(UserDTO.class);
	private MTextField filterText = new MTextField();
	private AgencyMemberForm form;
	private AgencyDTO agency;

	public DialogAgencyManageMember(UIEventBus eventBus, AgencyDTO agency, UserService userService,
			RoleService roleService, AgencyService agencyService, UserAgencyService userAgencyService,
			I18N i18n, Locale locale) {
		
		super( "Manage "  + agency.getName() + " members, roles and languages");
		
		this.eventBus = eventBus;
		this.agency = agency;
		this.userService = userService;
		this.roleService = roleService;
		this.agencyService = agencyService;
		this.userAgencyService = userAgencyService;
		this.i18n = i18n;
		this.locale = locale;
		
		this.form = new AgencyMemberForm( this, userAgencyService, userService, agencyService);
		
		initLayout();
	}
	
	private void initLayout() {
		MButton addBtn = new MButton(" + Add new member");
        addBtn.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-normal");
        addBtn.addClickListener(e -> {
            form.setUserLayoutVisible( true );
            
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
			.withWidth("1024px")
			.withModal( true )
			.withContent(layout);

		updateMessageStrings(locale);
	}

	public void updateList() {
		// first get all useragency member
		List<UserDTO> members = userService.findAllByAgencyId( agency.getId() );
		
		grid.setItems( members );
		
		grid.removeAllColumns();

		grid.addColumn( member -> {
			return new AgencyMemberGridComponent( this, member, agency, 
					userAgencyService.findByUser( member.getId() ),
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

	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
}
