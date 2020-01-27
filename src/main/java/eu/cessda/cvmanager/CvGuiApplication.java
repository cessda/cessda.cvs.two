package eu.cessda.cvmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.vaadin.spring.i18n.MessageProvider;
import org.vaadin.spring.i18n.ResourceBundleMessageProvider;

import java.util.Collections;

@SpringBootApplication( exclude = { SecurityAutoConfiguration.class, ThymeleafAutoConfiguration.class } )
@ComponentScan( basePackages = { "org.gesis.wts", "eu.cessda.cvmanager" } )
@EnableJpaRepositories( basePackages = { "org.gesis.wts.repository", "eu.cessda.cvmanager.repository" } )
@EnableElasticsearchRepositories( basePackages = { "eu.cessda.cvmanager.repository.search" } )
@EntityScan( basePackages = { "org.gesis.wts", "eu.cessda.cvmanager" } )
public class CvGuiApplication extends SpringBootServletInitializer
{

    private static final String UTF_8 = "UTF-8";

	public static void main( String[] args )
	{
		if ( args.length == 0 )
			SpringApplication.run( CvGuiApplication.class, args );
	}

	@Override
	protected SpringApplicationBuilder configure( SpringApplicationBuilder application )
	{
		return application.sources( CvGuiApplication.class );
	}

	@Bean
	MessageProvider messageProvider()
	{
		return new ResourceBundleMessageProvider( "i18n.messages", UTF_8 );
	}

	@Bean
	public TemplateEngine templateEngine()
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
