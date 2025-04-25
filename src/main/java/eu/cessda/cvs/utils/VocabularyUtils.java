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
package eu.cessda.cvs.utils;

import eu.cessda.cvs.domain.Concept;
import eu.cessda.cvs.domain.Version;
import eu.cessda.cvs.domain.Vocabulary;
import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.search.EsFilter;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.service.search.SearchScope;
import eu.cessda.cvs.web.rest.domain.Aggr;
import eu.cessda.cvs.web.rest.domain.Bucket;
import eu.cessda.cvs.web.rest.domain.CvResult;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class VocabularyUtils
{
    private VocabularyUtils()
	{
	}

    public static final Comparator<VersionDTO> VERSION_DTO_COMPARATOR =
        Comparator.comparing( VersionDTO::getItemType )
            .thenComparing( VersionDTO::getLanguage )
            .thenComparing( VersionDTO::getNumber, Comparator.reverseOrder() );

    public static final Comparator<Version> VERSION_COMPARATOR =
        Comparator.comparing( Version::getItemType )
            .thenComparing( Version::getLanguage )
            .thenComparing( Version::getNumber, Comparator.reverseOrder() );

	public static VersionDTO getVersionByLangVersion( VocabularyDTO vocabularyDTO, String language, String versionNumber )
	{
		return vocabularyDTO.getVersions().stream()
				.filter( v -> v.getLanguage().equals( language ) && v.getNumber().equals( VersionNumber.fromString( versionNumber ) ) )
				.findFirst().orElse( null );
	}

	public static EsQueryResultDetail prepareEsQuerySearching(
			String query,
			String agency,
			String lang,
			Pageable pageable,
			SearchScope searchScope )
	{
		String filter = null;
		if ( agency != null )
		{
			filter = "agency:" + agency + ";";
		}
		if ( lang != null )
		{
			filter += "language:" + lang;
		}

		return VocabularyUtils.prepareEsQuerySearching( query, filter, pageable, searchScope );
	}

	public static EsQueryResultDetail prepareEsQuerySearching( String q, String f, Pageable pageable, SearchScope searchScope )
	{
		if ( q == null )
			q = "";

		EsQueryResultDetail esq = new EsQueryResultDetail( searchScope );
		esq.setSearchTerm( q );

		// prepare filter
		if ( f == null || f.isEmpty() ) {
			f = "language:en";
		}
		VocabularyUtils.prepareActiveFilters( f, esq, searchScope );

		if ( pageable != null )
		{
			Pageable newPageable = VocabularyUtils.buildNewPageable( pageable, esq.getSortLanguage() );
			esq.setPage( newPageable );
		}

		return esq;
	}

	public static void prepareActiveFilters( String f, EsQueryResultDetail esq, SearchScope searchScope )
	{
        f = URLDecoder.decode( f, StandardCharsets.UTF_8);
        if ( f != null && !f.isEmpty() )
		{
			for ( String filterChunk : f.split( ";" ) )
			{
				processFilterChunk( esq, searchScope, filterChunk );
			}
		}
	}

	private static void processFilterChunk( EsQueryResultDetail esq, SearchScope searchScope, String filterChunk )
	{
		String[] filterSplit = filterChunk.split( ":" );
		if ( filterSplit.length != 2 )
			return;
		// prepare the field
		String field = setAggField( searchScope, filterSplit );
		if ( field == null )
			return;
		// prepare the selected filter
		List<String> activeFilters = new ArrayList<>();
		if ( field.equals( EsFilter.LANGS_PUB_AGG ) || field.equals( EsFilter.LANGS_AGG ) )
		{
			if ( filterSplit[1].contains( "_all" ) )
			{
				esq.setSearchAllLanguages( true );
				esq.setSortLanguage("All");
				return;
			}
			else if ( filterSplit[1].contains( "," ) )
			{
				activeFilters.add( filterSplit[1] );
			}
			else
			{
				activeFilters.add( filterSplit[1] );
				esq.setSortLanguage( filterSplit[1] );
			}
		}
		else
		{
			String[] enableFilters = filterSplit[1].split( "," );
			Collections.addAll( activeFilters, enableFilters );
		}

		// add the filter
		esq.getEsFilters().forEach( esf ->
		{
			if ( esf.getField().equals( field ) )
			{
				esf.setValues( activeFilters );
			}
		} );
	}

	private static String setAggField( SearchScope searchScope, String[] filterSplit )
	{
		String field = null;
		if ( filterSplit[0].equals( "agency" ) )
			field = EsFilter.AGENCY_AGG;
		if ( filterSplit[0].equals( "vocab" ) )
			field = EsFilter.NOTATION_AGG;
		if ( filterSplit[0].equals( "language" ) )
		{
			if ( searchScope.equals( SearchScope.PUBLICATIONSEARCH ) )
				field = EsFilter.LANGS_PUB_AGG;
			else
				field = EsFilter.LANGS_AGG;
		}
		if ( filterSplit[0].equals( "status" ) )
			field = EsFilter.STATUS_AGG;
		return field;
	}

	public static Pageable buildNewPageable( Pageable pageable, String sortLanguage )
	{
		if ( pageable.getSort().isUnsorted() )
		{
			return pageable;
		}

		Sort.Order sortOrder = pageable.getSort().iterator().next();
		String sortProperty = sortOrder.getProperty();
		Sort.Direction direction = sortOrder.getDirection();
		if ( sortOrder.getProperty().equals( "relevance" ) )
		{
			sortProperty = "_score";
			direction = Sort.Direction.DESC;
		}
		else if ( sortOrder.getProperty().equals( "code" ) )
		{
			sortProperty = "title" + StringUtils.capitalize( sortLanguage ) + ".Key";
		}
		return PageRequest.of( pageable.getPageNumber(), pageable.getPageSize(), Sort.by( direction, sortProperty ) );
	}

	public static CvResult mapResultToCvResult( EsQueryResultDetail esq, Page<VocabularyDTO> vocabulariesPage )
	{
		// bucketFilter
		Map<String, List<EsFilter>> filterMap = new LinkedHashMap<>();
		for ( EsFilter esFilter : esq.getEsFilters() )
		{
			List<EsFilter> esFilters = filterMap.computeIfAbsent( esFilter.getField(), k -> new ArrayList<>() );
			esFilters.add( esFilter );
		}

        var aggrs = new ArrayList<Aggr>();

		// map contains either 1 or 2
		filterMap.forEach( ( k, v ) ->
		{
            var buckets = transformToBuckets( v.get( 0 ).getBucket() );
            var filteredBuckets = transformToBuckets( v.get( 0 ).getFilteredBucket() );

            Aggr agg = new Aggr(
                v.get( 0 ).getFilterType().toString().toLowerCase(),
                v.get( 0 ).getField(),
                v.get( 0 ).getValues(),
                buckets,
                filteredBuckets
            );
			aggrs.add( agg );
		} );

        return new CvResult(
            vocabulariesPage.getContent(),
            vocabulariesPage.getTotalElements(),
            vocabulariesPage.getTotalPages(),
            vocabulariesPage.getNumberOfElements(),
            vocabulariesPage.getNumber(),
            vocabulariesPage.getSize(),
            vocabulariesPage.isFirst(),
            vocabulariesPage.isLast(),
            aggrs
        );
	}

    private static List<Bucket> transformToBuckets( Map<String, Long> bucket )
    {
        List<Bucket> buckets = new ArrayList<>( bucket.size() );
        bucket.forEach( ( k, v ) -> buckets.add( new Bucket( k, v ) ) );
        return buckets;
    }

    public static String generateUri(String uri, Vocabulary vocabulary )
	{
		return generateUri( uri, null, vocabulary.getNotation(), null, vocabulary.getSourceLanguage(), null, null );
	}

	public static String generateUri(String uri, Vocabulary vocabulary, Version version, Concept concept )
	{
		if ( concept == null )
			return generateUri( uri, true, vocabulary.getNotation(), version.getNumber(),
					vocabulary.getSourceLanguage(), null, null );
		else
			return generateUri( uri, true, vocabulary.getNotation(), version.getNumber(),
					vocabulary.getSourceLanguage(), concept.getNotation(), concept.getId() );
	}

	public static String generateUri(String uri, VersionDTO version, ConceptDTO concept )
	{
		if ( concept == null )
			return generateUri( uri, true, version.getNotation(), version.getNumber(), version.getLanguage(), null, null );
		else
			return generateUri( uri, true, version.getNotation(), version.getNumber(), version.getLanguage(), concept.getNotation(),
					concept.getId() );
	}

	/**
	 * Generated
	 *
	 * @param uri
	 * @param notation
	 * @return
	 */
	public static String generateUri(
			String uri,
			Boolean isVersionUri,
			String notation,
			VersionNumber version,
			String language,
			String code,
			Long conceptID )
	{
		if ( !uri.contains( "[VOCABULARY]" ) )
			throw new IllegalArgumentException( "Uri does not contains \"[VOCABULARY]\". Please check agency configuration" );
		String generatedUri = uri.replace( "[VOCABULARY]", notation );
		if ( language != null )
		{
			generatedUri = generatedUri.replace( "[LANGUAGE]", language );
		}
		if ( isVersionUri != null )
		{
			// generate version or code URI
			generatedUri = generatedUri.replace( "[VERSION]", version.toString() );
			if ( !Boolean.TRUE.equals( isVersionUri ) )
			{
				// generate code uri
				if ( code != null )
					generatedUri = generatedUri.replace( "[CODE]", code );
				if ( conceptID != null )
					generatedUri = generatedUri.replace( "[CONCEPTID]", conceptID.toString() );
			}
		}
		else
		{
			// generate Vocabulary URI
			generatedUri = generatedUri.split( notation )[0] + notation;
		}
		return generatedUri;
	}

	public static String toStrictXhtml( String text )
	{
		if ( text == null )
			return null;
		final Document document = Jsoup.parse( text );
		document.outputSettings().syntax( Document.OutputSettings.Syntax.xml );
		return document.html();
	}
}
