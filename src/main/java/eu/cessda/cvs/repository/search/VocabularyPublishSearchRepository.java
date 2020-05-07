package eu.cessda.cvs.repository.search;

import eu.cessda.cvs.domain.VocabularyPublish;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VocabularyPublishSearchRepository extends ElasticsearchRepository<VocabularyPublish, Long>
{

}
