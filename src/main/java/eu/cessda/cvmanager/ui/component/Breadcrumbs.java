package eu.cessda.cvmanager.ui.component;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomComponent;

public class Breadcrumbs extends CustomComponent {

	private static final long serialVersionUID = -746842814675835328L;

	final static Logger log = LoggerFactory.getLogger(Breadcrumbs.class);

	private MCssLayout container = new MCssLayout();
	private MCssLayout innerContainer = new MCssLayout();
	private Map<String,String> linkMap = new LinkedHashMap<>();
	private String baseUrl;
	private String basePageName;
	private String basePageUrl;
	
	public Breadcrumbs() {
		setCompositionRoot(container);
		initLayout();
	}

	private void initLayout() {
		container
			.withStyleName( "breadcrumb" )
			.add( innerContainer );
		
		innerContainer
			.withStyleName( "container" );
	}
	
	public Breadcrumbs clear() {
		innerContainer.removeAllComponents();
		linkMap.clear();
		return this;
	}
	
	public Breadcrumbs addItem(String name, String url) {
		linkMap.put(name, url);
		return this;
	}
	
	public Breadcrumbs build() {
		
		innerContainer
			.add( new MLabel( "<a href='" + baseUrl + "/#!" + basePageUrl + "'>" + basePageName + "</a> " ).withContentMode( ContentMode.HTML ).withStyleName("pull-left","bc-first"));
		
		linkMap.forEach( (k, v) -> {
			if( v != null )
				innerContainer.add( new MLabel( "&nbsp;›&nbsp;<a href='" + baseUrl + "/#!" + v + "'>" + k + "</a>" ).withContentMode( ContentMode.HTML ).withStyleName("pull-left"));
			else
				innerContainer.add( new MLabel( "&nbsp;›&nbsp;" + k ).withContentMode( ContentMode.HTML ).withStyleName("pull-left"));
		});
		
		return this;
	}

	public Map<String, String> getLinkMap() {
		return linkMap;
	}

	public void setLinkMap(Map<String, String> linkMap) {
		this.linkMap = linkMap;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public Breadcrumbs withBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBasePageName() {
		return basePageName;
	}

	public void setBasePageName(String basePageName) {
		this.basePageName = basePageName;
	}
	
	public Breadcrumbs withBasePageName(String basePageName) {
		this.basePageName = basePageName;
		return this;
	}

	public String getBasePageUrl() {
		return basePageUrl;
	}

	public void setBasePageUrl(String basePageUrl) {
		this.basePageUrl = basePageUrl;
	}

	public Breadcrumbs withBasePageUrl(String basePageUrl) {
		this.basePageUrl = basePageUrl;
		return this;
	}
}
