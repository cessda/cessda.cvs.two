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
package eu.cessda.cvs.config;

import eu.cessda.cvs.CvsApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@AutoConfigureMockMvc
@SpringBootTest( classes = CvsApp.class )
class SecurityConfigurationIT
{
    private static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
    private static final String MATOMO = "https://analytics.cessda.eu";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowMatomoInContentSecurityPolicy() throws Exception
    {
        String policy = mockMvc.perform( get( "/" ) )
            .andExpect( header().exists( CONTENT_SECURITY_POLICY ) )
            .andReturn().getResponse().getHeader( CONTENT_SECURITY_POLICY );

        Map<String, List<String>> directives = parseDirectives( policy );

        // index.html loads matomo.js from the Matomo instance
        assertThat( directives.get( "script-src" ) ).contains( MATOMO );
        // the tracker falls back to an image request when it cannot send a beacon
        assertThat( directives.get( "img-src" ) ).contains( MATOMO );
        // the tracking requests to matomo.php fall under connect-src, which is not set and so falls back to default-src
        assertThat( directives ).doesNotContainKey( "connect-src" );
        assertThat( directives.get( "default-src" ) ).contains( MATOMO );
    }

    @Test
    void shouldNotAllowMatomoCloudInContentSecurityPolicy() throws Exception
    {
        String policy = mockMvc.perform( get( "/" ) )
            .andReturn().getResponse().getHeader( CONTENT_SECURITY_POLICY );

        assertThat( policy ).doesNotContain( "matomo.cloud" );
    }

    /**
     * Split a Content Security Policy into its directives, mapping each directive name to its sources.
     */
    private static Map<String, List<String>> parseDirectives( String policy )
    {
        Map<String, List<String>> directives = new HashMap<>();
        for ( String directive : policy.split( ";" ) )
        {
            List<String> parts = Arrays.asList( directive.trim().split( "\\s+" ) );
            directives.put( parts.get( 0 ), parts.subList( 1, parts.size() ) );
        }
        return directives;
    }
}
