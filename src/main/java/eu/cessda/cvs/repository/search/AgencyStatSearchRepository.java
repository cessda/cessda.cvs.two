package eu.cessda.cvs.repository.search;

import eu.cessda.cvs.domain.search.AgencyStat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Vocabulary entity.
 */
public interface AgencyStatSearchRepository extends ElasticsearchRepository<AgencyStat, Long>
{
}
