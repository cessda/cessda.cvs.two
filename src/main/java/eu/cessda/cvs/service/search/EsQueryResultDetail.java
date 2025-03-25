/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private static final long serialVersionUID = -4522175739918034968L;

	public static final int PAGE_SIZE = 30 ;
    private SearchScope searchScope;

    // Query properties with default value, to be updated during query
	private String searchTerm = "";
	private String sortLanguage = "en";
    private transient Pageable page = PageRequest.of( 0, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "_score"));
	private List<String> aggFields = new ArrayList<>( Arrays.asList( EsFilter.AGENCY_AGG, EsFilter.LANGS_PUB_AGG,EsFilter.NOTATION_AGG ));
	private List<EsFilter> esFilters = new ArrayList<>();
	private boolean isSearchAllLanguages = false;
	private boolean withHighlight = false;
	private int codeSize = 3;

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
            aggFields = new ArrayList<>(Arrays.asList( EsFilter.AGENCY_AGG, EsFilter.LANGS_PUB_AGG, EsFilter.NOTATION_AGG ));
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

    public boolean isWithHighlight() {
        return withHighlight;
    }

    public void setWithHighlight(boolean withHighlight) {
        this.withHighlight = withHighlight;
    }

    public int getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(int codeSize) {
        this.codeSize = codeSize;
    }

    @Override
    public String toString()
    {
        return "EsQueryResultDetail{" +
            "searchScope=" + searchScope +
            ", searchTerm='" + searchTerm + '\'' +
            ", sortLanguage='" + sortLanguage + '\'' +
            ", page=" + page +
            ", aggFields=" + aggFields +
            ", esFilters=" + esFilters +
            ", isSearchAllLanguages=" + isSearchAllLanguages +
            ", withHighlight=" + withHighlight +
            ", codeSize=" + codeSize +
            ", vocabularies=" + vocabularies +
            '}';
    }
}
