package eu.cessda.cvs.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LicenceMapperTest {

    private LicenceMapper licenceMapper;

    @BeforeEach
    public void setUp() {
        licenceMapper = new LicenceMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(licenceMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(licenceMapper.fromId(null)).isNull();
    }
}
