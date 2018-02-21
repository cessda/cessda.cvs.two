package eu.cessda.cvmanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends GlobalMethodSecurityConfiguration {

	//@Autowired
    //private CustomAuthenticationProvider authProvider;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //@formatter:off
        //auth.authenticationProvider( authProvider );
    	auth.userDetailsService(userDetailesServiceImpl()).passwordEncoder(encoder);
    }
    
	@Bean
	public UserDetailesServiceImpl userDetailesServiceImpl(){
		return new UserDetailesServiceImpl();
	}
    
	/**
	 * Create a Bean of the "AuthenticationManager" class.
	 * @return an instance of "AuthenticationManager".
	 * @throws Exception
	 */
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationManager();
    }
    
	/**
	 * Create a Bean of the "BCryptPasswordEncoder" class.
	 * @return an instance of "BCryptPasswordEncoder".
	 */
	@Bean( name = "passwordEncoder" )
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
    	return new BCryptPasswordEncoder();
    }

    static {
        // Use a custom SecurityContextHolderStrategy
        SecurityContextHolder.setStrategyName(VaadinSessionSecurityContextHolderStrategy.class.getName());
    }
}
