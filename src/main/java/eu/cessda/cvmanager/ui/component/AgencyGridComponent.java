package eu.cessda.cvmanager.ui.component;

import java.util.Iterator;

import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.themes.ValoTheme;

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.ui.layout.AgencySearchLayout;
import eu.cessda.cvmanager.ui.view.AgencyView;
import eu.cessda.cvmanager.ui.view.AgencyView.ViewMode;

public class AgencyGridComponent extends CustomComponent {

	final static Logger log = LoggerFactory.getLogger(AgencyGridComponent.class);

	private AgencyDTO agency;
	private String keyword;
	private AgencyView agencyView;

	private static final long serialVersionUID = 4932510587869607326L;

	private MCssLayout container = new MCssLayout();
	private MHorizontalLayout hLayout = new MHorizontalLayout();
	private MCssLayout vLayout = new MCssLayout();
	private MCssLayout titleLayout = new MCssLayout();
	private MButton enTitle = new MButton();
	private MLabel desc = new MLabel();
	private MLabel version = new MLabel();

	private MLabel conceptList = new MLabel();

	public AgencyGridComponent(AgencyView agencyView, AgencyDTO agencyDto, ConfigurationService configService, String keyword) {
		this.agencyView = agencyView;
		this.agency = agencyDto;
		this.keyword = keyword;
		
		setCompositionRoot(container);
		initLayout();
	}

	private void initLayout() {
		enTitle.addStyleNames(ValoTheme.BUTTON_LINK, "bold-text");
		enTitle.setCaption( agency.getName() );
		enTitle.addClickListener( e -> {
			agencyView.setAgency( agency, ViewMode.DETAIL);
			agencyView.getaDetailLayout().setAgency(agency);
			
			String url = Page.getCurrent().getUriFragment().toString();
			url = url.split(AgencyView.VIEW_NAME)[0] + AgencyView.VIEW_NAME + "/" + agency.getName();
			Page.getCurrent().setUriFragment(url, true);
		});
		desc.withContentMode(ContentMode.HTML).withFullWidth();
		desc.setValue( agency.getDescription() );
		version.withContentMode(ContentMode.HTML);
		conceptList.withContentMode(ContentMode.HTML);

		titleLayout.withFullWidth().add(enTitle);

		vLayout
			.withFullWidth()
			.add(titleLayout, desc, version, conceptList);

		MLabel logoLabel = new MLabel()
				.withContentMode( ContentMode.HTML )
				.withWidth("120px");
		
		if( agency.getLogo() != null && !agency.getLogo().isEmpty())
			logoLabel.setValue(  "<img style=\"max-width:120px;max-height:80px\" alt=\"" + agency.getName() + " logo\" src='" + agency.getLogo() + "'>");

		hLayout.withFullWidth().add(logoLabel, vLayout).withExpand(vLayout, 1.0f);

		container
			.withStyleName("itemcontainer agency")
			.withFullWidth()
			.add(hLayout);
	}

	public MCssLayout getContainer() {
		return container;
	}

	public void setContainer(MCssLayout container) {
		this.container = container;
	}

}
