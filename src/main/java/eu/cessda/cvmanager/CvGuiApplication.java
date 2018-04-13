package eu.cessda.cvmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Description;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.vaadin.spring.i18n.MessageProvider;
import org.vaadin.spring.i18n.ResourceBundleMessageProvider;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan(basePackages = { "org.gesis.wts", "eu.cessda.cvmanager" })
@EnableJpaRepositories(basePackages = { "org.gesis.wts.repository", "eu.cessda.cvmanager.repository" })
@EnableElasticsearchRepositories(basePackages = { "eu.cessda.cvmanager.repository.search" })
@EntityScan(basePackages = { "org.gesis.wts", "eu.cessda.cvmanager" })
public class CvGuiApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CvGuiApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CvGuiApplication.class);
	}

	@Bean
	MessageProvider messageProvider() {
		return new ResourceBundleMessageProvider("i18n.messages", "UTF-8");
	}
	
	@Bean
	@Description("Thymeleaf template resolver serving HTML")
	public ClassLoaderTemplateResolver emailTemplateResolver() {
	    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
	    templateResolver.setPrefix("templates/");
	    templateResolver.setSuffix(".html");
	    templateResolver.setTemplateMode("HTML");
	    templateResolver.setCharacterEncoding("UTF_8");
	    templateResolver.setCacheable(false);
	    return templateResolver;
	}
	
}
