package eu.cessda.cvs.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ConceptMapperTest {

    private ConceptMapper conceptMapper;

    @BeforeEach
    public void setUp() {
        conceptMapper = new ConceptMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(conceptMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(conceptMapper.fromId(null)).isNull();
    }
}
