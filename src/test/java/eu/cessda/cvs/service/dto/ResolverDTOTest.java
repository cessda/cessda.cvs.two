package eu.cessda.cvs.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class ResolverDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResolverDTO.class);
        ResolverDTO resolverDTO1 = new ResolverDTO();
        resolverDTO1.setId(1L);
        ResolverDTO resolverDTO2 = new ResolverDTO();
        assertThat(resolverDTO1).isNotEqualTo(resolverDTO2);
        resolverDTO2.setId(resolverDTO1.getId());
        assertThat(resolverDTO1).isEqualTo(resolverDTO2);
        resolverDTO2.setId(2L);
        assertThat(resolverDTO1).isNotEqualTo(resolverDTO2);
        resolverDTO1.setId(null);
        assertThat(resolverDTO1).isNotEqualTo(resolverDTO2);
    }
}
