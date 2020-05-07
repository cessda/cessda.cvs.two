package eu.cessda.cvs.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class ConceptDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConceptDTO.class);
        ConceptDTO conceptDTO1 = new ConceptDTO();
        conceptDTO1.setId(1L);
        ConceptDTO conceptDTO2 = new ConceptDTO();
        assertThat(conceptDTO1).isNotEqualTo(conceptDTO2);
        conceptDTO2.setId(conceptDTO1.getId());
        assertThat(conceptDTO1).isEqualTo(conceptDTO2);
        conceptDTO2.setId(2L);
        assertThat(conceptDTO1).isNotEqualTo(conceptDTO2);
        conceptDTO1.setId(null);
        assertThat(conceptDTO1).isNotEqualTo(conceptDTO2);
    }
}
