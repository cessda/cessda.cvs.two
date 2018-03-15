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
	private MVerticalLayout vLayout = new MVerticalLayout();
	private MCssLayout languageLayout = new MCssLayout();
	private MCssLayout titleLayout = new MCssLayout();
	private MButton enTitle = new MButton();
	private MLabel desc = new MLabel();
	private MLabel version = new MLabel();

	private MLabel conceptList = new MLabel();
	private Image logo;

	private transient ConfigurationService configService;

	public AgencyGridComponent(AgencyView agencyView, AgencyDTO agencyDto, ConfigurationService configService, String keyword) {
		this.agencyView = agencyView;
		this.agency = agencyDto;
		this.configService = configService;
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
		});
		desc.withContentMode(ContentMode.HTML).withFullWidth();
		desc.setValue( agency.getDescription() );
		version.withContentMode(ContentMode.HTML);
		conceptList.withContentMode(ContentMode.HTML);

		languageLayout.withFullWidth();

		titleLayout.withFullWidth().add(enTitle);

		vLayout.withMargin(false).withFullWidth().add(languageLayout, titleLayout, desc, version, conceptList);

		Resource res = new ThemeResource("img/noimage.png");
		if( agency.getLogoPath() != null && !agency.getLogoPath().isEmpty())
			res = new ThemeResource(agency.getLogoPath());

		logo = new Image(null, res);
		logo.setWidth("100");

		hLayout.withFullWidth().add(logo, vLayout).withExpand(logo, 0.13f).withExpand(vLayout, 0.87f);

		container.withStyleName("itemcontainer").withFullWidth().add(hLayout);
	}

	public Image getLogo() {
		return logo;
	}

	public void setLogo(Image logo) {
		this.logo = logo;
	}

	public MCssLayout getContainer() {
		return container;
	}

	public void setContainer(MCssLayout container) {
		this.container = container;
	}

	private void applyButtonStyle(Button pressedButton) {

		Iterator<Component> iterate = languageLayout.iterator();
		while (iterate.hasNext()) {
			Component c = iterate.next();
			if (c instanceof Button) {
				((Button) c).removeStyleName("button-pressed");
			}
		}
		pressedButton.addStyleName("button-pressed");
	}

}
