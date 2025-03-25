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
package eu.cessda.cvs.config;

import io.github.jhipster.config.JHipsterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebMvcConfigurer
{

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;

    private final ApplicationProperties applicationProperties;
    private final JHipsterProperties jHipsterProperties;
    private final WebProperties webProperties;

    public WebConfigurer( Environment env, ApplicationProperties applicationProperties, JHipsterProperties jHipsterProperties, WebProperties webProperties ) {
        this.env = env;
        this.applicationProperties = applicationProperties;
        this.jHipsterProperties = jHipsterProperties;
        this.webProperties = webProperties;
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        if (env.getActiveProfiles().length != 0) {
            log.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
        }
        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        initCachingHttpHeadersFilter(servletContext, disps);
        log.info("Web application fully configured");
    }

    /**
     * Customize the Servlet engine: Mime types, the document root, the cache.
     */
    @Override
    @ParametersAreNonnullByDefault
    public void addResourceHandlers( ResourceHandlerRegistry registry ) {
        // Configure mapping for upload directories
        if (applicationProperties.getStaticFilePath() != null)
        {
            registry.addResourceHandler( "/content/**" )
                .addResourceLocations( new PathResource( applicationProperties.getStaticFilePath() ), new ClassPathResource( "/static/content/" ) )
                .setCacheControl( webProperties.getResources().getCache().getCachecontrol().toHttpCacheControl() );
        }
    }

    /**
     * Initializes the caching HTTP Headers Filter.
     */
    private void initCachingHttpHeadersFilter(ServletContext servletContext,
                                              EnumSet<DispatcherType> disps) {
        log.debug("Registering Caching HTTP Headers Filter");
        FilterRegistration.Dynamic cachingHttpHeadersFilter =
            servletContext.addFilter("cachingHttpHeadersFilter", (request, response, chain) -> {
                    long cacheTimeToLive = TimeUnit.DAYS.toSeconds(jHipsterProperties.getHttp().getCache().getTimeToLiveInDays());

                    HttpServletResponse httpResponse = (HttpServletResponse) response;

                    httpResponse.setHeader("Cache-Control", "max-age=" + cacheTimeToLive + ", no-store");

                    // Setting Expires header, for proxy caching
                    httpResponse.setDateHeader("Expires", Instant.now().plusSeconds(cacheTimeToLive).toEpochMilli());

                    chain.doFilter(request, response);
                });

        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/i18n/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/app/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/v2/*");
        cachingHttpHeadersFilter.setAsyncSupported(true);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = jHipsterProperties.getCors();
        List<String> allowedOrigins = config.getAllowedOrigins();
        List<String> allowedOriginPatterns = config.getAllowedOriginPatterns();
        if ((allowedOrigins != null && !allowedOrigins.isEmpty()) || (allowedOriginPatterns != null && !allowedOriginPatterns.isEmpty())) {
            // Only the public APIs are registered
            log.debug("Registering CORS filters");
            source.registerCorsConfiguration( "/api/test-cors", config );
            source.registerCorsConfiguration( "/v2/**", config );
            source.registerCorsConfiguration( "/urn/**", config );
        }
        return new CorsFilter(source);
    }

}
