package eu.cessda.cvmanager.ui.view.admin.layout;

import java.util.Locale;

import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ResolverService;
import eu.cessda.cvmanager.service.dto.ResolverDTO;
import eu.cessda.cvmanager.ui.view.admin.component.ResolverGrid;
import eu.cessda.cvmanager.ui.view.admin.form.ResolverForm;

public class ManageResolverLayout extends MCssLayout implements Translatable {
	private static final long serialVersionUID = 1L;
	
	private Locale locale = UI.getCurrent().getLocale();
	
	// autowired
	private final ResolverService resolverService;

	// components
	private MLabel pageTitle = new MLabel();
	private ResolverGrid resolverGrid;
	private ResolverForm form;

	public ManageResolverLayout(I18N i18n, ResolverService resolverService ) {
		super();
		this.resolverService = resolverService;
		this.resolverGrid = new ResolverGrid();
		this.form = new ResolverForm(this, resolverService );
		init();
	}

	private void init() {
		pageTitle
			.withContentMode(ContentMode.HTML)
			.withValue("<h2>Manage Resolver</h2>");
		
	    MButton addBtn = new MButton("+ Add new resolver");
	    addBtn.withStyleName( ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL, "pull-right", "btn-spacing-normal");
	    addBtn.addClickListener(e -> {
	        resolverGrid.asSingleSelect().clear();
	        form.setResolverDTO(new ResolverDTO());
	    });
	    
	    MCssLayout toolbar = new MCssLayout(addBtn);
	    toolbar.withFullWidth();
	
	    resolverGrid
	    	.withFullWidth()
	    	.withHeight("500px");
	    
	    MHorizontalLayout mainLayout = new MHorizontalLayout(resolverGrid, form);
	    mainLayout
	    	.withFullWidth()
	    	.withExpand(resolverGrid, 1);
		    
	    updateList();
		
	    form.setVisible(false);
	
	    resolverGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                form.setVisible(false);
            } else {
                form.setResolverDTO(event.getValue());
            }
        });
	    
	    this
			.withFullSize()
			.addComponents(pageTitle, toolbar, mainLayout);
	}
	
	public void updateList() {
		resolverGrid.setDataProvider( new ListDataProvider<ResolverDTO>( resolverService.findAll() ));
	}
	
	@Override
	public void updateMessageStrings(Locale locale) {
		
	}

}
