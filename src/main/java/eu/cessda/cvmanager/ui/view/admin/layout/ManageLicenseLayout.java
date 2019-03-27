package eu.cessda.cvmanager.ui.view.admin.layout;

import java.util.List;
import java.util.Locale;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.ui.view.admin.form.LicenseForm;



public class ManageLicenseLayout extends MCssLayout implements Translatable {
	private static final long serialVersionUID = 1L;
	
	private Locale locale = UI.getCurrent().getLocale();
	
	// autowired
	private final LicenceService licenceService;

	// components
	private MLabel pageTitle = new MLabel();
	private Grid<LicenceDTO> grid = new Grid<>(LicenceDTO.class);
	private MTextField filterText = new MTextField();
	private LicenseForm form;

	public ManageLicenseLayout(I18N i18n, LicenceService licenceService) {
		super();
		this.licenceService = licenceService;
		this.form = new LicenseForm(this, this.licenceService);
		init();
	}

	private void init() {
		pageTitle
			.withContentMode(ContentMode.HTML)
			.withValue("<h2>Manage Licence</h2>");
		
		filterText.withPlaceholder("filter by name / licensename ...")
			.withWidth("300px")
			.withValueChangeMode(ValueChangeMode.LAZY)
			.addValueChangeListener(e -> updateList());

	    Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
	    clearFilterTextBtn.setDescription("Clear the current filter");
	    clearFilterTextBtn.addClickListener(e -> filterText.clear());
	    
	    CssLayout filtering = new CssLayout();
	    filtering.addComponents(filterText, clearFilterTextBtn);
	    filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
	    
	    MButton addBtn = new MButton("+ Add new license");
	    addBtn.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-normal");
	    addBtn.addClickListener(e -> {
	        grid.asSingleSelect().clear();
	        form.setLicenseDTO(new LicenceDTO());
	    });
	    
	    MCssLayout toolbar = new MCssLayout(filtering, addBtn);
	    toolbar.withFullWidth();
	
	    grid.setColumns("name", "link", "logoLink");
	    
	    HorizontalLayout main = new HorizontalLayout(grid, form);
	    main.setSizeFull();
	    grid.setSizeFull();
	    grid.setHeight("500px");
	    main.setExpandRatio(grid, 1);
		    
	    updateList();
		
	    form.setVisible(false);
	
	    grid.asSingleSelect().addValueChangeListener(event -> {
	        if (event.getValue() == null) {
	            form.setVisible(false);
	        } else {
	            form.setLicenseDTO(event.getValue());
	        }
	    });
	    
	    this
			.withFullSize()
			.addComponents(pageTitle, toolbar, main);
	}
	
	public void updateList() {
		List<LicenceDTO> licenceDTOs = licenceService.findAll();
		grid.setItems(licenceDTOs);
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		
	}

}
