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
package eu.cessda.cvs.config.audit;

import eu.cessda.cvs.domain.CodeSnippet;
import eu.cessda.cvs.domain.User;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.service.dto.AgencyDTO;
import eu.cessda.cvs.service.dto.CommentDTO;
import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.dto.LicenceDTO;
import eu.cessda.cvs.service.dto.MetadataFieldDTO;
import eu.cessda.cvs.service.dto.MetadataValueDTO;
import eu.cessda.cvs.service.dto.UserAgencyDTO;
import eu.cessda.cvs.service.dto.UserDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.service.dto.VocabularyDTO;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class AuditEventPublisher implements ApplicationEventPublisherAware  {
    private ApplicationEventPublisher publisher;

    public static final String CODE_TITLE = "code_title";
    public static final String CV_TITLE = "cv_title";
    public static final String CV_TITLE_SL = "cv_title_sl";
    public static final String CV_TITLE_TL = "cv_title_tl";
    public static final String CV_TYPE = "cv_type";
    public static final String CV_VERSION = "cv_version";
    public static final String CV_LANGUAGE = "cv_language";
    public static final String CV_STATUS = "cv_status";
    public static final String CV_NOTATION = "cv_notation";
    public static final String CV_AGENCY_NAME = "cv_agency_name";

    @Override
    public void setApplicationEventPublisher(
            ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(AuditEvent event) {
        if (this.publisher != null)
            this.publisher.publishEvent(new AuditApplicationEvent(event));
    }

    /**
     * Log AgencyResource
     * @param user
     * @param agency
     * @param action
     */
    public void publish(String user, AgencyDTO agency, String action) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", agency.getName());
        map.put("link", agency.getLink());
        if (agency.getUri() != null && !agency.getUri().equalsIgnoreCase("")) {
            map.put("uri", agency.getUri());
        }
        if (agency.getUriCode() != null && !agency.getUriCode().equalsIgnoreCase("")) {
            map.put("uri_code", agency.getUriCode());
        }
        if (agency.getCanonicalUri() != null && !agency.getCanonicalUri().equalsIgnoreCase("")) {
            map.put("cannonical_uri", agency.getCanonicalUri());
        }
        publish(new AuditEvent(user, action, map));
    }

    //** */
    public String getAgency(UserDTO userDTO) {
        String agencies = "";
        if (userDTO.getUserAgencies() != null) {
            StringBuilder agenciesB = new StringBuilder();
            for (UserAgencyDTO agency : userDTO.getUserAgencies()) {
                if (agency.getAgencyName() != null) {
                    agenciesB.append(agency.getAgencyName() + ", ");
                }
                if (agency.getLanguage() != null) {
                    agenciesB.append(agency.getLanguage() + ", ");
                }
                if (agency.getAgencyRole() != null) {
                    agenciesB.append(agency.getAgencyRole());
                }
                agenciesB.append(" | ");
            }
            agencies = agenciesB.toString();
            if (!agencies.equalsIgnoreCase("")) {
                agencies = agencies.substring(0, agencies.length() - 3);
            }
        }
        return agencies;
    }

    //** */
    public String getAuthority(UserDTO userDTO) {
        String authorities = "";
        if (userDTO.getAuthorities() != null) {
            StringBuilder authoritiesB = new StringBuilder();
            for (String authority : userDTO.getAuthorities()) {
                authoritiesB.append(authority);
                authoritiesB.append(" | ");
            }
            authorities = authoritiesB.toString();
            if (!authorities.equalsIgnoreCase("")) {
                authorities = authorities.substring(0, authorities.length() - 3);
            }
        }
        return authorities;
    }

    /**
     * Log AccountResource, UserResource
     * @param user
     * @param userDTO
     * @param action
     */
    public void publish(String user, UserDTO userDTO, User userData, String action) {
        HashMap<String, Object> map = new HashMap<>();
        if (userDTO != null) {
            map.put("id", userDTO.getLogin());
            map.put("name", userDTO.getFirstName() + " " + userDTO.getLastName());
            map.put("email", userDTO.getEmail());
            map.put("language", userDTO.getLangKey());
        }
        switch (action) {
            case "USER_CREATED":
            case "USER_UPDATED":
                String agencies = getAgency(userDTO);
                if (!agencies.equals("")) {
                    map.put("agency", agencies);
                }
                String authorities = getAuthority(userDTO);
                if (!agencies.equals("")) {
                    map.put("roles", authorities);
                }
                break;
            case "USER_DELETED":
                map.put("id", userData.getLogin());
                map.put("name", userData.getFirstName() + " " + userData.getLastName());
                map.put("email", userData.getEmail());
                break;
            default:
        }
        publish(new AuditEvent(user, action, map));
    }

    /**
     * 
     * @param user
     * @param vocabulary
     * @param version
     * @param concept
     * @param replacingConceptDTO
     * @param codeSnippet
     * @param action
     */
    public void publish(String user, VocabularyDTO vocabulary, VersionDTO version, ConceptDTO concept, ConceptDTO replacingConceptDTO, CodeSnippet codeSnippet, String action) {
        HashMap<String, Object> map = new HashMap<>();
        if (vocabulary != null) {
            map.put(CV_AGENCY_NAME, vocabulary.getAgencyName());
            map.put(CV_TITLE_SL, vocabulary.getTitleAll());
        }
        switch (action) {
            case "CREATE_CODE":
            case "EDIT_CODE":
            case "ADD_TL_CODE":
            case "EDIT_TL_CODE":
            case "DELETE_TL_CODE":
                map.put(CV_TITLE, version.getTitle());
                map.put(CV_LANGUAGE, version.getLanguage());
                map.put(CV_TYPE, version.getItemType());
                map.put(CV_VERSION, version.getNumberAsString());
                if (codeSnippet.getChangeType() != null && codeSnippet.getChangeDesc() != null) {
                    map.put("code_change_type", codeSnippet.getChangeType());
                    map.put("code_change_description", codeSnippet.getChangeDesc());
                }
                if (codeSnippet.getNotation() != null && !codeSnippet.getNotation().equals("")) {
                    map.put("code_notation", codeSnippet.getNotation());
                }
                if (codeSnippet.getDefinition() != null && !codeSnippet.getDefinition().equals("")) {
                    map.put("code_definition", codeSnippet.getDefinition());
                }
                map.put(CODE_TITLE, codeSnippet.getTitle());
                break;
            case "DELETE_CODE":
                map.put(CV_TITLE, version.getTitle());
                map.put(CV_LANGUAGE, version.getLanguage());
                map.put(CV_TYPE, version.getItemType());
                map.put(CV_VERSION, version.getNumberAsString());
                map.put(CODE_TITLE, concept.getTitle());
                break;
            case "DEPRECATE_CODE":
                map.put(CV_TITLE, version.getTitle());
                map.put(CV_LANGUAGE, version.getLanguage());
                map.put(CV_TYPE, version.getItemType());
                map.put(CV_VERSION, version.getNumberAsString());
                map.put(CODE_TITLE, concept.getTitle());
                if (replacingConceptDTO != null) {
                    map.put("replaced_by_title", replacingConceptDTO.getTitle());
                }
                break;
            case "REORDER_CODE":
                map.put(CV_TITLE, version.getTitle());
                map.put(CV_LANGUAGE, version.getLanguage());
                map.put(CV_VERSION, version.getNumberAsString());
                map.put(CODE_TITLE, concept.getTitle());
                break;
            default:
        }
        publish(new AuditEvent(user, action, map));
    }

    /**
     * 
     * @param user
     * @param version
     * @param comment
     * @param action
     */
    public void publish(String user, VersionDTO version, CommentDTO comment, String action) {
        HashMap<String, Object> map = new HashMap<>();
        switch (action) {
            case "ADD_COMMENT":
            case "UPDATE_COMMENT":
            case "DELETE_COMMENT":
                map.put(CV_TITLE, version.getTitle());
                map.put(CV_LANGUAGE, version.getLanguage());
                map.put(CV_VERSION, version.getNumberAsString());
                map.put("cv_comment", comment.getContent());
                break;
            default:
        }
        publish(new AuditEvent(user, action, map));
    }

    /**
     * Log EditorResource
     * @param user
     * @param vocabulary
     * @param version
     * @param vocabularySnippet
     * @param action
     * @param forward
     */
    public void publish(String user, VocabularyDTO vocabulary, VersionDTO version, VocabularySnippet vocabularySnippet, String action, boolean forward) {
        HashMap<String, Object> map = new HashMap<>();
        if (vocabulary != null) {
            map.put(CV_AGENCY_NAME, vocabulary.getAgencyName());
            map.put(CV_TITLE_SL, vocabulary.getTitleAll());
            if (vocabularySnippet != null) {
                map.put(CV_TITLE, vocabulary.getTitleByLanguage(vocabularySnippet.getLanguage()));
                map.put(CV_TITLE_TL, vocabulary.getTitleByLanguage(vocabularySnippet.getLanguage()));
            } else {
                map.put(CV_TITLE, vocabulary.getTitleAll());
            }
            if (map.get(CV_TITLE_SL) == map.get(CV_TITLE)) {
                map.remove(CV_TITLE_SL);
            } 
            if (map.get(CV_TITLE_TL) == map.get(CV_TITLE)) {
                map.remove(CV_TITLE_TL);
            }
        }
        switch (action) {
            case "FORWARD_CV_SL_STATUS_REVIEW":
            case "FORWARD_CV_TL_STATUS_REVIEW":
                map.put(CV_TYPE, version.getItemType());
                map.put(CV_LANGUAGE, version.getLanguage());
                map.put(CV_TITLE, version.getTitle());
                map.put(CV_VERSION, version.getNumber());
                map.put(CV_STATUS, version.getStatus());
                break;
            case "FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE":
            case "FORWARD_CV_TL_STATUS_READY_TO_PUBLISH":
            case "FORWARD_CV_SL_STATUS_PUBLISH":
                map.put(CV_TYPE, version.getItemType());
                map.put(CV_LANGUAGE, version.getLanguage());
                map.put(CV_TITLE, version.getTitle());
                map.put(CV_VERSION, version.getNumber());
                map.put(CV_STATUS, version.getStatus());
                if (vocabularySnippet!= null && vocabularySnippet.getVersionNotes() != null && !vocabularySnippet.getVersionNotes().equals("")) {
                    map.put("CV_VERSION_notes", vocabularySnippet.getVersionNotes());
                }
                if (vocabularySnippet!= null && vocabularySnippet.getVersionChanges() != null && !vocabularySnippet.getVersionChanges().equals("")) {
                    map.put("CV_VERSION_changes", vocabularySnippet.getVersionChanges());
                }
                break;
            default:
        }
        if (forward) {
            publish(new AuditEvent(user, action, map));
        }
    }

    /**
     * Log EditorResource
     * @param user
     * @param vocabulary
     * @param version
     * @param vocabularySnippet
     * @param concept
     * @param replacingConceptDTO
     * @param codeSnippet
     * @param comment
     * @param action
     */
    public void publish(String user, VocabularyDTO vocabulary, VersionDTO version, VocabularySnippet vocabularySnippet, String action) {
        HashMap<String, Object> map = new HashMap<>();
        if (vocabulary != null) {
            map.put(CV_AGENCY_NAME, vocabulary.getAgencyName());
            map.put(CV_TITLE_SL, vocabulary.getTitleAll());
            if (vocabularySnippet != null) {
                map.put(CV_TITLE, vocabulary.getTitleByLanguage(vocabularySnippet.getLanguage()));
                map.put(CV_TITLE_TL, vocabulary.getTitleByLanguage(vocabularySnippet.getLanguage()));
            } else {
                map.put(CV_TITLE, vocabulary.getTitleAll());
            }
            if (map.get(CV_TITLE_SL) == map.get(CV_TITLE)) {
                map.remove(CV_TITLE_SL);
            } 
            if (map.get(CV_TITLE_TL) == map.get(CV_TITLE)) {
                map.remove(CV_TITLE_TL);
            }
        }
        switch (action) {
            case "CREATE_VOCABULARY":
            case "UPDATE_VOCABULARY":
            case "DELETE_VOCABULARY":
                if (vocabularySnippet != null) {
                    map.put(CV_TYPE, vocabularySnippet.getItemType());
                    map.put(CV_VERSION, vocabularySnippet.getVersionNumber());
                    map.put(CV_LANGUAGE, vocabularySnippet.getLanguage());
                    map.put(CV_STATUS, vocabularySnippet.getStatus());
                    map.put(CV_NOTATION, vocabularySnippet.getNotation());
                    map.put("cv_definition", vocabularySnippet.getDefinition());
                    if (vocabularySnippet.getNotes() != null && !vocabularySnippet.getNotes().equalsIgnoreCase("")) {
                        map.put("cv_notes", vocabularySnippet.getNotes());
                    }
                } else {
                    if (vocabulary != null) {
                        map.put(CV_VERSION, vocabulary.getVersionNumber());
                        map.put(CV_STATUS, vocabulary.getStatus());
                        map.put(CV_NOTATION, vocabulary.getNotation());
                        if (vocabulary.getNotes() != null && !vocabulary.getNotes().equalsIgnoreCase("")) {
                            map.put("cv_notes", vocabulary.getNotes());
                        }
                    }
                }
                break;
            case "CREATE_NEW_VOCABULARY_VERSION":
            case "UPDATE_VERSION":
            case "DELETE_VERSION":
                map.put(CV_TITLE, version.getTitle());
                map.put(CV_TYPE, version.getItemType());
                map.put(CV_VERSION, version.getNumberAsString());
                map.put(CV_LANGUAGE, version.getLanguage());
                map.put(CV_NOTATION, version.getNotation());
                break;
            case "UPDATE_VOCABULARY_EDIT_VERSION_INFO_CV":
                if (vocabularySnippet != null) {
                    map.put(CV_TYPE, vocabularySnippet.getItemType());
                    map.put(CV_LANGUAGE, vocabularySnippet.getLanguage());
                    map.put(CV_VERSION, vocabularySnippet.getVersionNumber());
                    if (vocabularySnippet.getVersionNotes() != null && !vocabularySnippet.getVersionNotes().equals("")) {
                        map.put("CV_VERSION_notes", vocabularySnippet.getVersionNotes());
                    }
                    if (vocabularySnippet.getVersionChanges() != null && !vocabularySnippet.getVersionChanges().equals("")) {
                        map.put("CV_VERSION_changes", vocabularySnippet.getVersionChanges());
                    }
                }
                break;
            case "UPDATE_VOCABULARY_EDIT_CV":
                if (vocabularySnippet != null) {
                    map.put(CV_TYPE, vocabularySnippet.getItemType());
                    map.put(CV_LANGUAGE, vocabularySnippet.getLanguage());
                    map.put("cv_definition", vocabularySnippet.getDefinition());
                    if (vocabularySnippet.getChangeType() != null) {
                        map.put("cv_change_type", vocabularySnippet.getChangeType());
                    }
                    if (vocabularySnippet.getChangeDesc() != null) {
                        map.put("cv_change_description", vocabularySnippet.getChangeDesc());
                    }
                }
                break;
            case "UPDATE_VOCABULARY_EDIT_DDI_CV":
                if (vocabularySnippet != null) {
                    map.put(CV_TYPE, vocabularySnippet.getItemType());
                    map.put(CV_LANGUAGE, vocabularySnippet.getLanguage());
                    map.put("cv_ddi_usage", vocabularySnippet.getDdiUsage());
                }
                break;
            case "UPDATE_VOCABULARY_EDIT_IDENTITY_CV":
                if (vocabularySnippet != null) {
                    map.put(CV_TYPE, vocabularySnippet.getItemType());
                    map.put(CV_LANGUAGE, vocabularySnippet.getLanguage());
                    map.put("cv_translator_agency", vocabularySnippet.getTranslateAgency());
                    map.put("cv_translator_agency_link", vocabularySnippet.getTranslateAgencyLink());
                }
                break;
            case "UPDATE_VOCABULARY_EDIT_NOTE_CV":
                if (vocabularySnippet != null) {
                    map.put(CV_TYPE, vocabularySnippet.getItemType());
                    map.put(CV_LANGUAGE, vocabularySnippet.getLanguage());
                    map.put("cv_note", vocabularySnippet.getNotes());
                }
                break;
            case "DELETE_WHOLE_VOCABULARY":
                break;
            case "DELETE_VOCABULARY_SL_AND_RELATED_TL(S)_VERSION":
                if (vocabulary != null) {
                    map.put(CV_VERSION, vocabulary.getVersionNumber());
                    map.put(CV_TYPE, "SL & TL(s)");
                }
                break;
            case "DELETE_TL_VOCABULARY":
                map.put(CV_LANGUAGE, version.getLanguage());
                map.put(CV_TITLE, version.getTitle());
                map.put(CV_VERSION, version.getNumber());
                map.put(CV_TYPE, version.getItemType());
                break;
            default:    
        }
        publish(new AuditEvent(user, action, map));
    }

    /**
     * Log LicenseResource
     * @param auditUserString
     * @param licenceDTO
     * @param licenceSnippet
     * @param action
     */
    public void publish(String user, LicenceDTO licenceDTO, LicenceDTO licenceSnippet, String action) {
        HashMap<String, Object> map = new HashMap<>();
        if (licenceDTO.getLink() != null) {
            map.put("licence_link", licenceDTO.getLink());
        }
        map.put("licence_name", licenceDTO.getName());
        map.put("licence_abbreviation", licenceDTO.getAbbr());
        switch (action) {
            case "CREATE_LICENCE":
            case "UPDATE_LICENCE":
            case "DELETE_LICENCE":
                if (licenceSnippet != null) {
                    if (licenceSnippet.getLink() != null) {
                        map.put("licence_link", licenceSnippet.getLink());
                    }
                    if (licenceSnippet.getName() != null) {
                       map.put("licence_name", licenceSnippet.getName());
                    }
                    if (licenceSnippet.getAbbr() != null) {
                      map.put("licence_abbreviation", licenceDTO.getAbbr());
                    }
                }
                break;
            default:
        }
        publish(new AuditEvent(user, action, map));
    }

    public void publish(String user, MetadataValueDTO metadataValueDTO, MetadataFieldDTO metadataFieldDTO, String action) {
        HashMap<String, Object> map = new HashMap<>();
        switch (action) {
            case "CREATE_METADATA":
            case "UPDATE_METADATA":
                map.put("metadata_key", metadataValueDTO.getMetadataKey());
                map.put("metadata_position", metadataValueDTO.getPosition());
                map.put("metadata_identifier", metadataValueDTO.getIdentifier());
                break;
            case "DELETE_METADATA":
                map.put("metadata_key", metadataValueDTO.getMetadataKey());
                map.put("metadata_position", metadataValueDTO.getPosition());
                map.put("metadata_identifier", metadataValueDTO.getIdentifier());
                break;
            default:
        }
        publish(new AuditEvent(user, action, map));
    }
}