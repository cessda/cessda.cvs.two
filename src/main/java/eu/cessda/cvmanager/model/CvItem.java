package eu.cessda.cvmanager.model;

import java.util.List;

import org.objectweb.asm.Label;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;

import eu.cessda.cvmanager.ui.view.DetailView;

public class CvItem  extends CustomComponent{
	private static final long serialVersionUID = 4932510587869607326L;
	private String imagePath;
	private List<CvElement> cvElements;
	private MCssLayout container = new MCssLayout();
	private MHorizontalLayout hLayout = new MHorizontalLayout();
	private MVerticalLayout vLayout = new MVerticalLayout();
	private MCssLayout languageLayout = new MCssLayout();
	private MCssLayout titleLayout = new MCssLayout();
	private MLabel enTitle = new MLabel();
	private MLabel olTitle = new MLabel();
	private MLabel desc = new MLabel();
	private MLabel version = new MLabel();
	private Image logo;
	
	public CvItem(String imagePath, List<CvElement> cvElements) {
		this.imagePath = imagePath;
		this.cvElements = cvElements;
		setCompositionRoot(container);
		initLayout();
	}
	
	private void initLayout() {
		enTitle
			.withStyleName( "marginright20" )
			.withContentMode( ContentMode.HTML )
			.addContextClickListener( e-> UI.getCurrent().getNavigator().navigateTo(DetailView.VIEW_NAME) );
		olTitle.withContentMode( ContentMode.HTML )
		.addContextClickListener( e-> UI.getCurrent().getNavigator().navigateTo(DetailView.VIEW_NAME) );
		desc
			.withContentMode( ContentMode.HTML )
			.withFullWidth();
		version.withContentMode( ContentMode.HTML );
		
		languageLayout.withFullWidth();
		cvElements.forEach( item -> {
			MButton langBUtton = new MButton( item.getLanguageIso() );
			langBUtton
				.withStyleName( "langbutton" )
				.addClickListener( e -> setContent( e.getButton().getCaption() ));
			languageLayout.add(
				langBUtton
			);
		});
		
		titleLayout
			.withFullWidth()
			.add(
					enTitle,
					olTitle
				);
		
		vLayout
			.withMargin( false )
			.withFullWidth()
			.add( 
				languageLayout,
				titleLayout,
				desc,
				version
			);
		
		Resource res = new ThemeResource( imagePath );
		logo = new Image( null, res );
		logo.setWidth( "100" );
		
		hLayout
			.withFullWidth()
			.add( 
				logo, 
				vLayout
			)
			.withExpand(logo, 0.13f)
			.withExpand(vLayout, 0.87f);
			
		
		container
			.withStyleName("itemcontainer")
			.withFullWidth()
			.add( hLayout );
		//Initial
		setContent( "EN" );
	}
	
	public Image getLogo() {
		return logo;
	}

	public void setLogo(Image logo) {
		this.logo = logo;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public List<CvElement> getCvElements() {
		return cvElements;
	}
	public void setCvElements(List<CvElement> cvElements) {
		this.cvElements = cvElements;
	}
	
	public CvElement getCvElementByLanguage( String langIso ) {
		return cvElements.stream()
			.filter( item -> item.getLanguageIso().equals(langIso) )
			.findFirst().get();
	}
	
	public MCssLayout getContainer() {
		return container;
	}
	public void setContainer(MCssLayout container) {
		this.container = container;
	}
	
	private void setContent( String language ) {
		enTitle.setValue( "<a href='#'>" + getCvElementByLanguage("EN").getTitle() + "</a>");
		CvElement cvElem = getCvElementByLanguage( language );
		olTitle.setValue(  "<a href='#'>" + cvElem.getTitle() + "</a>" );
		desc.setValue( cvElem.getDescription() );
		version.setValue( "Version: " +cvElem.getVersion() +(language.equals("EN")? "": "_"+language) + " <a href='#'>Download</a>");
	}
	
}
