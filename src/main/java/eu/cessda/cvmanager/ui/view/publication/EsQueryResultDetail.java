package eu.cessda.cvmanager.ui.view.publication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import eu.cessda.cvmanager.service.dto.VocabularyDTO;

public class EsQueryResultDetail {
	public static final int pageSize = 5;
	
	// Query properties
	private String searchTerm;
	private List<String> aggFields = new ArrayList<>( Arrays.asList( FiltersLayout.filterFields));
	private List<FilterItem> filterItems = new ArrayList<>();
	private Pageable page = PageRequest.of( 0, pageSize );
	private Sort sort;
	// sort and so on
	
	// Results
	private Page<VocabularyDTO> vocabularies;
	private Aggregations aggregations;
	// highlight

	
	public EsQueryResultDetail addFacetItem(FilterItem fi) {
		filterItems.add( fi );
		return this;
	}
	
	public String[] getFacetOptions() {
		Set<String> facetOptions = new LinkedHashSet<>();
		filterItems.forEach( item -> facetOptions.add( item.getField() ));
		
		return facetOptions.toArray( new String[facetOptions.size()]);
	}
	
	public static Set<String> getSelectedFilterByField( String fieldName, EsQueryResultDetail fce ){
		if( fce == null || fce.filterItems == null || fce.filterItems.isEmpty())
			return Collections.emptySet();
		
		return fce.filterItems.stream()
				.filter(facetItem -> facetItem.getField().equals(fieldName))
				.map( facetItem -> facetItem.getValue() )
				.collect(Collectors.toSet());
	}
	
	public void clear() {
		searchTerm = null;
		filterItems.clear();
		page = PageRequest.of( 0, pageSize );
	}
	
	public void resetPaging() {
		page = PageRequest.of( 0, pageSize );
	}
	
	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public Pageable getPage() {
		return page;
	}

	public void setPage(Pageable page) {
		this.page = page;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public static int getPagesize() {
		return pageSize;
	}

	public List<String> getAggFields() {
		return aggFields;
	}

	public void setAggFields(List<String> aggFields) {
		this.aggFields = aggFields;
	}

	public List<FilterItem> getFilterItems() {
		return filterItems;
	}

	public void setFilterItems(List<FilterItem> filterItems) {
		this.filterItems = filterItems;
	}

	public Page<VocabularyDTO> getVocabularies() {
		return vocabularies;
	}

	public void setVocabularies(Page<VocabularyDTO> vocabularies) {
		this.vocabularies = vocabularies;
	}

	public Aggregations getAggregations() {
		return aggregations;
	}

	public void setAggregations(Aggregations aggregations) {
		this.aggregations = aggregations;
	}
	
	public Order getFirstSortOrder() {
		return sort.iterator().next();
	}
	
}
