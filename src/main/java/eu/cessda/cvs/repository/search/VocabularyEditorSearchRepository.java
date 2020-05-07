package eu.cessda.cvs.repository.search;

import eu.cessda.cvs.domain.VocabularyEditor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Vocabulary entity.
 */
public interface VocabularyEditorSearchRepository extends ElasticsearchRepository<VocabularyEditor, Long>
{
}
