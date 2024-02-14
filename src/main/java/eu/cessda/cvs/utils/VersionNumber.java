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
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonSerialize(using = VersionNumber.Serializer.class)
@JsonDeserialize(using = VersionNumber.Deserializer.class)
public class VersionNumber implements Comparable<VersionNumber>, Serializable {

    private static final Comparator<Integer> nullSafeIntegerComparator = Comparator.nullsFirst(Integer::compareTo);

    public static class Serializer extends StdSerializer<VersionNumber> {

        private static final long serialVersionUID = 1L;

        public Serializer() {
            this(null);
        }

        public Serializer(Class<VersionNumber> t) {
            super(t);
        }

        @Override
        public void serialize(VersionNumber value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.toString());
        }

    }

    private static final long serialVersionUID = 1L;

    private static final Pattern parsePattern = Pattern.compile("^\\D*+(\\d++)\\.(\\d++)(?:\\.(\\d++))?.*+$");

    public static VersionNumber fromString(String str) {
        if (str == null) {
            return null;
        }
        Matcher m = parsePattern.matcher(str);
        if (m.find()) {
            int majorNumber = Integer.parseInt(m.group(1));
            int minorNumber = Integer.parseInt(m.group(2));
            int patchNumber = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
            return new VersionNumber(majorNumber, minorNumber, patchNumber);
        } else {
            throw new IllegalArgumentException("Illegal version number provided '" + str + "'");
        }
    }

    private final Integer majorNumber;
    private final Integer minorNumber;
    private final Integer patchNumber;

    public VersionNumber() {
        majorNumber = minorNumber = patchNumber = null;
    }

    public VersionNumber(Integer majorNumber, Integer minorNumber, Integer patchNumber) {
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.patchNumber = patchNumber;
    }

    public VersionNumber(Integer majorNumber, Integer minorNumber) {
        this(majorNumber, minorNumber, 0);
    }

    public VersionNumber(VersionNumber versionNumber, Integer patchNumber) {
        this(versionNumber.majorNumber, versionNumber.minorNumber, patchNumber);
    }

    public Integer getMajorNumber() {
        return majorNumber;
    }

    public Integer getMinorNumber() {
        return minorNumber;
    }

    public Integer getPatchNumber() {
        return patchNumber;
    }

    @JsonIgnore
    public String getMinorVersion() {
        return majorNumber + "." + minorNumber;
    }

    @JsonIgnore
    public VersionNumber getBasePatchVersion() {
        return new VersionNumber(majorNumber, minorNumber, 0);
    }

    public String toString() {
        return majorNumber + "." + minorNumber + "." + patchNumber;
    }

    public static String toString(VersionNumber versionNumber) {
        return versionNumber != null ? versionNumber.toString() : null;
    }

    public static class Deserializer extends StdDeserializer<VersionNumber> {

        private static final long serialVersionUID = 1L;

        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<VersionNumber> t) {
            super(t);
        }

        @Override
        public VersionNumber deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return VersionNumber.fromString(p.getText());
        }
    }

    private static final Comparator<VersionNumber> minorVersionNumberComparator = Comparator
        .comparing(
            VersionNumber::getMajorNumber, nullSafeIntegerComparator
        ).thenComparing(
            VersionNumber::getMinorNumber, nullSafeIntegerComparator
        );

    private static final Comparator<VersionNumber> versionNumberComparator = minorVersionNumberComparator
        .thenComparing(
            VersionNumber::getPatchNumber, nullSafeIntegerComparator
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
     * and the patch version number set to 0.
     * @implNote if minorNumber is {@code null}, it is treated as if it is 0.
     */
    public VersionNumber increaseMinorNumber() {
        return new VersionNumber(majorNumber,
            Objects.requireNonNullElse(minorNumber, 0) + 1,
            0
        );
    }

    /**
     * Returns a new VersionNumber with the patch version number incremented by 1.
     * @implNote if patchNumber is {@code null}, it is treated as if it is 0.
     */
    public VersionNumber increasePatchNumber() {
        return new VersionNumber(majorNumber, minorNumber,
            Objects.requireNonNullElse(patchNumber, 0) + 1
        );
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
