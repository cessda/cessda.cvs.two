package eu.cessda.cvs.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class UserAgencyTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAgency.class);
        UserAgency userAgency1 = new UserAgency();
        userAgency1.setId(1L);
        UserAgency userAgency2 = new UserAgency();
        userAgency2.setId(userAgency1.getId());
        assertThat(userAgency1).isEqualTo(userAgency2);
        userAgency2.setId(2L);
        assertThat(userAgency1).isNotEqualTo(userAgency2);
        userAgency1.setId(null);
        assertThat(userAgency1).isNotEqualTo(userAgency2);
    }
}
