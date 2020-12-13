package eu.cessda.cvs.web.rest.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Aggr implements Serializable {
    private String type;
    private String field;
    private List<String> values = new ArrayList<>();
    private List<Bucket> buckets = new ArrayList<>();
    private List<Bucket> filteredBuckets = new ArrayList<>();

    public Aggr() {
        // used for jackson serialization
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void setBucketFromMap(Map<String, Long> buckets) {
        List<Bucket> bucks = new ArrayList<>();
        buckets.forEach((k,v) -> {
            Bucket buck = new Bucket();
            buck.setK( k );
            buck.setV( v );
            bucks.add( buck );
        });
        this.buckets = bucks;
    }

    public List<Bucket> getFilteredBuckets() {
        return filteredBuckets;
    }

    public void setFilteredBuckets(List<Bucket> filteredBuckets) {
        this.filteredBuckets = filteredBuckets;
    }

    public void setFilteredBucketFromMap(Map<String, Long> buckets) {
        List<Bucket> bucks = new ArrayList<>();
        buckets.forEach((k,v) -> {
            Bucket buck = new Bucket();
            buck.setK( k );
            buck.setV( v );
            bucks.add( buck );
        });
        this.filteredBuckets = bucks;
    }
}
