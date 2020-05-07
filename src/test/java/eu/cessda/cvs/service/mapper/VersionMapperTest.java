package eu.cessda.cvs.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class VersionMapperTest {

    private VersionMapper versionMapper;

    @BeforeEach
    public void setUp() {
        versionMapper = new VersionMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(versionMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(versionMapper.fromId(null)).isNull();
    }
}
