package eu.cessda.cvmanager.ui.view.admin;

import java.util.UUID;


import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.service.dto.LicenceDTO;

public class LicenseForm extends FormLayout {

	private static final long serialVersionUID = 1L;
	private MTextField name = new MTextField("Name");
    private MTextField link = new MTextField("Link");
    private MTextField logoLink = new MTextField("Logo Link");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    
    private MLabel preview = new MLabel().withContentMode( ContentMode.HTML);

    private final LicenceService licenceService;
	
    private LicenceDTO licenceDTO;
    private ManageLicenseLayout manageLicenseLayout;
    private Binder<LicenceDTO> binder = new Binder<>(LicenceDTO.class);

    public LicenseForm(ManageLicenseLayout manageAgencyLayout, LicenceService agencyService) {
        this.manageLicenseLayout = manageAgencyLayout;
        this.licenceService = agencyService;
        
        name.withFullWidth();
        link.withFullWidth();
        logoLink.withFullWidth();

        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        addComponents(name, link, logoLink, buttons, preview);
        setWidth("500px");

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        binder.bindInstanceFields(this);

        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());
    }

    public void setLicenseDTO(LicenceDTO licenceDTO) {
        this.licenceDTO = licenceDTO;
        binder.setBean(licenceDTO);
        
        setLicensePreview( licenceDTO );

        // Show delete button for only customers already in the database
        delete.setVisible(licenceDTO.isPersisted());
        setVisible(true);
        name.selectAll();
    }

    private void delete() {
    	ConfirmDialog.show( this.getUI(), "Confirm",
			"Are you sure you want to delete \"" + licenceDTO.getName() + "\"?", "yes",
			"cancel",
				dialog -> {
					if( dialog.isConfirmed() ) {
						licenceService.delete(licenceDTO.getId());
				        manageLicenseLayout.updateList();
				        setVisible(false);
					}
				}
			);
    
    }
    
    private void setLicensePreview( LicenceDTO licenseDto ) {
    	if( licenseDto.isPersisted())
    		preview.setValue( "<br/><h2>Preview</h2><p>"
    			+ "<img alt=\"" + licenseDto.getName() + "\" style=\"border-width:0\" src=\"" + licenseDto.getLogoLink() + "\">"
    			+ "<br/><span>This work is licensed under a "
    			+ "<a rel=\"license\" href=\"" + licenseDto.getLink() + "\">" + licenseDto.getName() + "</a>.</span>"
    			+ "</p>" );
    	else
    		preview.setValue( "" );
    }

    private void save() {
    	licenceService.save(licenceDTO);
        manageLicenseLayout.updateList();
        setVisible(false);
    }

	public LicenceDTO getLicenseDTO() {
		return licenceDTO;
	}

}
