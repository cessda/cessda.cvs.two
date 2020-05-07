package eu.cessda.cvs.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import eu.cessda.cvs.web.rest.TestUtil;

public class MetadataFieldTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetadataField.class);
        MetadataField metadataField1 = new MetadataField();
        metadataField1.setId(1L);
        MetadataField metadataField2 = new MetadataField();
        metadataField2.setId(metadataField1.getId());
        assertThat(metadataField1).isEqualTo(metadataField2);
        metadataField2.setId(2L);
        assertThat(metadataField1).isNotEqualTo(metadataField2);
        metadataField1.setId(null);
        assertThat(metadataField1).isNotEqualTo(metadataField2);
    }
}
