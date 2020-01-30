package eu.cessda.cvmanager.ui.view.publication;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import eu.cessda.cvmanager.ui.component.ResponsiveBlock;
import eu.cessda.cvmanager.ui.view.EditorSearchView;
import org.gesis.wts.domain.enumeration.Language;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MCheckBox;
import org.vaadin.viritin.layouts.MCssLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FiltersLayout extends ResponsiveBlock{

	private static final long serialVersionUID = -3119422665592890019L;

	@FunctionalInterface
	public interface FilterListener
	{
		void filterSelected( String field, List<String> activeFilter );
	}

	// later change with generic
	private DiscoveryView discoveryView;
	public static final String AGENCY_AGG = "agencyName";
	public static final String LANGS_AGG = "languages";
	public static final String LANGS_PUB_AGG = "languagesPublished";
	public static final String STATUS_AGG = "statuses";
	static final String[] filterFields = {
			AGENCY_AGG,
			LANGS_PUB_AGG
	};
	static final String[] filterEditorFields = {
			AGENCY_AGG,
			LANGS_AGG,
			STATUS_AGG
	};
	private EditorSearchView editorSearchView;
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	private MButton resetFilter = new MButton( "Reset filter" );
	private MCssLayout filterLayout = new MCssLayout();
	private List<FacetFilter> facetFilters = new ArrayList<>();
	private FilterListener filterListener;


	public FiltersLayout( String titleHeader, String showHeader, DiscoveryView searchView, EditorSearchView editorSearchView, FilterListener filterListener, I18N i18n )
	{
		super( titleHeader, showHeader, i18n );
		this.discoveryView = searchView;
		this.editorSearchView = editorSearchView;
		this.filterListener = filterListener;
		this.i18n = i18n;

		filterLayout.withStyleName( "filter-layout" );
		getInnerContainer().add( filterLayout );
    }
	
	public void clear() {
		filterLayout.removeAllComponents();
		facetFilters.clear();
	}
	
	public void setFacetFilter( EsQueryResultDetail esQueryResultDetail ) {
		clear();
		
		resetFilter
			.withFullWidth()
			.withVisible( false )
			.withStyleName( "groupButton disable" )
			.addClickListener( e -> {
				esQueryResultDetail.clearFilter();
//				discoveryView.setEsQueryResultDetail(esQueryResultDetail);
				if( discoveryView != null )
					discoveryView.refreshSearchResult();
				if( editorSearchView != null )
					editorSearchView.refreshSearchResult();
			});
		
		filterLayout.add(resetFilter);
		
		if( !esQueryResultDetail.getEsFilters().isEmpty() )
		{

			for ( EsFilter esFilter : esQueryResultDetail.getEsFilters() )
			{
				FacetFilter facetFilter = new FacetFilter( esFilter );
				facetFilters.add( facetFilter );
				filterLayout.add( facetFilter );
			}

			if ( esQueryResultDetail.isAnyFilterActive() )
				resetFilter.setVisible( true );
		}
	}

	class FacetFilter extends CustomComponent{
		
		private static final long serialVersionUID = -2492140838449462532L;
		private MCssLayout facetLayout = new MCssLayout();
		private EsFilter esFilter;
		private ComboBox<String> filterOption = new ComboBox<>();
		private String facetName;
		private List<MCheckBox> items = new ArrayList<>();
				
		public FacetFilter(EsFilter eFilter ) {
			this.esFilter = eFilter;
			
			facetLayout
				.withFullWidth()
				.add( 
					new Label( "<h3 class=\"filter-menu\">" + i18n.get("filter." + esFilter.getField(), locale) + "</h3>" , ContentMode.HTML)
			);
			
			switch( esFilter.getFilterType()) {
				case NORMAL:{
					generateNormalFilter();
				}
			}
			setCompositionRoot( facetLayout );
		}
		
		private void generateNormalFilter() {
			// generate combobox option
			filterOption.setPlaceholder( "Filter by " + i18n.get( "filter." + esFilter.getField(), locale ).toLowerCase() );
			filterOption.setItems( esFilter.getBucketAsList() );
			if ( esFilter.getField().equals( LANGS_AGG ) || esFilter.getField().equals( LANGS_PUB_AGG ) )
				filterOption.setItemCaptionGenerator( lang ->
				{
					int index = lang.lastIndexOf( '(' );
					return Language.getByIso( lang.substring( 0, index ).trim() ).getFormatted() + " " + lang.substring( index );
				} );
			filterOption.setWidth( "100%" );
			filterOption.addValueChangeListener( e ->
			{
				String selectedVal = e.getValue();
				int index = selectedVal.lastIndexOf( '(' );
				esFilter.addValue( selectedVal.substring( 0, index ).trim() );
				filterListener.filterSelected( esFilter.getField(), esFilter.getValues() );
			} );
			facetLayout.add( filterOption );

			// generate checkbox option
			generateCheckboxSelectedFilter();
		}
		
		private void generateCheckboxSelectedFilter() {
			esFilter.getFilteredBucketAsList().forEach( e ->
			{
				MCheckBox checkBox = new MCheckBox( e );
				checkBox
						.withFullWidth()
						.withStyleName( "font13px" )
						.setValue( true );
				if ( esFilter.getField().equals( LANGS_AGG ) || esFilter.getField().equals( LANGS_PUB_AGG ) )
				{
					int index = e.lastIndexOf( '(' );
					checkBox.setCaption( Language.getByIso( e.substring( 0, index ).trim() ).getFormatted() + " " + e.substring( index ) );
				}

				items.add( checkBox );
				facetLayout.add( checkBox );

				checkBox.addValueChangeListener( event ->
				{
					String selectedVal = event.getComponent().getCaption();
					if ( esFilter.getField().equals( LANGS_AGG ) || esFilter.getField().equals( LANGS_PUB_AGG ) )
					{
						int index1 = selectedVal.indexOf( ')' );
						int index2 = selectedVal.indexOf( '(' );
						esFilter.getValues().remove( selectedVal.substring( index2 + 1, index1 ) );
					}
					else
					{
						int index = selectedVal.lastIndexOf( '(' );
						esFilter.getValues().remove( selectedVal.substring( 0, index ).trim() );
					}

					filterListener.filterSelected( esFilter.getField(), esFilter.getValues() );
				});
			});
			
		}

		public String getFacetName() {
			return facetName;
		}

		public void setFacetName(String facetName) {
			this.facetName = facetName;
		}

		public List<MCheckBox> getItems() {
			return items;
		}

		public void setItems(List<MCheckBox> items) {
			this.items = items;
		}

		public MCssLayout getFacetLayout() {
			return facetLayout;
		}

		public void setFacetLayout(MCssLayout facetLayout) {
			this.facetLayout = facetLayout;
		}

	}

}
