package eu.cessda.cvs.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ResolverMapperTest {

    private ResolverMapper resolverMapper;

    @BeforeEach
    public void setUp() {
        resolverMapper = new ResolverMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(resolverMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(resolverMapper.fromId(null)).isNull();
    }
}
