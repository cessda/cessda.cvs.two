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
package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.domain.MetadataValue;
import eu.cessda.cvs.domain.enumeration.ObjectType;

public class MetadataValueResourceUtil
{

    public static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    public static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    public static final String DEFAULT_VALUE = "AAAAAAAAAA";
    public static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final ObjectType DEFAULT_OBJECT_TYPE = ObjectType.AGENCY;
    private static final Long DEFAULT_OBJECT_ID = 1L;
    private static final Integer DEFAULT_POSITION = 1;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetadataValue createEntity() {
        return new MetadataValue()
            .identifier(DEFAULT_IDENTIFIER)
            .position(DEFAULT_POSITION)
            .value(DEFAULT_VALUE)
            .objectType(DEFAULT_OBJECT_TYPE)
            .objectId(DEFAULT_OBJECT_ID);
    }

    private MetadataValueResourceUtil()
    {
    }
}
