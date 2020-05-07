package eu.cessda.cvs.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class UserAgencyDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAgencyDTO.class);
        UserAgencyDTO userAgencyDTO1 = new UserAgencyDTO();
        userAgencyDTO1.setId(1L);
        UserAgencyDTO userAgencyDTO2 = new UserAgencyDTO();
        assertThat(userAgencyDTO1).isNotEqualTo(userAgencyDTO2);
        userAgencyDTO2.setId(userAgencyDTO1.getId());
        assertThat(userAgencyDTO1).isEqualTo(userAgencyDTO2);
        userAgencyDTO2.setId(2L);
        assertThat(userAgencyDTO1).isNotEqualTo(userAgencyDTO2);
        userAgencyDTO1.setId(null);
        assertThat(userAgencyDTO1).isNotEqualTo(userAgencyDTO2);
    }
}
