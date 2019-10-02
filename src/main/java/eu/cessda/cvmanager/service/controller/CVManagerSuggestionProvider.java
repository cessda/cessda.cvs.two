/**
 * 
 */
package eu.cessda.cvmanager.service.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteQuery;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteSuggestion;
import eu.maxschuster.vaadin.autocompletetextfield.provider.CollectionSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.provider.MatchMode;

/**
 * @author klascr
 *
 */
public class CVManagerSuggestionProvider extends CollectionSuggestionProvider {

	final static Logger log = LoggerFactory.getLogger(CVManagerSuggestionProvider.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 6069930989597789282L;

	private List<String> instituteList;

	private Map<String, String> ids = new HashMap<String, String>();
	private List<String> localValues = new ArrayList<String>();

	/**
	 * 
	 */
	public CVManagerSuggestionProvider() {
		super();
		setMatchMode(MatchMode.CONTAINS);
		setIgnoreCase(true);

		// setValues( fetchNames( "GESIS" ) );
	}

	@Override
	public Collection<AutocompleteSuggestion> querySuggestions(AutocompleteQuery query) {
		String term = query.getTerm();
		if (term == null || term.isEmpty()) {
			return Collections.emptyList();
		}
		if (isIgnoreCase()) {
			// Use lower case version of the term for matching
			term = term.toLowerCase(getLocale());
		}

		boolean hasLimit = query.hasLimit();
		int limit = query.getLimit();

		Set<AutocompleteSuggestion> suggestions;
		if (hasLimit) {
			suggestions = new LinkedHashSet<AutocompleteSuggestion>(limit);
		} else {
			suggestions = new LinkedHashSet<AutocompleteSuggestion>();
		}

		List<String> names = fetchNames(term);

		int added = 0;
		for (String name : names) {
			if (hasLimit && added >= limit) {
				break; // limit reached, exit loop
			}

			String searchValue = name.toLowerCase();

			// if ( MatchMode.BEGINS == getMatchMode() && searchValue.startsWith( term )
			// || MatchMode.CONTAINS == getMatchMode() && searchValue.contains( term ) )
			// {
			if (suggestions.add(new AutocompleteSuggestion(name))) {
				++added;
			}
			// }
		}
		return suggestions;
	}

	/**
	 * Fetch list of Manufacturers for auto-completion
	 * 
	 * @return List<Manufacturer>
	 * @version 1.0
	 */
	public List<String> fetchNames(String query) {

		// suggest/{dataset}/{language}/{query}/{limit}
		String url = "http://localhost:8080/cvmanager/suggest/dataset/foo/language/en/limit/20/query/" + query;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

			HttpEntity<?> entity = new HttpEntity<>(headers);

			RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
			log.info(url);

			HttpEntity<CodeList> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET,
					entity, CodeList.class);

			// System.out.println( response );
			CodeList res = response.getBody();
			List<String> result = new ArrayList<String>();
			for (Code b : res.getListOfCodes()) {
				result.add(b.getPrefLabel().getValue());
				// store string/name and GND ID
				ids.put(b.getCode().getValue(), b.getPrefLabel().getValue());

			}
			return result;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Arrays.asList("");
		}

	}

	private ClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(1000);
		factory.setConnectTimeout(1000);
		return factory;
	}

	/**
	 * @param values
	 * @param matchMode
	 * @param ignoreCase
	 * @param locale
	 */
	public CVManagerSuggestionProvider(Collection<String> values, MatchMode matchMode, boolean ignoreCase,
			Locale locale) {
		super(values, matchMode, ignoreCase, locale);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param values
	 * @param matchMode
	 * @param ignoreCase
	 */
	public CVManagerSuggestionProvider(Collection<String> values, MatchMode matchMode, boolean ignoreCase) {
		super(values, matchMode, ignoreCase);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param values
	 * @param matchMode
	 */
	public CVManagerSuggestionProvider(Collection<String> values, MatchMode matchMode) {
		super(values, matchMode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param values
	 */
	public CVManagerSuggestionProvider(Collection<String> values) {
		super(values);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the manufacturerList
	 */
	public List<String> getInstitutionList() {
		return instituteList;
	}

	/**
	 * @param manufacturerList
	 *            the manufacturerList to set
	 */
	public void setInstitutionList(List<String> manufacturerList) {
		this.instituteList = manufacturerList;
	}

	public final Map<String, String> getIds() {
		return ids;
	}

	public final void setIds(Map<String, String> ids) {
		this.ids = ids;
	}

}
