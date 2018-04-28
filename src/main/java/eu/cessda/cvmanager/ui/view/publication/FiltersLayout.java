package eu.cessda.cvmanager.ui.view.publication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.SingleBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filters.Filters;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.data.domain.Page;
//import org.springframework.data.solr.core.query.Field;
//import org.springframework.data.solr.core.query.result.FacetEntry;
//import org.springframework.data.solr.core.query.result.FacetFieldEntry;
//import org.springframework.data.solr.core.query.result.FacetPage;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.i18n.I18N;
//import org.vaadin.tokenfield.TokenField;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MCheckBox;
import org.vaadin.viritin.layouts.MCssLayout;

import com.vaadin.shared.ui.ContentMode;
//import com.vaadin.shared.ui.combobox.FilteringMode;
//import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Button.ClickListener;

//import net.cessda.saw.utils.ComponentUtils;
//import net.cessda.saw.vaadin.view.SearchView;
//import net.cessda.saw.vaadin.view.Token;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.UI;

public class FiltersLayout extends CustomComponent{

	private static final long serialVersionUID = -3119422665592890019L;
	
	@FunctionalInterface
	public interface FilterListener {
		void filterSelected( List<FilterItem> filterItems );
	}
	
	private DiscoveryView discoveryView;
	
	private I18N i18n;
	private Locale locale = UI.getCurrent().getLocale();
	
	public static final String AGENCY_AGG = "agencyName";
	public static final String LANGS_AGG = "languages";
	
	public static String[] filterFields = {
			AGENCY_AGG,
			LANGS_AGG
		};
	
	private MButton resetFilter = new MButton( "Reset filter" );
	private MCssLayout filterLayout = new MCssLayout();
	private List<FacetFilter> facetFilters = new ArrayList<>();
	private FilterListener filterListener;
	

	public FiltersLayout( DiscoveryView searchView, FilterListener filterListener, I18N i18n ) {
		this.discoveryView = searchView;
		this.filterListener = filterListener;
		this.i18n = i18n;

		filterLayout.withFullWidth();
		setCompositionRoot( filterLayout );
    }
	
	public void clear() {
		filterLayout.removeAllComponents();
		facetFilters.clear();
	}
	
	public void setFacetFilter( EsQueryResultDetail esQueryResultDetail ) {
		clear();
		
		resetFilter
			.withVisible( false )
			.withStyleName( "groupButton disable" )
			.addClickListener( e -> {
				esQueryResultDetail.getFilterItems().clear();
				discoveryView.setEsQueryResultDetail(esQueryResultDetail);
			});
		
		filterLayout.add(resetFilter);
		
		boolean checkedFilterExist = false;
		
		for( String field : filterFields ) {
			if( esQueryResultDetail.getFilterItems().isEmpty() ) {
				Terms aggregation = esQueryResultDetail.getAggregations().get( field + "_count");
				
				if( aggregation != null) {
					List<String> facetItems = new ArrayList<>();
					FacetFilter facetFilter = new FacetFilter( field );
					// add list
					facetFilters.add( facetFilter );
					// add GUI
					filterLayout.add( facetFilter );
					
					Set<String> selectedFacetItem = EsQueryResultDetail.getSelectedFilterByField(field, esQueryResultDetail);
					
					for (Terms.Bucket b : aggregation.getBuckets()) {
//						valuesWithNumHits.put(b.getKeyAsString(), b.getDocCount());
						
						boolean isFacetChecked = false;
						if( selectedFacetItem != null && !selectedFacetItem.isEmpty())
							isFacetChecked = selectedFacetItem.contains( b.getKeyAsString() );
						
						facetItems.add( b.getKeyAsString() + " (" + b.getDocCount() + ")" );
						
						// only show checkbox on selected facet
						if( isFacetChecked) {
							facetFilter.addFacetItem( b.getKeyAsString() + " (" + b.getDocCount() + ")", isFacetChecked);
							checkedFilterExist = true;
						}
						
					}
					facetFilter.getSearchFacetItem().setItems(facetItems);
				}
				
			}
			else {
				Filters aggFilters= esQueryResultDetail.getAggregations().get( "aggregration_filter");
				if( aggFilters != null ) {
					List<String> facetItems = new ArrayList<>();
					FacetFilter facetFilter = new FacetFilter( field );
					// add list
					facetFilters.add( facetFilter );
					// add GUI
					filterLayout.add( facetFilter );
					
					Set<String> selectedFacetItem = EsQueryResultDetail.getSelectedFilterByField(field, esQueryResultDetail);
					
//					Terms aggregation = ((SingleBucketAggregation) aggFilters).getAggregations().get( field + "_count");
					Collection<Filters.Bucket> buckets = (Collection<Filters.Bucket>) aggFilters.getBuckets();
					
					for (Filters.Bucket bucket : buckets) {

			            if (bucket.getDocCount() != 0) {

			                System.out.println((int) bucket.getDocCount());
			                System.out.println(bucket.getKeyAsString());
			                Terms terms =bucket.getAggregations().get(field + "_count");
			                Collection<Terms.Bucket> bkts = (Collection<Bucket>) terms.getBuckets();
			                for (Bucket b : bkts) {

			                    if (b.getDocCount() != 0) {
			                        //ESClassification classificaiton = new ESClassification();
//			                        System.out.println((int) b.getDocCount());
//			                        System.out.println(b.getKeyAsString());
			                        
			                        boolean isFacetChecked = false;
									if( selectedFacetItem != null && !selectedFacetItem.isEmpty())
										isFacetChecked = selectedFacetItem.contains( b.getKeyAsString() );
									
									facetItems.add( b.getKeyAsString() + " (" + b.getDocCount() + ")" );
									
									// only show checkbox on selected facet
									if( isFacetChecked) {
										facetFilter.addFacetItem( b.getKeyAsString() + " (" + b.getDocCount() + ")", isFacetChecked);
										checkedFilterExist = true;
									}

			                    } else {
			                        //list = Collections.<ESClassification> emptyList();
			                    }

			                }


			            }

			        }
					
					
//					
//					for (Terms.Bucket b : aggregation.getBuckets()) {
////						valuesWithNumHits.put(b.getKeyAsString(), b.getDocCount());
//						
//						boolean isFacetChecked = false;
//						if( selectedFacetItem != null && !selectedFacetItem.isEmpty())
//							isFacetChecked = selectedFacetItem.contains( b.getKeyAsString() );
//						
//						facetItems.add( b.getKeyAsString() + " (" + b.getDocCount() + ")" );
//						
//						// only show checkbox on selected facet
//						if( isFacetChecked) {
//							facetFilter.addFacetItem( b.getKeyAsString() + " (" + b.getDocCount() + ")", isFacetChecked);
//							checkedFilterExist = true;
//						}
//						
//					}
					facetFilter.getSearchFacetItem().setItems(facetItems);
					
//					for (Filters.Bucket b : aggFilters.getBuckets()) {
////						for (Terms.Bucket bb : (Bucket) b.getAggregations()) {
////							
////						}
////						valuesWithNumHits.put(b.getKeyAsString(), b.getDocCount());
//						
//						boolean isFacetChecked = false;
//						if( selectedFacetItem != null && !selectedFacetItem.isEmpty())
//							isFacetChecked = selectedFacetItem.contains( b.getKeyAsString() );
//						
//						facetItems.add( b.getKeyAsString() + " (" + b.getDocCount() + ")" );
//						
//						// only show checkbox on selected facet
//						if( isFacetChecked) {
//							facetFilter.addFacetItem( b.getKeyAsString() + " (" + b.getDocCount() + ")", isFacetChecked);
//							checkedFilterExist = true;
//						}
//						
//					}
//					facetFilter.getSearchFacetItem().setItems(facetItems);
				}
			}
			
		}
		
		
	
						
//		for ( Field field : esQueryResultDetail.getResult().getFacetFields() )
//		{
//			List<String> facetItems = new ArrayList<>();
//			FacetFilter facetFilter = new FacetFilter( field.getName() );
//			// add list
//			facetFilters.add( facetFilter );
//			// add GUI
//			filterLayout.add( facetFilter );
//			
//			Set<String> selectedFacetItem = null;
//			if( esQueryResultDetail != null)
//				selectedFacetItem = SolrQueryDetail.getSelectedFilterByField(field.getName(), esQueryResultDetail);
//			
//			for ( FacetEntry facetEntry : esQueryResultDetail.getResult().getFacetResultPage( field ).getContent() ) {
//				if( facetEntry.getValue().length() < 2)
//					continue;
//				boolean isFacetChecked = false;
//				if( selectedFacetItem != null && !selectedFacetItem.isEmpty())
//					isFacetChecked = selectedFacetItem.contains( facetEntry.getValue() );
//				
//				facetItems.add( facetEntry.getValue() + " (" + facetEntry.getValueCount() + ")" );
//				
//				// only show checkbox on selected facet
//				if( isFacetChecked) {
//					facetFilter.addFacetItem( facetEntry.getValue() + " (" + facetEntry.getValueCount() + ")", isFacetChecked);
//					checkedFilterExist = true;
//				}
//			}
//			ComponentUtils.setComboboxItems(facetFilter.getSearchFacetItem(), facetItems.toArray(new String[facetItems.size()]));
//		}
		
		if( checkedFilterExist )
			resetFilter.setVisible( true );
	}
	
	public List<FilterItem> getSelectedFacetItem( String newFacetName, String newFacetValue) {
		List<FilterItem> filterItems = new ArrayList<>();
		
		facetFilters.forEach( facetFilter ->{
			facetFilter.getItems().forEach( item ->{
				if( item.getValue() ) {
					// remove facet occurrence number from caption
					String caption = item.getCaption();
					int index = caption.lastIndexOf( "(" );
					
					FilterItem filterItem = new FilterItem(facetFilter.getFacetName(), caption.substring( 0, index).trim());
					filterItems.add(filterItem);
				}
			});
		});
		
		if( newFacetName != null && newFacetValue != null && !newFacetValue.isEmpty()) {
			FilterItem filterItem = new FilterItem(newFacetName, newFacetValue);
			filterItems.add(filterItem);
		}
		return filterItems;
	}

	
	
	class FacetFilter extends CustomComponent{
		
		private static final long serialVersionUID = -2492140838449462532L;
		private String facetName;
		private List<MCheckBox> items = new ArrayList<>();
		private MCssLayout facetLayout = new MCssLayout();
		private ComboBox searchFacetItem = new ComboBox();
		
		public FacetFilter(String fieldName ) {
			setCompositionRoot( facetLayout );
			
			this.facetName = fieldName;
			
//			searchFacetItem.setInputPrompt( "Filter by " + i18n.get(fieldName, locale).toLowerCase() );
//			searchFacetItem.setNullSelectionAllowed( false );
//			searchFacetItem.setFilteringMode(FilteringMode.CONTAINS);
			searchFacetItem.setWidth("90%");
			searchFacetItem.addValueChangeListener( e -> {
				String selectedVal = e.getValue().toString();
				int index = selectedVal.lastIndexOf( "(" );
				filterListener.filterSelected(getSelectedFacetItem( facetName, selectedVal.substring( 0, index).trim()));
			});
			
			facetLayout.add( 
					new Label( "<h3 class=\"filter-menu\">" + i18n.get(fieldName, locale) + "</h3>" , ContentMode.HTML),
					searchFacetItem
					);
			
		}
		
		public void addFacetItem(String itemLabel, boolean checked) {
			MCheckBox checkBox = new MCheckBox( itemLabel );
			checkBox
				.withFullWidth()
				.withStyleName( "font13px" )
				.setValue( checked );
			items.add(checkBox);
			facetLayout.add(checkBox);
			
			checkBox.addValueChangeListener( event -> filterListener.filterSelected(getSelectedFacetItem( null, null)));
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

		public ComboBox getSearchFacetItem() {
			return searchFacetItem;
		}

		public void setSearchFacetItem(ComboBox searchFacetItem) {
			this.searchFacetItem = searchFacetItem;
		}
		
	}

}