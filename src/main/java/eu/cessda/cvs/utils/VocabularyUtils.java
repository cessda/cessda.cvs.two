package eu.cessda.cvs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.cessda.cvs.service.ConfigurationService;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.service.search.EsFilter;
import eu.cessda.cvs.service.search.EsQueryResultDetail;
import eu.cessda.cvs.service.search.SearchScope;
import eu.cessda.cvs.web.rest.domain.Aggr;
import eu.cessda.cvs.web.rest.domain.CvResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.*;

public final class VocabularyUtils {
    private static final Logger log = LoggerFactory.getLogger(VocabularyUtils.class);
    private VocabularyUtils(){}

    public static VocabularyDTO generateVocabularyByPath(Path jsonPath){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        VocabularyDTO vocabularyDTO = null;
        try {
            vocabularyDTO = mapper.readValue(jsonPath.toFile(), VocabularyDTO.class);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return vocabularyDTO;
    }

    public static EsQueryResultDetail prepareEsQuerySearching(String q, String f, Pageable pageable, SearchScope searchScope) {
        if (q == null)
            q = "";
        Pageable newPageable = VocabularyUtils.buildNewPageable(pageable);

        EsQueryResultDetail esq = new EsQueryResultDetail(searchScope);
        esq.setSearchTerm(q);
        esq.setPage(newPageable);
        // prepare filter
        if ( f != null && !f.isEmpty())
            VocabularyUtils.prepareActiveFilters(f, esq, searchScope);

        return esq;
    }

    public static void prepareActiveFilters(String f, EsQueryResultDetail esq, SearchScope searchScope) {
        try {
            f = URLDecoder.decode( f, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
        if( f != null && !f.isEmpty()){
            for (String filterChunk : f.split(";")) {
                String[] filterSplit = filterChunk.split(":");
                String field = null;
                List<String> activeFilters = new ArrayList<>();
                if( filterSplit.length != 2 )
                    continue;
                // prepare the field
                if( filterSplit[0].equals( "agency") )
                    field = EsFilter.AGENCY_AGG;
                if( filterSplit[0].equals( "language") ){
                    if( searchScope.equals( SearchScope.PUBLICATIONSEARCH ))
                        field = EsFilter.LANGS_PUB_AGG;
                    else
                        field = EsFilter.LANGS_AGG;
                }
                if( filterSplit[0].equals( "status") )
                    field = EsFilter.STATUS_AGG;
                // prepare the selected filter
                String[] enableFilters = filterSplit[1].split(",");
                Collections.addAll(activeFilters, enableFilters);
                // add the filter
                String finalField = field;
                esq.getEsFilters().forEach(esf -> {
                    if( esf.getField().equals(finalField)){
                        esf.setValues( activeFilters );
                    }
                });
            }
        }
    }

    public static Pageable buildNewPageable(Pageable pageable) {
        if( pageable.getSort().isUnsorted()) {
            return pageable;
        }

        Sort.Order sortOrder = pageable.getSort().iterator().next();
        String sortProperty = sortOrder.getProperty();
        Sort.Direction direction = sortOrder.getDirection();
        if (sortOrder.getProperty().equals("relevance")) {
            sortProperty = "_score";
            direction = Sort.Direction.ASC;
        } else if (sortOrder.getProperty().equals("code")) {
            sortProperty = "notation";
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortProperty));
    }

    public static CvResult mapResultToCvResult(EsQueryResultDetail esq, Page<VocabularyDTO> vocabulariesPage) {
        CvResult cvResult = new CvResult();
        cvResult.setVocabularies( vocabulariesPage.getContent() );
        cvResult.setFirst( vocabulariesPage.isFirst() );
        cvResult.setLast( vocabulariesPage.isLast() );
        cvResult.setNumber( vocabulariesPage.getNumber());
        cvResult.setNumberOfElements( vocabulariesPage.getNumberOfElements());
        cvResult.setSize( vocabulariesPage.getSize());
        cvResult.setTotalElements( vocabulariesPage.getTotalElements());
        cvResult.setTotalPage( vocabulariesPage.getTotalPages());

        // bucketFilter
        Map<String, List<EsFilter>> filterMap = new LinkedHashMap<>();
        for (EsFilter esFilter : esq.getEsFilters()) {
            List<EsFilter> esFilters = filterMap.computeIfAbsent(esFilter.getField(), k -> new ArrayList<>());
            esFilters.add( esFilter );
        }
        // map contains either 1 or 2
        filterMap.forEach((k, v) ->{
            Aggr agg = new Aggr();
            agg.setType( v.get(0).getFilterType().toString().toLowerCase());
            agg.setField( v.get(0).getField());
            agg.setValues( v.get(0).getValues() );
            agg.setBucketFromMap( v.get(0).getBucket());
            agg.setFilteredBucketFromMap( v.get(0).getFilteredBucket());
            cvResult.addAggr(agg);
        });
        return cvResult;
    }

    public static String generateAgencyBaseUri( String agencyUri) {
        String cvUriLink = agencyUri;
        if(cvUriLink == null )
            cvUriLink = ConfigurationService.DEFAULT_CV_LINK;
        if(!cvUriLink.endsWith("/"))
            cvUriLink += "/";
        return cvUriLink;
    }

}
