package eu.cessda.cvs.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MetadataFieldMapperTest {

    private MetadataFieldMapper metadataFieldMapper;

    @BeforeEach
    public void setUp() {
        metadataFieldMapper = new MetadataFieldMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(metadataFieldMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(metadataFieldMapper.fromId(null)).isNull();
    }
}
