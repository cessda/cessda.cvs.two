package eu.cessda.cvs.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MetadataValueMapperTest {

    private MetadataValueMapper metadataValueMapper;

    @BeforeEach
    public void setUp() {
        metadataValueMapper = new MetadataValueMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(metadataValueMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(metadataValueMapper.fromId(null)).isNull();
    }
}
