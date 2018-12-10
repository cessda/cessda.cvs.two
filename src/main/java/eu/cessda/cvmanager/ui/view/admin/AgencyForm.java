package eu.cessda.cvmanager.ui.view.admin;

import java.util.ArrayList;
import java.util.List;

import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.dto.LicenceDTO;
import eu.cessda.cvmanager.ui.component.UploadAgencyLogo;
import eu.cessda.cvmanager.ui.component.UploadExample;

public class AgencyForm extends FormLayout {

	private static final long serialVersionUID = 1L;
	private MTextField name = new MTextField("Name");
    private MTextField description = new MTextField("Description");
    private MTextField link = new MTextField("Link").withPlaceholder("e.g. http://www.example.com/");
    private MTextField uri = new MTextField("Default CV URI").withPlaceholder("e.g. http://www.ddialliance.org/Specification/DDI-CV/");
    private MTextField canonicalUri = new MTextField("CV Canonical URI").withPlaceholder("e.g. urn:ddi-cv:");
    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete");
    
    private final AgencyService agencyService;
    private AgencyDTO agencyDTO;
    private ManageAgencyLayout manageAgencyLayout;
    private Binder<AgencyDTO> binder = new Binder<>(AgencyDTO.class);
    
    private ComboBox<LicenceDTO> licensesCb = new ComboBox<>( "Licence" );
    private MLabel licensePreview = new MLabel().withContentMode( ContentMode.HTML);
    private List<LicenceDTO> licenses;
    private UploadAgencyLogo uploadAgencyLogo = new UploadAgencyLogo();
    private String agencyNameTemp;

    public AgencyForm(ManageAgencyLayout manageAgencyLayout, AgencyService agencyService, List<LicenceDTO> licenses) {
        this.manageAgencyLayout = manageAgencyLayout;
        this.agencyService = agencyService;
        this.licenses = licenses;
//        uploadExample.init("advanced");
        
        licensesCb.setItems( licenses );
        licensesCb.setWidth( "100%" );
        licensesCb.setTextInputAllowed( false );
        licensesCb.setItemCaptionGenerator( item -> item.getId() + " " + item.getName());
        licensesCb.addValueChangeListener( e -> {
        	if( e.getValue() != null)
        		setLicensePreview( e.getValue());
        	else
        		licensePreview.setValue("");
        });
                
        name.withFullWidth().setReadOnly( true );
        description.withFullWidth();
        link.withFullWidth();
        uri.withFullWidth();
        canonicalUri.withFullWidth();

        setSizeUndefined();
        setWidth("500px");
        MCssLayout buttons = new MCssLayout(save, cancel, delete);
        buttons.withFullWidth();
        
	    uploadAgencyLogo.setCaption("Logo");
	    
        addComponents(name, description, link, uri, canonicalUri, licensesCb, licensePreview, uploadAgencyLogo, buttons);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);
        
        delete.addStyleNames(ValoTheme.BUTTON_DANGER, "pull-right");
        

        binder.bindInstanceFields(this);

        save.addClickListener(e -> this.save());
        cancel.addClickListener( e -> this.setVisible( false ));
        delete.addClickListener(e -> this.delete());
    }

    public void setAgencyDTO(AgencyDTO agencyDTO) {
        this.agencyDTO = agencyDTO;
        agencyNameTemp = agencyDTO.getName();
        binder.setBean(agencyDTO);
        
        uploadAgencyLogo.init(agencyDTO);
        
        JavaScript.getCurrent().execute("document.getElementsByClassName('gwt-FileUpload')[0].setAttribute('accept', 'image/*')");
        
        licensesCb.setValue( null );
        
        if( agencyDTO.getLicenseId() != null) 
        	 licenses.stream().filter( p -> p.getId().equals( agencyDTO.getLicenseId())).findFirst().ifPresent( 
        			 license -> {
        				 licensesCb.setValue(license);
        				 setLicensePreview(license);
        			 });
        

        // Show delete button for only customers already in the database
        delete.setVisible(agencyDTO.isPersisted());
        setVisible(true);
        name.selectAll();
    }

    private void delete() {
    	ConfirmDialog.show( this.getUI(), "Confirm",
			"Are you sure you want to delete \"" + agencyDTO.getName() + "\", all related CVs will be deleted as well?", "yes",
			"cancel",
				dialog -> {
					if( dialog.isConfirmed() ) {
						agencyService.delete(agencyDTO.getId());
				        manageAgencyLayout.updateList();
				        setVisible(false);
					}
				}
			);
    	
    }

    private void save() {
    	if( licensesCb.getValue() != null)
    		agencyDTO.setLicenseId( licensesCb.getValue().getId() );
    	else
    		agencyDTO.setLicenseId( null );
    	agencyService.save(agencyDTO);
        manageAgencyLayout.updateList();
        
        // TODO: Update CV if agency name changed
        
        setVisible(false);
    }
    
    private void setLicensePreview( LicenceDTO licenseDto ) {
    	if( licenseDto.isPersisted())
    		licensePreview.setValue( "<p>"
    			+ "<img style=\"max-width:120px\" alt=\"" + licenseDto.getName() + "\" style=\"border-width:0\" src=\"" + licenseDto.getLogoLink() + "\">"
    			+ "&nbsp;&nbsp;&nbsp;<span>This work is licensed under a "
    			+ "<a rel=\"license\" href=\"" + licenseDto.getLink() + "\">" + licenseDto.getName() + "</a>.</span>"
    			+ "</p>" );
    	else
    		licensePreview.setValue( "" );
    }

}
