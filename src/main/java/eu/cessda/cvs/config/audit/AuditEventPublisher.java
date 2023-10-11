package eu.cessda.cvs.config.audit;

import eu.cessda.cvs.domain.CodeSnippet;
import eu.cessda.cvs.domain.VocabularySnippet;
import eu.cessda.cvs.service.dto.CommentDTO;
import eu.cessda.cvs.service.dto.ConceptDTO;
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

    @Override
    public void setApplicationEventPublisher(
            ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(AuditEvent event) {
        if (this.publisher != null)
            this.publisher.publishEvent(new AuditApplicationEvent(event));
    }

    public void publish(String user, VocabularyDTO vocabulary, VersionDTO version, VocabularySnippet vocabularySnippet, ConceptDTO concept, ConceptDTO replacingConceptDTO, CodeSnippet codeSnippet, CommentDTO commentDTO, String action) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (vocabulary != null) {
            map.put("cv_agency_name", vocabulary.getAgencyName());
            map.put("cv_title_sl", vocabulary.getTitleAll());
            if (vocabularySnippet != null) {
                map.put("cv_title", vocabulary.getTitleByLanguage(vocabularySnippet.getLanguage()));
                map.put("cv_title_tl", vocabulary.getTitleByLanguage(vocabularySnippet.getLanguage()));
            } else {
                map.put("cv_title", vocabulary.getTitleAll());
            }
            if (map.get("cv_title_sl") == map.get("cv_title")) {
                map.remove("cv_title_sl");
            } 
            if (map.get("cv_title_tl") == map.get("cv_title")) {
                map.remove("cv_title_tl");
            }
        }
        switch (action) {
            case "CREATE_VOCABULARY":
                map.put("cv_type", vocabularySnippet.getItemType());
                map.put("cv_version", vocabularySnippet.getVersionNumber());
                map.put("cv_language", vocabularySnippet.getLanguage());
                map.put("cv_status", vocabularySnippet.getStatus());
                map.put("cv_notation", vocabularySnippet.getNotation());
                map.put("cv_definition", vocabularySnippet.getDefinition());
                if (vocabularySnippet.getNotes() != null && !vocabularySnippet.getNotes().equalsIgnoreCase("")) {
                    map.put("cv_notes", vocabularySnippet.getNotes());
                }
                break;
            case "CREATE_NEW_VOCABULARY_VERSION":
                map.put("cv_title", version.getTitle());
                map.put("cv_type", version.getItemType());
                map.put("cv_version", version.getNumberAsString());
                map.put("cv_language", version.getLanguage());
                map.put("cv_notation", version.getNotation());
                break;
            case "UPDATE_VOCABULARY_EDIT_VERSION_INFO_CV":
                map.put("cv_type", vocabularySnippet.getItemType());
                map.put("cv_language", vocabularySnippet.getLanguage());
                map.put("cv_version", vocabularySnippet.getVersionNumber());
                if (vocabularySnippet.getVersionNotes() != null && !vocabularySnippet.getVersionNotes().equals("")) {
                    map.put("cv_version_notes", vocabularySnippet.getVersionNotes());
                }
                if (vocabularySnippet.getVersionChanges() != null && !vocabularySnippet.getVersionChanges().equals("")) {
                    map.put("cv_version_changes", vocabularySnippet.getVersionChanges());
                }
                break;
            case "UPDATE_VOCABULARY_EDIT_CV":
                map.put("cv_type", vocabularySnippet.getItemType());
                map.put("cv_language", vocabularySnippet.getLanguage());
                map.put("cv_definition", vocabularySnippet.getDefinition());
                if (vocabularySnippet.getChangeType() != null) {
                    map.put("cv_change_type", vocabularySnippet.getChangeType());
                }
                if (vocabularySnippet.getChangeDesc() != null) {
                    map.put("cv_change_description", vocabularySnippet.getChangeDesc());
                }
                break;
            case "UPDATE_VOCABULARY_EDIT_DDI_CV":
                map.put("cv_type", vocabularySnippet.getItemType());
                map.put("cv_language", vocabularySnippet.getLanguage());
                map.put("cv_ddi_usage", vocabularySnippet.getDdiUsage());
                break;
            case "UPDATE_VOCABULARY_EDIT_IDENTITY_CV":
                map.put("cv_type", vocabularySnippet.getItemType());
                map.put("cv_language", vocabularySnippet.getLanguage());
                map.put("cv_translator_agency", vocabularySnippet.getTranslateAgency());
                map.put("cv_translator_agency_link", vocabularySnippet.getTranslateAgencyLink());
                break;
            case "UPDATE_VOCABULARY_EDIT_NOTE_CV":
                map.put("cv_type", vocabularySnippet.getItemType());
                map.put("cv_language", vocabularySnippet.getLanguage());
                map.put("cv_note", vocabularySnippet.getNotes());
                break;
            case "FORWARD_CV_SL_STATUS_REVIEW":
            case "FORWARD_CV_TL_STATUS_REVIEW":
                map.put("type", version.getItemType());
                map.put("cv_language", version.getLanguage());
                map.put("cv_title", version.getTitle());
                map.put("cv_version", version.getNumber());
                map.put("cv_status", version.getStatus());
                break;
            case "FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE":
            case "FORWARD_CV_TL_STATUS_READY_TO_PUBLISH":
                map.put("cv_type", version.getItemType());
                map.put("cv_language", version.getLanguage());
                map.put("cv_title", version.getTitle());
                map.put("cv_version", version.getNumber());
                map.put("cv_status", version.getStatus());
                if (vocabularySnippet.getVersionNotes() != null && !vocabularySnippet.getVersionNotes().equals("")) {
                    map.put("cv_version_notes", vocabularySnippet.getVersionNotes());
                }
                if (vocabularySnippet.getVersionChanges() != null && !vocabularySnippet.getVersionChanges().equals("")) {
                    map.put("cv_version_changes", vocabularySnippet.getVersionChanges());
                }
                break;
            case "FORWARD_CV_SL_STATUS_PUBLISH":
                map.put("cv_type", version.getItemType());
                map.put("cv_language", version.getLanguage());
                map.put("cv_title", version.getTitle());
                map.put("cv_version", version.getNumber());
                map.put("cv_status", version.getStatus());
                break;
            case "DELETE_WHOLE_VOCABULARY":
                break;
            case "DELETE_VOCABULARY_SL_AND_RELATED_TL(S)_VERSION":
                map.put("cv_version", vocabulary.getVersionNumber());
                map.put("cv_type", "SL & TL(s)");
                break;
            case "DELETE_TL_VOCABULARY":
                map.put("cv_language", version.getLanguage());
                map.put("cv_title", version.getTitle());
                map.put("cv_version", version.getNumber());
                map.put("cv_type", version.getItemType());
                break;
            case "CREATE_CODE":
            case "EDIT_CODE":
            case "ADD_TL_CODE":
            case "EDIT_TL_CODE":
            case "DELETE_TL_CODE":
                map.put("cv_title", version.getTitle());
                map.put("cv_language", version.getLanguage());
                map.put("cv_type", version.getItemType());
                map.put("cv_version", version.getNumberAsString());
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
                map.put("code_title", codeSnippet.getTitle());
                break;
            case "DELETE_CODE":
                map.put("cv_title", version.getTitle());
                map.put("cv_language", version.getLanguage());
                map.put("cv_type", version.getItemType());
                map.put("cv_version", version.getNumberAsString());
                map.put("code_title", concept.getTitle());
                break;
            case "DEPRECATE_CODE":
                map.put("cv_title", version.getTitle());
                map.put("cv_language", version.getLanguage());
                map.put("cv_type", version.getItemType());
                map.put("cv_version", version.getNumberAsString());
                map.put("code_title", concept.getTitle());
                if (replacingConceptDTO != null) {
                    map.put("replaced_by_title", replacingConceptDTO.getTitle());
                }
                break;
            case "REORDER_CODE":
                map.put("cv_title", version.getTitle());
                map.put("cv_language", version.getLanguage());
                map.put("cv_version", version.getNumberAsString());
                map.put("code_title", concept.getTitle());
                break;
            case "ADD_COMMENT":
            case "UPDATE_COMMENT":
            case "DELETE_COMMENT":
                map.put("cv_title", version.getTitle());
                map.put("cv_language", version.getLanguage());
                map.put("cv_version", version.getNumberAsString());
                map.put("cv_comment", commentDTO.getContent());
                break;
            default:    
        }
        publish(new AuditEvent(user, action, map));
    }
}