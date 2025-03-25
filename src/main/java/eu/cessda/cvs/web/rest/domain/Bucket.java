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

public class Bucket implements Serializable {
    private static final long serialVersionUID = -103627179503698977L;

    private final String k;
    private final Long v;

    public Bucket( String k, Long v )
    {
        this.k = k;
        this.v = v;
    }

    public String getK() {
        return k;
    }

    public Long getV() {
        return v;
    }
}
