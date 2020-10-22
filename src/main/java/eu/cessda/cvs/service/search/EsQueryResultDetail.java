package eu.cessda.cvs.service.search;

import eu.cessda.cvs.service.dto.VocabularyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EsQueryResultDetail implements Serializable {
	public static final int PAGE_SIZE = 30 ;
    private SearchScope searchScope;

    // Query properties with default value, to be updated during query
	private String searchTerm = "";
	private String sortLanguage = "en";
    private transient Pageable page = PageRequest.of( 0, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "_score"));
	private List<String> aggFields = new ArrayList<>( Arrays.asList( EsFilter.AGENCY_AGG, EsFilter.LANGS_PUB_AGG ));
	private List<EsFilter> esFilters = new ArrayList<>();
	private boolean isSearchAllLanguages = false;

	// Results
	private transient Page<VocabularyDTO> vocabularies;

    public EsQueryResultDetail(){
        this( SearchScope.PUBLICATIONSEARCH );
    }

	public EsQueryResultDetail(SearchScope searchScope) {
        init(searchScope);
        this.searchScope = searchScope;
    }

    private void init(SearchScope searchScope) {
        if( searchScope.equals(SearchScope.EDITORSEARCH)) {
            aggFields = new ArrayList<>(Arrays.asList( EsFilter.AGENCY_AGG, EsFilter.LANGS_AGG, EsFilter.STATUS_AGG ));
        } else {
            aggFields = new ArrayList<>(Arrays.asList( EsFilter.AGENCY_AGG, EsFilter.LANGS_PUB_AGG ));
        }
        // initialize esfilter
        for( String field: aggFields) {
            EsFilter esFilter = new EsFilter();
            esFilter.setField( field );
            this.addEsFilter(esFilter);
        }
    }

    public SearchScope getSearchScope() {
        return searchScope;
    }

    public void setSearchScope(SearchScope searchScope) {
        init(searchScope);
        this.searchScope = searchScope;
    }

    public boolean isUseCustomQuery(){
	    return !searchTerm.isEmpty() && searchTerm.length() > 2;
    }

    public void clear() {
		searchTerm = null;
		clearFilter();
		page = PageRequest.of( 0, PAGE_SIZE);
	}

	public void clearFilter() {
		this.esFilters.forEach( esFilter -> esFilter.getValues().clear());
	}

	public void resetPaging() {
		page = PageRequest.of( 0, PAGE_SIZE);
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

	public static int getPagesize() {
		return PAGE_SIZE;
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

	public List<EsFilter> getEsFilters() {
		return esFilters;
	}

	public void setEsFilters(List<EsFilter> esFilters) {
		this.esFilters = esFilters;
	}

	public EsQueryResultDetail addEsFilter(EsFilter esFilter) {
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
		}
		return false;
	}

    public String getSortLanguage() {
        return sortLanguage;
    }

    public void setSortLanguage(String sortLanguage) {
        this.sortLanguage = sortLanguage;
    }

    public boolean isSearchAllLanguages() {
        return isSearchAllLanguages;
    }

    public void setSearchAllLanguages(boolean searchAllLanguages) {
        isSearchAllLanguages = searchAllLanguages;
    }
}
