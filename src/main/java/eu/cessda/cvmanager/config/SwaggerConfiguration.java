package eu.cessda.cvmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration
{
	@Bean
	public Docket docketBean()
	{
		return new Docket( DocumentationType.SWAGGER_2 )
				.useDefaultResponseMessages( false )
				.select()
				.apis( RequestHandlerSelectors.any() )
				.paths( PathSelectors.regex( "/.*" ) )
				.paths( Predicates.not(PathSelectors.regex("/error.*")) )
				.paths( Predicates.not(PathSelectors.regex("/actuator.*")) )
				.build()
				.apiInfo(getApiInformation());
	}

	private ApiInfo getApiInformation(){
		return new ApiInfo("CVS REST APIs",
				"List of CVS rest APIs",
				"1.0",
				"https://www.cessda.eu/Acceptable-Use-Policy",
				new Contact("CESSDA", "https://www.cessda.eu/", "cessda@cessda.eu"),
				"Apache 2.0",
				"http://www.apache.org/licenses/LICENSE-2.0",
				Collections.emptyList()
		);
	}
}
