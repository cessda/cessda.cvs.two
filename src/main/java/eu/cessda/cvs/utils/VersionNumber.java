package eu.cessda.cvs.utils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VersionNumber implements Comparable<VersionNumber>, Serializable {

    private static final long serialVersionUID = 1L;
    
    public static Pattern PARSE_PATTERN = Pattern.compile("^[^0-9]*([0-9]+)\\.([0-9]+)(?:\\.([0-9]+))?.*$");
    
    public static VersionNumber fromString(String str) {
        if (str == null) {
            return null;
        }
        Matcher m = PARSE_PATTERN.matcher(str);
        if (m.find()) {
            VersionNumber v = new VersionNumber();
            v.slMajorNumber = Integer.parseInt(m.group(1));
            v.slMinorNumber = Integer.parseInt(m.group(2));
            v.tlNumber = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
            return v;
        } else {
            throw new IllegalArgumentException("Illegal version number provided '" + str + "'");
        }
    }

    private Integer slMajorNumber;
    private Integer slMinorNumber;
    private Integer tlNumber;
    
    public VersionNumber() {
        slMajorNumber = slMinorNumber = tlNumber = null;
    }

    public VersionNumber(String str) {
        this(VersionNumber.fromString(str));
    }

    public VersionNumber(Integer slMajorNumber, Integer slMinorNumber, Integer tlNumber) {
        this.slMajorNumber = slMajorNumber;
        this.slMinorNumber = slMinorNumber;
        this.tlNumber = tlNumber;
    }

    public VersionNumber(Integer slMajorNumber, Integer slMinorNumber) {
        this(slMajorNumber, slMinorNumber, 0);
    }

    public VersionNumber(VersionNumber other) {
        this(other.slMajorNumber, other.slMinorNumber, other.tlNumber);
    }

    public VersionNumber(VersionNumber slVersionNumber, Integer tlVersionNumber) {
        this(slVersionNumber.slMajorNumber, slVersionNumber.slMinorNumber, tlVersionNumber);
    }

    public Integer getSlMajorNumber() {
        return slMajorNumber;
    }

    public Integer getSlMinorNumber() {
        return slMinorNumber;
    }

    public Integer getTlNumber() {
        return tlNumber;
    }

    @JsonIgnore
    public String getSlMajorMinorNumbers() {
        return slMajorNumber + "." + slMinorNumber;
    }

    @JsonIgnore
    public VersionNumber getSlNumber() {
        return new VersionNumber(slMajorNumber, slMinorNumber, 0);
    }

    public String toString() {
        return slMajorNumber + "." + slMinorNumber + "." + tlNumber;
    }

    @Override
    public int compareTo(VersionNumber other) {
        if (this ==  other)
            return 0;
        if (other == null)
            return 1;
        int cmp = slMajorNumber.compareTo(other.slMajorNumber);
        if (cmp != 0)
            return cmp;
        cmp = slMinorNumber.compareTo(other.slMinorNumber);
        if (cmp != 0)
            return cmp;
        return tlNumber.compareTo(other.tlNumber);
    }

    public boolean isSameSlNumberAs(VersionNumber other) {
        if (other != null) {
            return slMajorNumber == other.slMajorNumber && slMinorNumber == other.slMinorNumber;
        }
        return false;
    }

    public boolean isSlNumber() {
        return tlNumber == 0;
    }

    public VersionNumber increaseSlNumber() {
        return new VersionNumber(slMajorNumber, slMinorNumber + 1, 0);
    }    

    public VersionNumber increaseTlNumber() {
        return new VersionNumber(slMajorNumber, slMinorNumber, tlNumber + 1);
    }    

    public VersionNumber increaseTl(VersionNumber currentSlNumber) {
        if (compareTo(currentSlNumber) == -1) {
            return new VersionNumber(currentSlNumber.getSlNumber(), 1);
        } else {
            return this.increaseTlNumber();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((slMajorNumber == null) ? 0 : slMajorNumber.hashCode());
        result = prime * result + ((slMinorNumber == null) ? 0 : slMinorNumber.hashCode());
        result = prime * result + ((tlNumber == null) ? 0 : tlNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VersionNumber other = (VersionNumber) obj;
        if (slMajorNumber == null) {
            if (other.slMajorNumber != null)
                return false;
        } else if (!slMajorNumber.equals(other.slMajorNumber))
            return false;
        if (slMinorNumber == null) {
            if (other.slMinorNumber != null)
                return false;
        } else if (!slMinorNumber.equals(other.slMinorNumber))
            return false;
        if (tlNumber == null) {
            if (other.tlNumber != null)
                return false;
        } else if (!tlNumber.equals(other.tlNumber))
            return false;
        return true;
    }

    @JsonIgnore
    public boolean isValid() {
        return (slMajorNumber != null || slMinorNumber != null || tlNumber != null) && slMajorNumber >= 1;
    }
}
