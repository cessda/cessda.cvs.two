package eu.cessda.cvs.repository.search;

import eu.cessda.cvs.domain.search.VocabularyPublish;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VocabularyPublishSearchRepository extends ElasticsearchRepository<VocabularyPublish, Long>
{

}
