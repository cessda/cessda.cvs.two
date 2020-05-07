package eu.cessda.cvs.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class VocabularyMapperTest {

    private VocabularyMapper vocabularyMapper;

    @BeforeEach
    public void setUp() {
        vocabularyMapper = new VocabularyMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(vocabularyMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(vocabularyMapper.fromId(null)).isNull();
    }
}
