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
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class VersionNumberTest
{
    @Test
    void createThreePartFromString() {
        String input = "1.2.3";

        var versionNumber = VersionNumber.fromString( input );

        // Assert all components are as expected
        assertThat( versionNumber.getMajorNumber() ).isEqualTo( 1 );
        assertThat( versionNumber.getMinorNumber() ).isEqualTo( 2 );
        assertThat( versionNumber.getPatchNumber() ).isEqualTo( 3 );
    }

    @Test
    void createTwoPartFromString() {
        String input = "1.2";

        var versionNumber = VersionNumber.fromString( input );

        // Assert all components are as expected
        assertThat( versionNumber.getMajorNumber() ).isEqualTo( 1 );
        assertThat( versionNumber.getMinorNumber() ).isEqualTo( 2 );
        assertThat( versionNumber.getPatchNumber() ).isZero();
    }

    @Test
    void failIfStringIsTooShort() {
        // Too short, major and minor parts are mandatory
        String input = "1";

        // Should throw IllegalArgumentException
        assertThatIllegalArgumentException().isThrownBy( () -> VersionNumber.fromString( input ) );
    }

    @Test
    void failIfInvalidCharacters() {
        // 3sa is not a valid number
        String input = "1.2.3sa";

        // Should throw IllegalArgumentException with a root cause of NumberFormatException
        assertThatIllegalArgumentException().isThrownBy( () -> VersionNumber.fromString( input ) )
            .withCauseInstanceOf( NumberFormatException.class );
    }

    @Test
    void failIfTooLong() {
        // Too long, versions only have 3 parts
        String input = "1.2.3.4";

        // Should throw IllegalArgumentException
        assertThatIllegalArgumentException().isThrownBy( () -> VersionNumber.fromString( input ) );
    }
}
