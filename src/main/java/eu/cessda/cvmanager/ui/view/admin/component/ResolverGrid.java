package eu.cessda.cvmanager.ui.view.admin.component;

import java.util.Locale;

import org.vaadin.spring.i18n.support.Translatable;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

import eu.cessda.cvmanager.service.dto.ResolverDTO;

public class ResolverGrid extends MGrid<ResolverDTO> implements Translatable{

	private static final long serialVersionUID = 1793974878866451841L;
	static private final String COLUMN_RESOURCE_ID = "resourceId";
	static private final String COLUMN_RESOURCE_URL = "resourceURL";
	static private final String COLUMN_RESOLVER_URI = "resolverURI";
		
	public ResolverGrid() {
		init();
	}
	
	private void init(){
		this
			.withFullSize()
			.withHeight("500px")
			.setSelectionMode( SelectionMode.SINGLE );
		
		this.removeAllColumns();
		
		this.addColumn( ResolverDTO::getResolverURI )
			.setCaption( "Resolver URI" )
			.setId( COLUMN_RESOLVER_URI );
		
		this.addColumn( ResolverDTO::getResourceURL )
			.setCaption( "Resource URL" )
			.setId( COLUMN_RESOURCE_URL );
		
		this.addColumn( ResolverDTO::getResourceId )
			.setCaption( "Resource ID" )
			.setId( COLUMN_RESOURCE_ID );
		
		setFilter();
	}
		
	@SuppressWarnings("unchecked")
	private void setFilter() {
		HeaderRow filterRow = this.appendHeaderRow();
		MTextField tfResolverUri = new MTextField();
		tfResolverUri
			.withPlaceholder("search...")
			.withFullWidth();
		
		MTextField tfResourceUrl = new MTextField();
		tfResourceUrl
			.withPlaceholder("search...")
			.withFullWidth();
				
		MTextField tfResourceId = new MTextField();
		tfResourceId
			.withPlaceholder("search...")
			.withFullWidth();
		
		HeaderCell resolverUriFilter = filterRow.getCell( COLUMN_RESOLVER_URI );
		HeaderCell resourceUrlFilter = filterRow.getCell( COLUMN_RESOURCE_URL );
		HeaderCell resourceIdFilter = filterRow.getCell( COLUMN_RESOURCE_ID );
		
		tfResolverUri.addValueChangeListener(e -> ((ListDataProvider<ResolverDTO>) this.getDataProvider()).setFilter(p ->
				p.getResolverURI() != null && p.getResolverURI().toLowerCase().contains( e.getValue().toLowerCase() ) ));
		tfResourceUrl.addValueChangeListener(e -> ((ListDataProvider<ResolverDTO>) this.getDataProvider()).setFilter(p ->
				p.getResourceURL() != null && p.getResourceURL().toLowerCase().contains( e.getValue().toLowerCase() ) ));
		tfResourceId.addValueChangeListener(e -> ((ListDataProvider<ResolverDTO>) this.getDataProvider()).setFilter(p ->
				p.getResourceId() != null && p.getResourceId().toLowerCase().contains( e.getValue().toLowerCase() ) ));
		
		resolverUriFilter.setComponent(tfResourceId);
		resourceUrlFilter.setComponent(tfResourceUrl);
		resourceIdFilter.setComponent(tfResolverUri);
	}

	@Override
	public void updateMessageStrings(Locale locale) {
		// TODO Auto-generated method stub
		
	}
}
