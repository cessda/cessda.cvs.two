package eu.cessda.cvs.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class MetadataValueTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetadataValue.class);
        MetadataValue metadataValue1 = new MetadataValue();
        metadataValue1.setId(1L);
        MetadataValue metadataValue2 = new MetadataValue();
        metadataValue2.setId(metadataValue1.getId());
        assertThat(metadataValue1).isEqualTo(metadataValue2);
        metadataValue2.setId(2L);
        assertThat(metadataValue1).isNotEqualTo(metadataValue2);
        metadataValue1.setId(null);
        assertThat(metadataValue1).isNotEqualTo(metadataValue2);
    }
}
