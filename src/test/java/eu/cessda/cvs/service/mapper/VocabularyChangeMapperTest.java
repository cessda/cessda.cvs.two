package eu.cessda.cvs.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class VocabularyChangeMapperTest {

    private VocabularyChangeMapper vocabularyChangeMapper;

    @BeforeEach
    public void setUp() {
        vocabularyChangeMapper = new VocabularyChangeMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(vocabularyChangeMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(vocabularyChangeMapper.fromId(null)).isNull();
    }
}
