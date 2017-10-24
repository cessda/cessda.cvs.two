package eu.cessda.cvmanager.ui.component;

import java.util.List;

import org.gesis.stardat.entity.CVConcept;
import org.gesis.stardat.entity.CVEditor;
import org.gesis.stardat.entity.CVScheme;
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

import eu.cessda.cvmanager.service.ConfigurationService;
import eu.cessda.cvmanager.ui.view.DetailView;

public class CvSchemeComponent  extends CustomComponent{
	private CVScheme cvScheme;
	private List<CVConcept> cvConcepts;
	
	private static final long serialVersionUID = 4932510587869607326L;
	
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
	private ConfigurationService configService;
	
	public CvSchemeComponent(CVScheme cvScheme, ConfigurationService configService) {
		this.cvScheme = cvScheme;
		this.configService = configService;
		
		setCompositionRoot(container);
		initLayout();
	}
	
	private void initLayout() {
		enTitle
			.withStyleName( "marginright20" )
			.withContentMode( ContentMode.HTML );
			//.addContextClickListener( e-> UI.getCurrent().getNavigator().navigateTo(DetailView.VIEW_NAME) );
		olTitle.withContentMode( ContentMode.HTML );
		//.addContextClickListener( e-> UI.getCurrent().getNavigator().navigateTo(DetailView.VIEW_NAME) );
		desc
			.withContentMode( ContentMode.HTML )
			.withFullWidth();
		version.withContentMode( ContentMode.HTML );
		
		languageLayout.withFullWidth();
		cvScheme.getLanguagesByTitle().forEach( item -> {
			MButton langBUtton = new MButton( item.toUpperCase() );
			langBUtton
				.withStyleName( "langbutton" )
				.addClickListener( e -> setContent( e.getButton().getCaption().toLowerCase() ));
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
		
		Resource res =  new ThemeResource( "img/ddi-logo-r.png");
		if( cvScheme.getOwnerAgency() != null && !cvScheme.getOwnerAgency().isEmpty()) {
			CVEditor agency = cvScheme.getOwnerAgency().get(0);
			if( agency.getLogoPath() != null)
				res = new ThemeResource( agency.getLogoPath() );
		}

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
		setContent( "en" );
	}
	
	public Image getLogo() {
		return logo;
	}

	public void setLogo(Image logo) {
		this.logo = logo;
	}

	public List<CVConcept> getCvElements() {
		return cvConcepts;
	}
	public void setCvElements(List<CVConcept> cvElements) {
		this.cvConcepts = cvElements;
	}
	
	public MCssLayout getContainer() {
		return container;
	}
	public void setContainer(MCssLayout container) {
		this.container = container;
	}
	
	private void setContent( String language ) {
		enTitle.setValue( "<a href='" + configService.getServerContextPath()
				+ "/#!" + DetailView.VIEW_NAME+ "/" + cvScheme.getContainerId() + "'>" + cvScheme.getCode() + "</a>");
		olTitle.setValue(  "<a href='" + configService.getServerContextPath()
		+ "/#!" + DetailView.VIEW_NAME+ "/" + cvScheme.getContainerId() + "'>"  + cvScheme.getTitleByLanguage( language )  + "</a>" );
		desc.setValue( cvScheme.getDescriptionByLanguage( language ) );
		version.setValue( "Version: " + cvScheme.getVersion().getPublicationVersion() +(language.equals("en")? "": "_"+language) + " <a href='#'>Download</a>");
	}
	
}
