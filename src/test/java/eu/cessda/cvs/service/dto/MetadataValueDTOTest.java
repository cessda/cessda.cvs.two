package eu.cessda.cvs.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class MetadataValueDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetadataValueDTO.class);
        MetadataValueDTO metadataValueDTO1 = new MetadataValueDTO();
        metadataValueDTO1.setId(1L);
        MetadataValueDTO metadataValueDTO2 = new MetadataValueDTO();
        assertThat(metadataValueDTO1).isNotEqualTo(metadataValueDTO2);
        metadataValueDTO2.setId(metadataValueDTO1.getId());
        assertThat(metadataValueDTO1).isEqualTo(metadataValueDTO2);
        metadataValueDTO2.setId(2L);
        assertThat(metadataValueDTO1).isNotEqualTo(metadataValueDTO2);
        metadataValueDTO1.setId(null);
        assertThat(metadataValueDTO1).isNotEqualTo(metadataValueDTO2);
    }
}
