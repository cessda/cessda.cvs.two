package eu.cessda.cvmanager.ui.component;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.ui.view.PublicationDetailsView;

public class ResponsiveBlock extends CustomComponent implements Translatable {

	private static final long serialVersionUID = -2157226640156714417L;
	final static Logger log = LoggerFactory.getLogger(ResponsiveBlock.class);
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();

	private MCssLayout container = new MCssLayout();
	private MCssLayout header = new MCssLayout();
	private MCssLayout innerContainer = new MCssLayout();
	private MLabel title = new MLabel();
	private MButton showBlock = new MButton( "Show" );
	private MButton hideBlock = new MButton( VaadinIcons.CLOSE);
	private String titleHeader;
	private String showHeader;
	
	public ResponsiveBlock( String titleHeader, String showHeader, I18N i18n ) {
		this.titleHeader = titleHeader;
		this.showHeader = showHeader;
		this.i18n = i18n;
		setCompositionRoot(container);
		initLayout();
	}

	private void initLayout() {
		
		title
			.withValue( i18n.get(titleHeader))
			.withStyleName("title-block")
			.withContentMode( ContentMode.HTML );
		
		header
			.withStyleName("header-block")
			.add(
				title,
				hideBlock
			);
		
		showBlock
			.withCaption( i18n.get( showHeader ))
			.withStyleName("show-block" ,"filter-button", "button-responsive")
			.addClickListener( e -> {
				e.getButton().removeStyleName("button-responsive");
				hideBlock.addStyleName("button-responsive");
				innerContainer.removeStyleName("block-responsive");
			});
		
		hideBlock
			.withStyleName("block-close-button")
			.addClickListener( e -> {
				e.getButton().removeStyleName("button-responsive");
				showBlock.addStyleName("button-responsive");
				innerContainer.addStyleName("block-responsive");
			});
		
		innerContainer
			.withStyleName( "panel-container", "block-responsive" )
			.add( 
				header 
			);
		
		container
			.withFullWidth()
			.add( 
				showBlock,
				innerContainer
			);
	}
	
	public void setVisible(boolean visible) {
		container.setVisible(visible);
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		// TODO Auto-generated method stub
		
	}

	public MCssLayout getInnerContainer() {
		return innerContainer;
	}

	public void setInnerContainer(MCssLayout innerContainer) {
		this.innerContainer = innerContainer;
	}
	
}
