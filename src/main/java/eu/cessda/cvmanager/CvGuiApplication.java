package eu.cessda.cvmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.vaadin.spring.i18n.MessageProvider;
import org.vaadin.spring.i18n.ResourceBundleMessageProvider;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan(basePackages = { "org.gesis", "eu.cessda" })
@EnableJpaRepositories(basePackages = { "org.gesis" })
@EntityScan(basePackages = { "org.gesis" })

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
//
//	@Bean(name = "dataSource")
//	public DriverManagerDataSource securityDataSource() {
//		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
//
//		driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//		driverManagerDataSource.setUrl("jdbc:mysql://svko-glesd:3306/cvmanager-security?useSSL=false");
//		driverManagerDataSource.setUsername("root");
//		driverManagerDataSource.setPassword("GVM8Sj8FUxBf4gk");
//		return driverManagerDataSource;
//	}

}
