package eu.cessda.cvs.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class UserAgencyMapperTest {

    private UserAgencyMapper userAgencyMapper;

    @BeforeEach
    public void setUp() {
        userAgencyMapper = new UserAgencyMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(userAgencyMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(userAgencyMapper.fromId(null)).isNull();
    }
}
