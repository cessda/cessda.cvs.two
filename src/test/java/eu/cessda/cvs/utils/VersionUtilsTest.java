/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.cessda.cvs.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionUtilsTest {

    @Test
    void compareVersionTest(){
        assertThat( VersionUtils.compareVersion("1.0","2.0")).isNegative();
        assertThat( VersionUtils.compareVersion("1.0","1.0")).isZero();
        assertThat( VersionUtils.compareVersion("2.0","1.0")).isPositive();
        assertThat( VersionUtils.compareVersion("2.0","10.0")).isNegative();
        assertThat( VersionUtils.compareVersion("10.0","2.10")).isPositive();
        assertThat( VersionUtils.compareVersion("2.10","10.10")).isNegative();
        assertThat( VersionUtils.compareVersion("10.10","2.10")).isPositive();
    }

    @Test
    void compareVersionTest3Digits(){
        assertThat( VersionUtils.compareVersion("1.0.0","2.0.0")).isNegative();
        assertThat( VersionUtils.compareVersion("1.0.0","1.0.0")).isZero();
        assertThat( VersionUtils.compareVersion("2.0.0","1.0.0")).isPositive();
        assertThat( VersionUtils.compareVersion("2.0.1","1.2.0")).isPositive();
        assertThat( VersionUtils.compareVersion("2.0.0","10.0.0")).isNegative();
        assertThat( VersionUtils.compareVersion("10.0.0","2.10.0")).isPositive();
        assertThat( VersionUtils.compareVersion("2.10.0","10.10.0")).isNegative();
        assertThat( VersionUtils.compareVersion("10.10.0","2.10.0")).isPositive();
    }
    
    @Test
    void compareVersionTestMixedDigits(){
        assertThat( VersionUtils.compareVersion("2.0.1","1.2")).isPositive();
        assertThat( VersionUtils.compareVersion("1.0.0","2.0")).isNegative();
        assertThat( VersionUtils.compareVersion("1.0.0","1.0")).isZero();
        assertThat( VersionUtils.compareVersion("2.0.0","1.0")).isPositive();
        assertThat( VersionUtils.compareVersion("1.0","2.0.0")).isNegative();
        assertThat( VersionUtils.compareVersion("1.0","1.0.0")).isZero();
        assertThat( VersionUtils.compareVersion("2.0","1.0.0")).isPositive();
        assertThat( VersionUtils.compareVersion("2.0","10.0.0")).isNegative();
        assertThat( VersionUtils.compareVersion("2.0.0","10.0")).isNegative();
        assertThat( VersionUtils.compareVersion("10.0","2.10.0")).isPositive();
        assertThat( VersionUtils.compareVersion("10.0.0","2.10")).isPositive();
        assertThat( VersionUtils.compareVersion("2.10","10.10.0")).isNegative();
        assertThat( VersionUtils.compareVersion("2.10.0","10.10")).isNegative();
        assertThat( VersionUtils.compareVersion("10.10","2.10.0")).isPositive();
        assertThat( VersionUtils.compareVersion("10.10.0","2.10")).isPositive();
    }

    @Test
    void getSlNumberFromTlTest() {
        assertThat( VersionUtils.getSlVersionNumber("1.0.1")).hasToString("1.0.0");
        assertThat( VersionUtils.getSlVersionNumber("1.0")).hasToString("1.0.0");
        assertThat( VersionUtils.getSlVersionNumber("10.0.1")).hasToString("10.0.0");
        assertThat( VersionUtils.getSlVersionNumber("10.0")).hasToString("10.0.0");
    }

    @Test
    void increaseSlVersionByOneTest(){
        assertThat( VersionUtils.increaseSlVersionNumber("1.0")).isEqualTo("1.1.0");
        assertThat( VersionUtils.increaseSlVersionNumber("1.10")).isEqualTo("1.11.0");
        assertThat( VersionUtils.increaseSlVersionNumber("1.0.0")).isEqualTo("1.1.0");
        assertThat( VersionUtils.increaseSlVersionNumber("1.10.0")).isEqualTo("1.11.0");
        assertThat( VersionUtils.increaseSlVersionNumber("10.0")).isEqualTo("10.1.0");
        assertThat( VersionUtils.increaseSlVersionNumber("10.10")).isEqualTo("10.11.0");
        assertThat( VersionUtils.increaseSlVersionNumber("10.0.0")).isEqualTo("10.1.0");
        assertThat( VersionUtils.increaseSlVersionNumber("10.10.0")).isEqualTo("10.11.0");
    }

    @Test
    void increaseTlVersionByOneTest(){
        assertThat( VersionUtils.increaseTlVersionByOne("1.0.2", "2.0")).isEqualTo("2.0.1");
        assertThat( VersionUtils.increaseTlVersionByOne("2.0.1", "2.0")).isEqualTo("2.0.2");
        assertThat( VersionUtils.increaseTlVersionByOne("1.0.2", "2.0.0")).isEqualTo("2.0.1");
        assertThat( VersionUtils.increaseTlVersionByOne("2.0.1", "2.0.0")).isEqualTo("2.0.2");
        assertThat( VersionUtils.increaseTlVersionByOne("10.0.2", "20.0")).isEqualTo("20.0.1");
        assertThat( VersionUtils.increaseTlVersionByOne("20.0.1", "20.0")).isEqualTo("20.0.2");
        assertThat( VersionUtils.increaseTlVersionByOne("10.0.2", "20.0.0")).isEqualTo("20.0.1");
        assertThat( VersionUtils.increaseTlVersionByOne("20.0.1", "20.0.0")).isEqualTo("20.0.2");
    }

    @Test
    void getBaseVersionUriTest(){
        assertThat( VersionUtils.getBaseVersionUri("https://vocabularies.cessda.eu/aaa/sq/1.0.1", "aaa")).isEqualTo("https://vocabularies.cessda.eu");
    }

    @Test
    void splitLanguageVersionTest(){
        String[] methodOutput1 = {"2.1", "2.1", "en", "SL"};
        assertThat( VersionUtils.splitLanguageVersion("en-2.1")).isEqualTo(methodOutput1);

        String[] methodOutput2 = {"2.0", "2.0.1", "de", "TL"};
        assertThat( VersionUtils.splitLanguageVersion("de-2.0.1")).isEqualTo(methodOutput2);
    }
}
