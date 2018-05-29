package eu.cessda.cvmanager.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import eu.cessda.cvmanager.domain.BaseVocabulary;
import eu.cessda.cvmanager.domain.PublishedVocabulary;
import eu.cessda.cvmanager.domain.Vocabulary;

/**
 * Spring Data Elasticsearch repository for the Vocabulary entity.
 */
public interface PublishedVocabularySearchRepository extends ElasticsearchRepository<PublishedVocabulary, Long> {
}
