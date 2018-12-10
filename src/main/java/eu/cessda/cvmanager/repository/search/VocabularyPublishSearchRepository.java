package eu.cessda.cvmanager.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import eu.cessda.cvmanager.domain.VocabularyPublish;

public interface VocabularyPublishSearchRepository extends ElasticsearchRepository<VocabularyPublish, Long> {

}
