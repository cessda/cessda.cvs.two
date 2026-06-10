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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = VersionNumber.Deserializer.class)
public class VersionNumber implements Comparable<VersionNumber>, Serializable {

    private static final long serialVersionUID = 2L;

    /**
     * Parses a string into a version number. The string must either be a
     * two or three part version number.
     *
     * @param str the string to parse.
     * @return the version number represented by the string
     * @throws IllegalArgumentException if the string is invalid.
     */
    public static VersionNumber fromString(String str) {

        // Find last dot, also implicitly checks presence of dots
        int lastDot = str.lastIndexOf( '.' );

        // There must be at least one dot, fail if missing
        if (lastDot == -1) {
            throw new IllegalArgumentException("Illegal version number provided '" + str + "'");
        }

        // Version number components
        int[] versionArray = new int[]{-1 ,-1, -1};

        // Current string index
        int stringIndex = 0;

        // Current version array index
        int index = 0;

        // While there is more of the string remaining
        while ( stringIndex < str.length() )
        {
            // Find the next dot
            int nextDotIndex = str.indexOf( '.', stringIndex );

            // If nextDotIndex is -1, no more dots, read to the end of the string
            if (nextDotIndex == -1) {
                nextDotIndex = str.length();
            }

            try
            {
                // Store the component in the array
                versionArray[index++] = Integer.parseInt( str, stringIndex, nextDotIndex, 10 );
            }
            catch ( NumberFormatException e )
            {
                throw new IllegalArgumentException("Illegal version number provided: '" + str + "': " + e.getMessage(), e);
            }

            // break on end of input
            if (nextDotIndex == str.length() ) {
                break;
            }

            // If next index is longer than 3, string has more than 3 components. This is not allowed.
            if (index + 1 > 3) {
                throw new IllegalArgumentException("Illegal version number provided '" + str + "': Unexpected part '" + str.substring( nextDotIndex ) + "'");
            }

            // Set the string to nextDotIndex index + 1 (avoid the dot)
            stringIndex = nextDotIndex + 1;
        }

        // Check if the version number is valid
        if (versionArray[0] == -1 || versionArray[1] == -1) {
            throw new IllegalArgumentException("Illegal version number provided: '" + str + "'");
        }

        // Create a two or three part version number
        if (versionArray[2] != -1) {
            return new VersionNumber(versionArray[0], versionArray[1], versionArray[2]);
        } else {
            return new VersionNumber(versionArray[0], versionArray[1]);
        }
    }

    private final int majorNumber;
    private final int minorNumber;
    private final int patchNumber;

    public VersionNumber(int majorNumber, int minorNumber, int patchNumber) {
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.patchNumber = patchNumber;
    }

    public VersionNumber(int majorNumber, int minorNumber) {
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.patchNumber = 0;
    }

    public VersionNumber(VersionNumber versionNumber, int patchNumber) {
        this(versionNumber.majorNumber, versionNumber.minorNumber, patchNumber);
    }

    public int getMajorNumber() {
        return majorNumber;
    }

    public int getMinorNumber() {
        return minorNumber;
    }

    public int getPatchNumber() {
        return patchNumber;
    }

    @JsonIgnore
    public String getMinorVersion() {
        return majorNumber + "." + minorNumber;
    }

    @JsonIgnore
    public VersionNumber getBasePatchVersion() {
        return new VersionNumber(majorNumber, minorNumber);
    }

    public String toString() {
        return majorNumber + "." + minorNumber + "." + patchNumber;
    }

    public static class Deserializer extends FromStringDeserializer<VersionNumber> {

        private static final long serialVersionUID = -8397284193430713709L;

        public Deserializer()
        {
            super( VersionNumber.class );
        }

        @Override
        protected VersionNumber _deserialize( String value, DeserializationContext ctxt )
        {
            return VersionNumber.fromString(value);
        }
    }

    private static final Comparator<VersionNumber> minorVersionNumberComparator = Comparator
        .comparing(
            VersionNumber::getMajorNumber, Integer::compareTo
        ).thenComparing(
            VersionNumber::getMinorNumber, Integer::compareTo
        );

    private static final Comparator<VersionNumber> versionNumberComparator = minorVersionNumberComparator
        .thenComparing(
            VersionNumber::getPatchNumber, Integer::compareTo
        );

    @Override
    public int compareTo(VersionNumber other) {
        return versionNumberComparator.compare(this, other);
    }

    public boolean equalMinorVersionNumber(VersionNumber other) {
        return minorVersionNumberComparator.compare(this, other) == 0;
    }

    public boolean equalPatchVersionNumber(VersionNumber other) {
        return versionNumberComparator.compare(this, other) == 0;
    }

    /**
     * Returns a new VersionNumber with the minor version number incremented by 1,
     * and the patch version number set to {@code null}.
     */
    public VersionNumber increaseMinorNumber() {
        return new VersionNumber( majorNumber, minorNumber + 1 );
    }

    /**
     * Returns a new VersionNumber with the patch version number incremented by 1.
     * @implNote if patchNumber is {@code null}, it is treated as if it is 0.
     */
    public VersionNumber increasePatchNumber() {
        return new VersionNumber(majorNumber, minorNumber, patchNumber + 1 );
    }

    public VersionNumber increasePatch(VersionNumber currentSlNumber) {
        if (compareTo(currentSlNumber) < 0) {
            return new VersionNumber(currentSlNumber.getBasePatchVersion(), 1);
        } else {
            return this.increasePatchNumber();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorNumber, minorNumber, patchNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VersionNumber that = (VersionNumber) obj;
        return Objects.equals(majorNumber, that.majorNumber)
            && Objects.equals(minorNumber, that.minorNumber)
            && Objects.equals(patchNumber, that.patchNumber);
    }
}
