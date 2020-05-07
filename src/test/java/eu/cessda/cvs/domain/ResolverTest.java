package eu.cessda.cvs.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class ResolverTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Resolver.class);
        Resolver resolver1 = new Resolver();
        resolver1.setId(1L);
        Resolver resolver2 = new Resolver();
        resolver2.setId(resolver1.getId());
        assertThat(resolver1).isEqualTo(resolver2);
        resolver2.setId(2L);
        assertThat(resolver1).isNotEqualTo(resolver2);
        resolver1.setId(null);
        assertThat(resolver1).isNotEqualTo(resolver2);
    }
}
