package eu.cessda.cvmanager.ui.view.admin.form;


import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ResolverService;
import eu.cessda.cvmanager.service.dto.ResolverDTO;
import eu.cessda.cvmanager.ui.view.admin.layout.ManageResolverLayout;

public class ResolverForm extends FormLayout {

	private static final long serialVersionUID = 1L;
	private MTextField resolverURI = new MTextField("Resolver URI");
    private MTextField resourceURL = new MTextField("Resource URL");
    private MTextField resourceID = new MTextField("Resource ID");
    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete");
    
    private final ResolverService resolverService;
    private ResolverDTO resolverDTO;
    private ManageResolverLayout manageResolverLayout;
    private Binder<ResolverDTO> binder = new Binder<>(ResolverDTO.class);

    public ResolverForm(
    		ManageResolverLayout manageResolverLayout, 
    		ResolverService resolverService) {
        this.manageResolverLayout = manageResolverLayout;
        this.resolverService = resolverService;
                
        resolverURI.withFullWidth();
        resourceURL.withFullWidth();
        resourceID.withFullWidth();

        setSizeUndefined();
        setWidth("500px");
        MCssLayout buttons = new MCssLayout(save, cancel, delete);
        buttons.withFullWidth();
        
        addComponents(resolverURI, resourceURL, resourceID, buttons);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);
        
        delete.addStyleNames(ValoTheme.BUTTON_DANGER, "pull-right");

        binder.bindInstanceFields(this);

        save.addClickListener(e -> this.save());
        cancel.addClickListener( e -> this.setVisible( false ));
        delete.addClickListener(e -> this.delete());
    }

    public void setResolverDTO(ResolverDTO resolverDTO) {
        this.resolverDTO = resolverDTO;
        binder.setBean(resolverDTO);
        
        // Show delete button for only customers already in the database
        delete.setVisible(resolverDTO.isPersisted());
        setVisible(true);
        resolverURI.selectAll();
    }

    private void delete() {
    	ConfirmDialog.show( this.getUI(), "Confirm",
			"Are you sure you want to delete \"" + resolverDTO.getResolverURI() + "\"?", "yes",
			"cancel",
				dialog -> {
					if( dialog.isConfirmed() ) {
						resolverService.delete(resolverDTO.getId());
				        manageResolverLayout.updateList();
				        setVisible(false);
					}
				}
			);
    	
    }

    private void save() {
    	resolverService.save(resolverDTO);
        manageResolverLayout.updateList();
        setVisible(false);
    }
  

}
