/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.cessda.cvs.domain.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.cessda.cvs.domain.enumeration.ItemType;
import eu.cessda.cvs.domain.enumeration.Status;
import eu.cessda.cvs.service.dto.AgencyDTO;
import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;
import eu.cessda.cvs.utils.VersionNumber;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.ElementCollection;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Document(indexName = "agency")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgencyStat implements Serializable {
    private static final long serialVersionUID = 1L;

    public AgencyStat() {
    }

    public AgencyStat(AgencyDTO agencyDTO) {
        this(agencyDTO.getId(), agencyDTO.getName(), agencyDTO.getLink(), agencyDTO.getDescription(), agencyDTO.getLogo());
    }

    public AgencyStat(Long id, String name, String url, String description, String logo) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.description = description;
        this.logo = logo;
    }

    @Id
    private Long id;

    @Field( type = FieldType.Keyword )
    private String name;

    private String url;

    private String description;

    private String logo;

    @ElementCollection
    @Field( type = FieldType.Nested, store = true )
    private List<VocabStat> vocabStats = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<VocabStat> getVocabStats() {
        return vocabStats;
    }

    public void setVocabStats(List<VocabStat> vocabStats) {
        this.vocabStats = vocabStats;
    }

    private VocabStat getVocabStat(VocabularyDTO vocabularyDTO) {
        VocabStat vocabStat = this.vocabStats.stream().filter(v -> v.getNotation().equals(vocabularyDTO.getNotation()))
            .findFirst().orElse(null);
        if( vocabStat == null ) {
            vocabStat = new VocabStat(vocabularyDTO.getNotation(), vocabularyDTO.getSourceLanguage());
            this.vocabStats.add( vocabStat);
        }
        return vocabStat;
    }

    public void deleteVocabStat(String cvNotation) {
        VocabStat vocabStat = this.vocabStats.stream().filter(v -> v.getNotation().equals( cvNotation ))
            .findFirst().orElse(null);
        if( vocabStat != null ) {
            this.vocabStats.remove(vocabStat);
        }
    }

    public AgencyStat updateVocabStat(VocabularyDTO vocabularyDTO) {
        
        VocabStat vocabStat = getVocabStat(vocabularyDTO);
        VersionNumber latestSlVersionNumber = null;
        VersionNumber latestPublishedSlVersionNumber = null;

        Set<String> languages = new LinkedHashSet<>();
        List<VersionStatusStat> versionStatusStats = new ArrayList<>();
        List<VersionCodeStat> versionCodeStats = new ArrayList<>();

        for (VersionDTO v : vocabularyDTO.getVersions()) {
            if(v.getItemType().equals(ItemType.SL.toString())){
                if(v.getStatus().equals(Status.PUBLISHED.toString())) {
                    latestPublishedSlVersionNumber = generatePublishedSlVersionCodeStats(latestPublishedSlVersionNumber, versionCodeStats, v);
                }
                if(latestSlVersionNumber == null) {
                    latestSlVersionNumber = v.getNumber();
                }
            }
            if(v.getStatus().equals(Status.PUBLISHED.toString()) || v.getNumber().equalMinorVersionNumber(latestSlVersionNumber)) {
                languages.add(v.getLanguage());
                VersionStatusStat versionStatusStat = new VersionStatusStat(
                    v.getLanguage(),
                    v.getItemType(),
                    v.getNumber(),
                    v.getStatus(),
                    v.getCreationDate(),
                    v.getLastChangeDate()
                );
                versionStatusStats.add(versionStatusStat);
            }
        }

        vocabStat.setCurrentVersion(VersionNumber.toString(latestSlVersionNumber));
        vocabStat.setLatestPublishedVersion(VersionNumber.toString(latestPublishedSlVersionNumber));
        vocabStat.setLanguages(new ArrayList<>(languages));
        vocabStat.setVersionCodeStats(versionCodeStats);
        vocabStat.setVersionStatusStats(versionStatusStats);

        return this;
    }

    private VersionNumber generatePublishedSlVersionCodeStats(VersionNumber latestPublishedSlVersionNumber, List<VersionCodeStat> versionCodeStats, VersionDTO v) {
        if( latestPublishedSlVersionNumber == null )
            latestPublishedSlVersionNumber = v.getNumber();

        VersionCodeStat versionCodeStat = new VersionCodeStat(v.getNumber());
        versionCodeStat.setCodes(v.getConcepts().stream()
            .sorted(Comparator.comparing(ConceptDTO::getPosition))
            .map(ConceptDTO::getNotation).collect(Collectors.toList()));
        versionCodeStats.add(versionCodeStat);
        return latestPublishedSlVersionNumber;
    }
}
