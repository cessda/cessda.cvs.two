package eu.cessda.cvmanager.ui.view.publication;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import eu.cessda.cvmanager.service.dto.VocabularyDTO;

public class EsQueryResultDetail {
	public static final int pageSize = 10 ;
	
	// Query properties
	private String searchTerm;
	private List<String> aggFields = new ArrayList<>( Arrays.asList( FiltersLayout.filterFields));
	private Pageable page = PageRequest.of( 0, pageSize );
	private Sort sort;
	private List<EsFilter> esFilters = new ArrayList<>();
	
	// Results
	private Page<VocabularyDTO> vocabularies;
	
	public EsQueryResultDetail( String viewName) {
		if( viewName.equals( DiscoveryView.VIEW_NAME))
			aggFields = new ArrayList<>( Arrays.asList( FiltersLayout.filterFields));
		else
			aggFields = new ArrayList<>( Arrays.asList( FiltersLayout.filterEditorFields));
		
		// initialize esfilter
		for( String field: aggFields) {
			EsFilter esFilter = new EsFilter();
			esFilter.setField( field );
			this.addEsFilter(esFilter);
		}
	}
	
	public void clear() {
		searchTerm = null;
		clearFilter();
		page = PageRequest.of( 0, pageSize );
	}
	
	public void clearFilter() {
		this.esFilters.forEach( esFilter -> {
			esFilter.getValues().clear();
		});
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

	public Page<VocabularyDTO> getVocabularies() {
		return vocabularies;
	}

	public void setVocabularies(Page<VocabularyDTO> vocabularies) {
		this.vocabularies = vocabularies;
	}

	public Order getFirstSortOrder() {
		return sort.iterator().next();
	}

	public List<EsFilter> getEsFilters() {
		return esFilters;
	}

	public void setEsFilters(List<EsFilter> esFilters) {
		this.esFilters = esFilters;
	}
	
	public EsQueryResultDetail addEsFilter( EsFilter esFilter) {
		this.esFilters.add(esFilter);
		return this;
	}
	
	public Optional<EsFilter> getEsFilterByField( String field) {
		return this.esFilters.stream().filter( e -> e.getField().equals( field )).findFirst();
	}
		
	public boolean isAnyFilterActive() {
		for( EsFilter esFilter : this.esFilters){
			if( esFilter.getValues() != null && !esFilter.getValues().isEmpty())
				return true;
		};
		return false;
	}
}
