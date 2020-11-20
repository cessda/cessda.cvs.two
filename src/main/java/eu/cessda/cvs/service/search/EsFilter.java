package eu.cessda.cvs.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EsFilter implements Serializable {
	public enum FilterType {KEYWORD, RANGE}

    public static final String AGENCY_AGG = "agencyName";
    public static final String LANGS_AGG = "languages";
    public static final String LANGS_PUB_AGG = "languagesPublished";
    public static final String STATUS_AGG = "statuses";
    public static final String NOTATION_AGG = "notation";

	private FilterType filterType;
	private String field;
	// active filter
	private List<String> values = new ArrayList<>();
	// aggregration active filter
	private Map<String, Long> filteredBucket = new LinkedHashMap<>();

	// aggregation exclude active filter
	private Map<String, Long> bucket = new LinkedHashMap<>();

	public EsFilter() {
		filterType = FilterType.KEYWORD;
	}
	public FilterType getFilterType() {
		return filterType;
	}
	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	public EsFilter addValue(String value) {
		if( !this.values.contains(value) )
			this.values.add(value);
		return this;
	}
	public Map<String, Long> getBucket() {
		return bucket;
	}
	public void setBucket(Map<String, Long> bucket) {
		this.bucket = bucket;
	}

	public EsFilter addBucket(String term, Long count) {
		this.bucket.put(term, count);
		return this;
	}

	public Map<String, Long> getFilteredBucket() {
		return this.filteredBucket;
	}
	public void setFilteredBucket(Map<String, Long> filteredBucket) {
		this.filteredBucket = filteredBucket;
	}

	public EsFilter addFilteredBucket(String term, Long count) {
		if( this.filteredBucket == null )
			this.filteredBucket = new LinkedHashMap<>();

		this.filteredBucket.put(term, count);
		return this;
	}

	public void clearBucket() {
		this.bucket.clear();
		this.filteredBucket.clear();
	}

	public static boolean isActiveFilter(EsFilter esFilter, String term) {
        return esFilter.getValues().contains(term);
    }
}
