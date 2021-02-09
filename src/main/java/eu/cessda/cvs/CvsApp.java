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

package eu.cessda.cvs;

import eu.cessda.cvs.config.ApplicationProperties;
import io.github.jhipster.config.DefaultProfileUtil;
import io.github.jhipster.config.JHipsterConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class CvsApp {

    private static final Logger log = LoggerFactory.getLogger(CvsApp.class);
    private static final String UTF_8 = "UTF-8";

    private final Environment env;

    public CvsApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes cvs.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not " +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    @SuppressWarnings("squid:S4823") // no security hotspot, the args are properly handled
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CvsApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }


    @Bean
    public SpringTemplateEngine templateEngine()
    {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver( xmlTemplateResolver() );
        templateEngine.addTemplateResolver( htmlTemplateResolver() );
        templateEngine.addTemplateResolver( stringTemplateResolver() );
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver()
    {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder( 1 );
        templateResolver.setResolvablePatterns( Collections.singleton( "html/*" ) );
        templateResolver.setPrefix( "templates/" );
        templateResolver.setSuffix( ".html" );
        templateResolver.setTemplateMode( TemplateMode.HTML );
        templateResolver.setCharacterEncoding( UTF_8 );
        templateResolver.setCacheable( false );
        return templateResolver;
    }

    private ITemplateResolver xmlTemplateResolver()
    {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder( 2 );
        templateResolver.setResolvablePatterns( Collections.singleton( "xml/*" ) );
        templateResolver.setPrefix( "templates/" );
        templateResolver.setSuffix( ".xml" );
        templateResolver.setTemplateMode( TemplateMode.XML );
        templateResolver.setCharacterEncoding( UTF_8 );
        templateResolver.setCacheable( false );
        return templateResolver;
    }

    private ITemplateResolver stringTemplateResolver()
    {
        final StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setOrder( 3 );
        // No resolvable pattern, will simply process as a String template everything not previously
        // matched
        templateResolver.setTemplateMode( TemplateMode.HTML );
        templateResolver.setCacheable( false );
        return templateResolver;
    }
}
