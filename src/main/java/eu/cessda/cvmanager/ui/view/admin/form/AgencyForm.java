package eu.cessda.cvmanager.ui.view.admin.form;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.AgencyService;
import eu.cessda.cvmanager.service.dto.AgencyDTO;
import eu.cessda.cvmanager.ui.view.admin.ManageAgencyView;

public class AgencyForm extends FormLayout {

    private TextField name = new TextField("Name");
    private TextField description = new TextField("Description");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private final AgencyService agencyService;
    private AgencyDTO agencyDTO;
    private ManageAgencyView manageAgencyView;
    private Binder<AgencyDTO> binder = new Binder<>(AgencyDTO.class);

    public AgencyForm(ManageAgencyView manageAgencyView, AgencyService agencyService) {
        this.manageAgencyView = manageAgencyView;
        this.agencyService = agencyService;

        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        addComponents(name, description, buttons);

        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        binder.bindInstanceFields(this);

        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());
    }

    public void setAgencyDTO(AgencyDTO agencyDTO) {
        this.agencyDTO = agencyDTO;
        binder.setBean(agencyDTO);

        // Show delete button for only customers already in the database
        delete.setVisible(agencyDTO.isPersisted());
        setVisible(true);
        name.selectAll();
    }

    private void delete() {
    	agencyService.delete(agencyDTO.getId());
        manageAgencyView.updateList();
        setVisible(false);
    }

    private void save() {
    	agencyService.save(agencyDTO);
        manageAgencyView.updateList();
        setVisible(false);
    }

}
