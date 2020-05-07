package eu.cessda.cvs.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class MetadataFieldDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetadataFieldDTO.class);
        MetadataFieldDTO metadataFieldDTO1 = new MetadataFieldDTO();
        metadataFieldDTO1.setId(1L);
        MetadataFieldDTO metadataFieldDTO2 = new MetadataFieldDTO();
        assertThat(metadataFieldDTO1).isNotEqualTo(metadataFieldDTO2);
        metadataFieldDTO2.setId(metadataFieldDTO1.getId());
        assertThat(metadataFieldDTO1).isEqualTo(metadataFieldDTO2);
        metadataFieldDTO2.setId(2L);
        assertThat(metadataFieldDTO1).isNotEqualTo(metadataFieldDTO2);
        metadataFieldDTO1.setId(null);
        assertThat(metadataFieldDTO1).isNotEqualTo(metadataFieldDTO2);
    }
}
