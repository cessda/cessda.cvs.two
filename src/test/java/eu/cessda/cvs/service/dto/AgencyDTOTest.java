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
package eu.cessda.cvs.service.dto;

import eu.cessda.cvs.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class AgencyDTOTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static AgencyDTO validAgency() {
        AgencyDTO agencyDTO = new AgencyDTO();
        agencyDTO.setName( "CESSDA" );
        agencyDTO.setLink( "https://www.cessda.eu/" );
        agencyDTO.setUri( "https://vocabularies.cessda.eu/vocabulary/[VOCABULARY]?v=[VERSION]" );
        agencyDTO.setUriCode( "https://vocabularies.cessda.eu/vocabulary/[VOCABULARY]_[CODE]?v=[VERSION]" );
        agencyDTO.setCanonicalUri( "urn:ddi:int.cessda.cv:" );
        return agencyDTO;
    }

    // all of them, as the validator reports violations in no particular order
    private static Set<String> invalidProperties( Consumer<AgencyDTO> change ) {
        AgencyDTO agencyDTO = validAgency();
        change.accept( agencyDTO );
        return validator.validate( agencyDTO ).stream()
            .map( ConstraintViolation::getPropertyPath )
            .map( Object::toString )
            .collect( Collectors.toSet() );
    }

    @Test
    void shouldAcceptACompleteAgency() {
        assertThat( validator.validate( validAgency() ) ).isEmpty();
    }

    @Test
    void shouldRequireTheNameAndLink() {
        assertThat( invalidProperties( a -> a.setName( " " ) ) ).containsExactly( "name" );
        assertThat( invalidProperties( a -> a.setLink( null ) ) ).containsExactly( "link" );
    }

    @Test
    void shouldRequireTheUris() {
        assertThat( invalidProperties( a -> a.setUri( null ) ) ).containsExactly( "uri" );
        assertThat( invalidProperties( a -> a.setUriCode( "" ) ) ).containsExactly( "uriCode" );
        assertThat( invalidProperties( a -> a.setCanonicalUri( null ) ) ).containsExactly( "canonicalUri" );
    }

    @Test
    void shouldRequireThePlaceholdersInTheUris() {
        assertThat( invalidProperties( a -> a.setUri( "https://a.example/[VERSION]" ) ) ).containsExactly( "uriWithPlaceholders" );
        assertThat( invalidProperties( a -> a.setUriCode( "https://a.example/[VOCABULARY]/[VERSION]" ) ) ).containsExactly( "uriCodeWithPlaceholders" );
        assertThat( invalidProperties( a -> a.setUriCode( "https://a.example/[CODE]/[VERSION]" ) ) ).containsExactly( "uriCodeWithPlaceholders" );
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgencyDTO.class);
        AgencyDTO agencyDTO1 = new AgencyDTO();
        agencyDTO1.setId(1L);
        AgencyDTO agencyDTO2 = new AgencyDTO();
        assertThat(agencyDTO1).isNotEqualTo(agencyDTO2);
        agencyDTO2.setId(agencyDTO1.getId());
        assertThat(agencyDTO1).isEqualTo(agencyDTO2);
        agencyDTO2.setId(2L);
        assertThat(agencyDTO1).isNotEqualTo(agencyDTO2);
        agencyDTO1.setId(null);
        assertThat(agencyDTO1).isNotEqualTo(agencyDTO2);
    }
}
