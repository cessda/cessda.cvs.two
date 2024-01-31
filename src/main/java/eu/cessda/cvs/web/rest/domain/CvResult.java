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
package eu.cessda.cvs.web.rest.domain;

import eu.cessda.cvs.service.dto.VocabularyDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CvResult implements Serializable {
    private static final long serialVersionUID = 1607897901898491262L;

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
