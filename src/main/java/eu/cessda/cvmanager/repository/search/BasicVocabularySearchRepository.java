package eu.cessda.cvmanager.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import eu.cessda.cvmanager.domain.BasicVocabulary;
import eu.cessda.cvmanager.domain.Vocabulary;

/**
 * Spring Data Elasticsearch repository for the Vocabulary entity.
 */
public interface BasicVocabularySearchRepository extends ElasticsearchRepository<BasicVocabulary, Long> {
}
