package eu.cessda.cvs.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class VersionDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VersionDTO.class);
        VersionDTO versionDTO1 = new VersionDTO();
        versionDTO1.setId(1L);
        VersionDTO versionDTO2 = new VersionDTO();
        assertThat(versionDTO1).isNotEqualTo(versionDTO2);
        versionDTO2.setId(versionDTO1.getId());
        assertThat(versionDTO1).isEqualTo(versionDTO2);
        versionDTO2.setId(2L);
        assertThat(versionDTO1).isNotEqualTo(versionDTO2);
        versionDTO1.setId(null);
        assertThat(versionDTO1).isNotEqualTo(versionDTO2);
    }
}
