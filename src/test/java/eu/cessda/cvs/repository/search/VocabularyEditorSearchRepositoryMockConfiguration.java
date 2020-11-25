package eu.cessda.cvs.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link VocabularyEditorSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class VocabularyEditorSearchRepositoryMockConfiguration {

    @MockBean
    private VocabularyEditorSearchRepository mockVocabularyEditorSearchRepository;

}
