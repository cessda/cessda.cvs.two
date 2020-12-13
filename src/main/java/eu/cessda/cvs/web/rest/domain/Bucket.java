package eu.cessda.cvs.web.rest.domain;

import java.io.Serializable;

public class Bucket implements Serializable {
    private String k;
    private Long v;

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public Long getV() {
        return v;
    }

    public void setV(Long v) {
        this.v = v;
    }
}
