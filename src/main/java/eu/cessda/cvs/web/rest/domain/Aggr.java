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

import java.io.Serializable;
import java.util.List;

public class Aggr implements Serializable {
    private static final long serialVersionUID = -7907701690323224787L;

    private final String type;
    private final String field;
    private final List<String> values;
    private final List<Bucket> buckets;
    private final List<Bucket> filteredBuckets;

    public Aggr(
        String type,
        String field,
        List<String> values,
        List<Bucket> buckets,
        List<Bucket> filteredBuckets
    ) {
        this.type = type;
        this.field = field;
        this.values = values;
        this.buckets = buckets;
        this.filteredBuckets = filteredBuckets;
    }

    public String getType() {
        return type;
    }

    public String getField() {
        return field;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public List<String> getValues() {
        return values;
    }

    public List<Bucket> getFilteredBuckets() {
        return filteredBuckets;
    }
}
