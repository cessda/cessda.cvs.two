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

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Describes the public API (v1, v2 and the URN resolver) in the OpenAPI document; which paths it covers is set by
 * {@code springdoc.paths-to-match}, the texts by the {@code api-docs} properties.
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI apiDocumentation(
        @Value( "${api-docs.title:}" ) String title,
        @Value( "${api-docs.description:}" ) String description,
        @Value( "${api-docs.version:}" ) String version,
        @Value( "${api-docs.terms-of-service-url:}" ) String termsOfServiceUrl,
        @Value( "${api-docs.contact-name:}" ) String contactName,
        @Value( "${api-docs.contact-url:}" ) String contactUrl,
        @Value( "${api-docs.contact-email:}" ) String contactEmail,
        @Value( "${api-docs.license:}" ) String license,
        @Value( "${api-docs.license-url:}" ) String licenseUrl
    ) {
        return new OpenAPI().info( new Info()
            .title( title )
            .description( description )
            .version( version )
            .termsOfService( termsOfServiceUrl.isEmpty() ? null : termsOfServiceUrl )
            .contact( new Contact().name( contactName ).url( contactUrl.isEmpty() ? null : contactUrl ).email( contactEmail.isEmpty() ? null : contactEmail ) )
            .license( new License().name( license ).url( licenseUrl.isEmpty() ? null : licenseUrl ) )
        );
    }
}
