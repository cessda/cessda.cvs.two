package eu.cessda.cvmanager.ui.component;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomComponent;

public class ResponsiveBlock extends CustomComponent implements Translatable {

	private static final String VAADIN_STYLE_BLOCK_RESPONSIVE = "block-responsive";

	private static final String VAADIN_STYLE_BUTTON_RESPONSIVE = "button-responsive";

	private static final long serialVersionUID = -2157226640156714417L;
	
	static  final Logger log = LoggerFactory.getLogger(ResponsiveBlock.class);
	
	private transient I18N i18n;
		

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
			.withStyleName("show-block" ,"filter-button", VAADIN_STYLE_BUTTON_RESPONSIVE)
			.addClickListener( e -> {
				e.getButton().removeStyleName(VAADIN_STYLE_BUTTON_RESPONSIVE);
				hideBlock.addStyleName(VAADIN_STYLE_BUTTON_RESPONSIVE);
				innerContainer.removeStyleName(VAADIN_STYLE_BLOCK_RESPONSIVE);
			});
		
		hideBlock
			.withStyleName("block-close-button")
			.addClickListener( e -> {
				e.getButton().removeStyleName(VAADIN_STYLE_BUTTON_RESPONSIVE);
				showBlock.addStyleName(VAADIN_STYLE_BUTTON_RESPONSIVE);
				innerContainer.addStyleName(VAADIN_STYLE_BLOCK_RESPONSIVE);
			});
		
		innerContainer
			.withStyleName( "panel-container", VAADIN_STYLE_BLOCK_RESPONSIVE )
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
		// Do nothing because of no internationalized messages strings.
	}

	public MCssLayout getInnerContainer() {
		return innerContainer;
	}

	public void setInnerContainer(MCssLayout innerContainer) {
		this.innerContainer = innerContainer;
	}
	
}
