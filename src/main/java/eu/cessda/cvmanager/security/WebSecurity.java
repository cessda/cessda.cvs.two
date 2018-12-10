package eu.cessda.cvmanager.security;

//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.gesis.wts.security.SecurityConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//
//import static eu.cessda.cvmanager.security.SecurityConstants.IMPORT_URL;
//
////@Import(SecurityConfiguration.class)
//@Configuration
//@EnableWebSecurity
public class WebSecurity/* extends WebSecurityConfigurerAdapter*/ {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
////        	.cors().and().csrf().disable()
//            .antMatcher( IMPORT_URL ).authorizeRequests()
//            .anyRequest().authenticated()
//            .and()
//            .addFilter(new JWTAuthenticationFilter(authenticationManager()))
//            .addFilter(new JWTAuthorizationFilter(authenticationManager()));
//            // this disables session creation on Spring Security
////            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//    }
    
//  @Bean
//  CorsConfigurationSource corsConfigurationSource() {
//    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
//    return source;
//  }
}