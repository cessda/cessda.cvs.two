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
import java.util.List;

public class CvResult implements Serializable {
    private static final long serialVersionUID = -320284053587072983L;

    private final List<VocabularyDTO> vocabularies;
    private final long totalElements;
    private final int totalPage;
    private final int numberOfElements;
    private final int number;
    private final int size;
    private final boolean last;
    private final boolean first;
    private final List<Aggr> aggrs;

    public CvResult(
        List<VocabularyDTO> vocabularies,
        long totalElements,
        int totalPage,
        int numberOfElements,
        int number,
        int size,
        boolean last,
        boolean first,
        List<Aggr> aggrs
    ) {
        this.vocabularies = vocabularies;
        this.totalElements = totalElements;
        this.totalPage = totalPage;
        this.numberOfElements = numberOfElements;
        this.number = number;
        this.size = size;
        this.last = last;
        this.first = first;
        this.aggrs = aggrs;
    }

    public List<VocabularyDTO> getVocabularies() {
        return vocabularies;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPage() {
        return totalPage;
    }
    public int getNumberOfElements() {
        return numberOfElements;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isFirst() {
        return first;
    }

    public List<Aggr> getAggrs() {
        return aggrs;
    }
}
