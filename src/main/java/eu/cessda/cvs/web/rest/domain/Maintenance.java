/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class Maintenance implements Serializable {
    private static final long serialVersionUID = 2L;

    private final String output;
    private final OffsetDateTime timestamp;
    private final Operation type;

    public Maintenance(String output, Operation type) {
        this.output = output;
        this.timestamp = OffsetDateTime.now(ZoneId.systemDefault());
        this.type = type;
    }

    public String getOutput() {
        return output;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public Operation getType() {
        return type;
    }

    public enum Operation {
        GENERATE_JSON, INDEX_AGENCY, INDEX_AGENCY_STAT, INDEX_VOCABULARY_PUBLISH, INDEX_VOCABULARY_EDITOR
    }
}
