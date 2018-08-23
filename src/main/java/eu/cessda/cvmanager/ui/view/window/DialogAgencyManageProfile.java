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
import org.vaadin.viritin.layouts.MWindow;

import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.MetadataFieldService;
import eu.cessda.cvmanager.service.MetadataValueService;
import eu.cessda.cvmanager.ui.component.AgencyMemberGridComponent;
import eu.cessda.cvmanager.ui.view.form.AgencyMemberForm;

public class DialogAgencyManageProfile extends MWindow implements Translatable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(DialogAgencyManageProfile.class);

	private final EventBus.UIEventBus eventBus;
	private final I18N i18n;
	private final AgencyService agencyService;
	private final MetadataFieldService metadataFieldService;
	private final MetadataValueService metadataValueService;
	private final Locale locale;
	
	private MCssLayout layout = new MCssLayout();
	private AgencyDTO agency;

	public DialogAgencyManageProfile(UIEventBus eventBus, AgencyDTO agency, AgencyService agencyService, 
			MetadataFieldService metadataFieldService, MetadataValueService metadataValueService,
			I18N i18n, Locale locale) {
		
		super( "Manage "  + agency.getName() + " profile and metadata");
		
		this.eventBus = eventBus;
		this.agency = agency;
		this.agencyService = agencyService;
		this.metadataFieldService = metadataFieldService;
		this.metadataValueService = metadataValueService;
		this.i18n = i18n;
		this.locale = locale;
		
		initLayout();
	}
	
	private void initLayout() {
		
	}

	public void updateList() {
	
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		
	}
}
