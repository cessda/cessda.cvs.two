package eu.cessda.cvmanager.ui.view.window;

import java.util.List;
import java.util.Locale;

import org.gesis.wts.service.AgencyService;
import org.gesis.wts.service.RoleService;
import org.gesis.wts.service.UserAgencyService;
import org.gesis.wts.service.UserService;
import org.gesis.wts.service.dto.AgencyDTO;
import org.gesis.wts.service.dto.UserAgencyDTO;
import org.gesis.wts.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.LicenceService;
import eu.cessda.cvmanager.service.MetadataFieldService;
import eu.cessda.cvmanager.service.MetadataValueService;
import eu.cessda.cvmanager.ui.component.AgencyMemberGridComponent;
import eu.cessda.cvmanager.ui.view.admin.AgencyForm;
import eu.cessda.cvmanager.ui.view.admin.AgencyManageProfile;
import eu.cessda.cvmanager.ui.view.form.AgencyMemberForm;

public class DialogAgencyManageProfile extends MWindow implements Translatable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(DialogAgencyManageProfile.class);

	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;
	private final AgencyService agencyService;
	private final LicenceService licenceService;
	private final Locale locale;
	
	
	private MFormLayout layout = new MFormLayout();
	private AgencyDTO agency;
	private AgencyManageProfile agencyManageProfile;

	public DialogAgencyManageProfile(UIEventBus eventBus, AgencyDTO agency, AgencyService agencyService, 
			LicenceService licenceService, I18N i18n, Locale locale) {
		
		super( "Manage "  + agency.getName() + " profile and metadata");
		
		this.eventBus = eventBus;
		this.agency = agency;
		this.agencyService = agencyService;
		this.licenceService =licenceService;
		this.i18n = i18n;
		this.locale = locale;
		
		agencyManageProfile = new AgencyManageProfile( this, agencyService, licenceService.findAll());
		agencyManageProfile.setAgencyDTO(agency);
		 
		layout
			.withHeight("96%")
			.withWidth("100%")
			.withStyleName("dialog-content")
			.addComponents( 
				agencyManageProfile
			);
		this
			.withHeight("650px")
			.withWidth("1200px")
			.withModal( true )
			.withContent(layout);

		updateMessageStrings(locale);
	}
	
	private void initLayout() {

	}

	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
}
