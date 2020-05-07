package eu.cessda.cvs.web.rest.domain;

import eu.cessda.cvs.service.dto.VocabularyDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CvResult implements Serializable {
    private List<VocabularyDTO> vocabularies;
    private long totalElements;
    private int totalPage;
    private int numberOfElements;
    private int number;
    private int size;
    private boolean last;
    private boolean first;
    private List<Aggr> aggrs = new ArrayList<>();

    public List<VocabularyDTO> getVocabularies() {
        return vocabularies;
    }

    public void setVocabularies(List<VocabularyDTO> vocabularies) {
        this.vocabularies = vocabularies;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public List<Aggr> getAggrs() {
        return aggrs;
    }

    public void setAggrs(List<Aggr> aggrs) {
        this.aggrs = aggrs;
    }

    public CvResult addAggr(Aggr aggr) {
        this.aggrs.add(aggr);
        return this;
    }
}
