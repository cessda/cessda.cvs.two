package eu.cessda.cvs.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@JsonSerialize(using = VersionNumber.Serializer.class)
@JsonDeserialize(using = VersionNumber.Deserializer.class)
public class VersionNumber implements Comparable<VersionNumber>, Serializable {

    private static final Logger log = LoggerFactory.getLogger(VersionNumber.class);
    
    public static class Deserializer extends StdDeserializer<VersionNumber> {
    
        private static final long serialVersionUID = 1L;

        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<VersionNumber> t) {
            super(t);
        }

        @Override
        public VersionNumber deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            try { 
                return VersionNumber.fromString(p.getText()); 
            } 
            catch (Exception e) { 
                log.error("Error deserializing version number", e);
            }    
            return null;
        }
    }

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
    
    public static Pattern PARSE_PATTERN = Pattern.compile("^[^0-9]*([0-9]+)\\.([0-9]+)(?:\\.([0-9]+))?.*$");
    
    public static VersionNumber fromString(String str) {
        if (str == null) {
            return null;
        }
        Matcher m = PARSE_PATTERN.matcher(str);
        if (m.find()) {
            VersionNumber v = new VersionNumber();
            v.majorNumber = Integer.parseInt(m.group(1));
            v.minorNumber = Integer.parseInt(m.group(2));
            v.patchNumber = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
            return v;
        } else {
            throw new IllegalArgumentException("Illegal version number provided '" + str + "'");
        }
    }

    private Integer majorNumber;
    private Integer minorNumber;
    private Integer patchNumber;
    
    public VersionNumber() {
        majorNumber = minorNumber = patchNumber = null;
    }

    public VersionNumber(String str) {
        this(VersionNumber.fromString(str));
    }

    public VersionNumber(Integer majorNumber, Integer minorNumber, Integer patchNumber) {
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.patchNumber = patchNumber;
    }

    public VersionNumber(Integer majorNumber, Integer minorNumber) {
        this(majorNumber, minorNumber, 0);
    }

    public VersionNumber(VersionNumber other) {
        this(other.majorNumber, other.minorNumber, other.patchNumber);
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

    @Override
    public int compareTo(VersionNumber other) {
        if (this == other)
            return 0;
        if (other == null)
            return 1;
        int cmp = majorNumber.compareTo(other.majorNumber);
        if (cmp != 0)
            return cmp;
        cmp = minorNumber.compareTo(other.minorNumber);
        if (cmp != 0)
            return cmp;
        return patchNumber.compareTo(other.patchNumber);
    }

    public boolean equalMinorVersionNumber(VersionNumber other) {
        if (other != null) {
            return majorNumber.equals(other.majorNumber) && minorNumber.equals(other.minorNumber);
        }
        return false;
    }

    public VersionNumber increaseMinorNumber() {
        return new VersionNumber(majorNumber, minorNumber + 1, 0);
    }    

    public VersionNumber increasePatchNumber() {
        return new VersionNumber(majorNumber, minorNumber, patchNumber + 1);
    }    

    public VersionNumber increasePatch(VersionNumber currentSlNumber) {
        if (compareTo(currentSlNumber) == -1) {
            return new VersionNumber(currentSlNumber.getBasePatchVersion(), 1);
        } else {
            return this.increasePatchNumber();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((majorNumber == null) ? 0 : majorNumber.hashCode());
        result = prime * result + ((minorNumber == null) ? 0 : minorNumber.hashCode());
        result = prime * result + ((patchNumber == null) ? 0 : patchNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }       
        if (obj == null) {
            return false;
        }        
        if (obj.getClass() == String.class) {
            return this.equals(VersionNumber.fromString((String) obj));
        }        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        VersionNumber other = (VersionNumber) obj;

        if ((majorNumber == null || other.majorNumber == null) && majorNumber != other.majorNumber) {
            return false;
        }
        if ((minorNumber == null || other.minorNumber == null) && minorNumber != other.minorNumber) {
            return false;
        }
        if ((patchNumber == null || other.patchNumber == null) && patchNumber != other.patchNumber) {
            return false;
        }

        if (!majorNumber.equals(other.majorNumber)) {
            return false;
        }
        if (!minorNumber.equals(other.minorNumber)) {
            return false;
        }
        if (!patchNumber.equals(other.patchNumber)) {
            return false;
        }

        return true;
    }
}
