/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.cessda.cvs.config;

import eu.cessda.cvs.security.AuthoritiesConstants;
import eu.cessda.cvs.security.jwt.JWTConfigurer;
import eu.cessda.cvs.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

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

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/index.html")
            .antMatchers("/test/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf()
            .disable()
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
        .and()
            .headers()
            .contentSecurityPolicy("default-src 'self' https://cdn.matomo.cloud https://cessda.matomo.cloud https://analytics.cessda.eu http://cdn.matomo.cloud/cessda.matomo.cloud/matomo https://eosc-helpdesk.eosc-portal.eu/api/v1/form_config https://eosc-helpdesk.eosc-portal.eu/api/v1/form_submit; frame-src 'self' https://cdn.matomo.cloud https://cessda.matomo.cloud https://cessda.atlassian.net http://cdn.matomo.cloud/cessda.matomo.cloud data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.matomo.cloud https://cessda.matomo.cloud https://cessda.atlassian.net https://analytics.cessda.eu https://storage.googleapis.com http://cdn.matomo.cloud/cessda.matomo.cloud/matomo.js https://code.jquery.com/jquery-3.6.0.min.js https://eosc-helpdesk.eosc-portal.eu/assets/form/form.js; style-src 'self' https://fonts.googleapis.com https://eosc-helpdesk.eosc-portal.eu/assets/form/form.css 'unsafe-inline'; img-src 'self' https://cdn.matomo.cloud https://cessda.matomo.cloud https://cessda.atlassian.net https://analytics.cessda.eu http://cdn.matomo.cloud/cessda.matomo.cloud data:; font-src 'self' https://fonts.gstatic.com data:")
        .and()
            .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
        .and()
            .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
        .and()
            .frameOptions()
            .deny()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/activate").permitAll()
            .antMatchers("/v1/**").permitAll()
            .antMatchers("/v2/**").permitAll()
            .antMatchers("/api/search").permitAll()
            .antMatchers(HttpMethod.GET, "/api/agencies").permitAll()
            .antMatchers(HttpMethod.GET, "/api/metadata-fields/**").permitAll()
            .antMatchers("/api/search/**").permitAll()
            .antMatchers("/api/download").permitAll()
            .antMatchers("/api/download/**").permitAll()
            .antMatchers("/api/account/reset-password/init").permitAll()
            .antMatchers("/api/account/reset-password/finish").permitAll()
            .antMatchers("/api/**").authenticated()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/info").permitAll()
            .antMatchers("/management/prometheus").permitAll()
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
        .and()
            .apply(securityConfigurerAdapter());
        // @formatter:on
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
}
