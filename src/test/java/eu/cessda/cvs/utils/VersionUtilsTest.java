package eu.cessda.cvs.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionUtilsTest {

    @Test
    public void compareVersionTest(){
        assertThat( VersionUtils.compareVersion("1.0","2.0") ).isEqualTo(-1);
        assertThat( VersionUtils.compareVersion("1.0","1.0") ).isEqualTo(0);
        assertThat( VersionUtils.compareVersion("2.0","1.0") ).isEqualTo(1);
        assertThat( VersionUtils.compareVersion("2.0.1","1.2") ).isEqualTo(1);
    }

    @Test
    public void increaseSlVersionByOneTest(){
        assertThat( VersionUtils.increaseSlVersionByOne("1.0" ) ).isEqualTo("1.1");
        assertThat( VersionUtils.increaseSlVersionByOne("1.10" ) ).isEqualTo("1.11");
    }

    @Test
    public void increaseTlVersionByOneTest(){
        assertThat( VersionUtils.increaseTlVersionByOne("1.0.2", "2.0") ).isEqualTo("2.0.1");
        assertThat( VersionUtils.increaseTlVersionByOne("2.0.1", "2.0" ) ).isEqualTo("2.0.2");
    }

    @Test
    public void getBaseVersionUriTest(){
        assertThat( VersionUtils.getBaseVersionUri("https://vocabularies.cessda.eu/aaa/sq/1.0.1", "aaa" ) ).isEqualTo("https://vocabularies.cessda.eu");
    }
}
