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

import eu.cessda.cvs.security.AuthoritiesConstants;
import eu.cessda.cvs.security.jwt.JWTConfigurer;
import eu.cessda.cvs.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {

    private final TokenProvider tokenProvider;

    private final CorsFilter corsFilter;
    private final SecurityProblemSupport problemSupport;

    public SecurityConfiguration(TokenProvider tokenProvider, CorsFilter corsFilter, SecurityProblemSupport problemSupport) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.problemSupport = problemSupport;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring()
            .requestMatchers(HttpMethod.OPTIONS, "/**")
            .requestMatchers(regexMatcher("^/app/.*\\.(js|html)$"))
            .requestMatchers("/i18n/**")
            .requestMatchers("/content/**")
            .requestMatchers("/swagger-ui/index.html")
            .requestMatchers("/test/**");
    }

    @SuppressWarnings( "ProhibitedExceptionDeclared" )
    @Bean
    public SecurityFilterChain filterChain( HttpSecurity http ) throws Exception {
        // @formatter:off
        return http
            .csrf(csrf -> csrf
                .disable())
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport))
            .headers(headers -> headers
                .contentSecurityPolicy(policy -> policy
                    .policyDirectives("default-src 'self' https://cdn.matomo.cloud https://cessda.matomo.cloud https://analytics.cessda.eu http://cdn.matomo.cloud/cessda.matomo.cloud/matomo https://helpdesk.cessda.eu/api/v1/form_config https://helpdesk.cessda.eu/api/v1/form_submit; frame-src 'self' https://cdn.matomo.cloud https://cessda.matomo.cloud https://cessda.atlassian.net http://cdn.matomo.cloud/cessda.matomo.cloud data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.matomo.cloud https://cessda.matomo.cloud https://cessda.atlassian.net https://analytics.cessda.eu https://storage.googleapis.com http://cdn.matomo.cloud/cessda.matomo.cloud/matomo.js https://code.jquery.com/jquery-3.6.0.min.js https://helpdesk.cessda.eu/assets/form/form.js; style-src 'self' https://fonts.googleapis.com https://helpdesk.cessda.eu/assets/form/form.css 'unsafe-inline'; img-src 'self' https://cdn.matomo.cloud https://cessda.matomo.cloud https://cessda.atlassian.net https://analytics.cessda.eu http://cdn.matomo.cloud/cessda.matomo.cloud data:; font-src 'self' https://fonts.gstatic.com data:"))
                .referrerPolicy(policy -> policy
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .permissionsPolicyHeader(policy -> policy
                    .policy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'"))
                .frameOptions(options -> options
                    .deny()))
            .sessionManagement(management -> management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/api/authenticate").permitAll()
                .requestMatchers("/api/register").permitAll()
                .requestMatchers("/api/activate").permitAll()
                .requestMatchers("/v1/**").permitAll()
                .requestMatchers("/v2/**").permitAll()
                .requestMatchers("/api/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/agencies").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/metadata-fields/**").permitAll()
                .requestMatchers("/api/search/**").permitAll()
                .requestMatchers("/api/download").permitAll()
                .requestMatchers("/api/download/**").permitAll()
                .requestMatchers("/api/account/reset-password/init").permitAll()
                .requestMatchers("/api/account/reset-password/finish").permitAll()
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/management/health").permitAll()
                .requestMatchers("/management/info").permitAll()
                .requestMatchers("/management/prometheus").permitAll()
                .requestMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN_TECHNICAL)
                // the client application, static resources, the API documentation and /urn; authorizeRequests
                // allowed whatever no rule matched, authorizeHttpRequests denies it
                .anyRequest().permitAll())
            .with(securityConfigurerAdapter(), withDefaults())
            .build();
        // @formatter:on
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }

    /**
     * Configures the role hierarchy.
     */
    @Bean
    static RoleHierarchy roleHierarchy() {
         var roleHierarchy = new RoleHierarchyImpl();
         roleHierarchy.setHierarchy(
             """
             ROLE_ADMIN > ROLE_ADMIN_CONTENT
             ROLE_ADMIN > ROLE_ADMIN_TECHNICAL
             ROLE_ADMIN_CONTENT > ROLE_USER\s
             ROLE_ADMIN_TECHNICAL > ROLE_USER
             ROLE_ADMIN > ROLE_USER
             ROLE_USER > ROLE_ANONYMOUS"""
         );
         return roleHierarchy;
    }
}
