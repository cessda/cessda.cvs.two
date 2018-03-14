package eu.cessda.cvmanager;

import org.apache.commons.lang3.CharEncoding;
import org.gesis.wts.service.mapper.AgencyMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Description;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.vaadin.spring.i18n.MessageProvider;
import org.vaadin.spring.i18n.ResourceBundleMessageProvider;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan(basePackages = { "eu.cessda", "org.gesis" })
@EnableJpaRepositories(basePackages = { "eu.cessda", "org.gesis" })
@EntityScan(basePackages = { "eu.cessda", "org.gesis" })

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
		return new ResourceBundleMessageProvider("org.vaadin.spring.i18n.messages", "UTF-8");
	}
	
	@Bean
	@Description("Thymeleaf template resolver serving HTML 5")
	public ClassLoaderTemplateResolver emailTemplateResolver() {
	    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
	    templateResolver.setPrefix("templates/");
	    templateResolver.setSuffix(".html");
	    templateResolver.setTemplateMode("HTML5");
	    templateResolver.setCharacterEncoding(CharEncoding.UTF_8);
	    templateResolver.setCacheable(false);
	    return templateResolver;
	}
	
}
