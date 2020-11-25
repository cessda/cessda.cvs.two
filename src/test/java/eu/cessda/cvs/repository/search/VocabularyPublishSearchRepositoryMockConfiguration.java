package eu.cessda.cvs.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link VocabularyPublishSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class VocabularyPublishSearchRepositoryMockConfiguration {

    @MockBean
    private VocabularyPublishSearchRepository mockVocabularyPublishSearchRepository;

}
