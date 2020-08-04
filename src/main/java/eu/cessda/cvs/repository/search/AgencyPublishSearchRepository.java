package eu.cessda.cvs.repository.search;

import eu.cessda.cvs.domain.search.AgencyPublish;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Vocabulary entity.
 */
public interface AgencyPublishSearchRepository extends ElasticsearchRepository<AgencyPublish, Long>
{
}
