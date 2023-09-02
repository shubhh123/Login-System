package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.example.demo.utils.RSAKeyProperties;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/*
 * Refer this doc if got confused.Its my explanation.
 * Ok so when we hit the end point, /check/status the following happens:
   1. First it will first Look out for the class that has an annotation of @Configuration. In this case we have SecurityfilterChain.java.
   2. Then it calls the filterChain(HttpSecurity http) of my SecurityfilterChain.java. Inside of this we have a property:
   .authorizeHttpRequests(auth ->auth.anyRequest().authenticated())
   So this inherently calls the AuthenticationManager[Reason I don't know], which intern looks out for the UserDetailsService.
   3. Now it scans the class annotated with Service which is in this case UserService. This class implements the UserDetailsService, so it will be called.
   Inside of this I have a loadUserByUsername() method[How this gets called automatically, not clear...], which checks if the user by name "Shubham" exists. or not:
   if(!username.equals("Shubham")) 
			throw new UsernameNotFoundException("Username "+ username+ " not found");
   If not then throws an exception
   If the user has passed Username as "Shubham" and the  password as "Password", then it gets authenticated and an object of ApplicationUser is returned.
 */

@Configuration
public class SecurityConfiguration {

	
	private final RSAKeyProperties keys;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public SecurityConfiguration(RSAKeyProperties keys) {
		this.keys = keys;
	}
	
	
	//This is the main idea spring security checks if the user is authenticated or not
	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
		
		
		/*
		 * The DaoAuthenticationProvider uses the UserDetailsService to retrieve 
		 * user details (including password) based on the provided username.
		 */
		
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return new ProviderManager(daoAuthenticationProvider);
	}
	
	
//	@Bean
//	public SecurityFilterChain filterchain(HttpSecurity http) throws Exception {
//		return http 
//				.csrf(csrf-> csrf.disable())
//				.authorizeHttpRequests(auth -> {
//					auth.requestMatchers(mvc.pattern("/auth/**")).permitAll();  //allows any user to register or access the auth endpoint
//					auth.anyRequest().authenticated(); //every other request will be authenticated	
//				})
//				//this lines means pass in username and password in http form ad we will go and authenticate it
//				.httpBasic().and()
//				.build();
//	}
	
	
	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
	    return new MvcRequestMatcher.Builder(introspector);
	}

	
	@SuppressWarnings("removal")
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http.csrf(csrf -> csrf.disable())
           .authorizeHttpRequests(auth -> {
        	auth.requestMatchers(mvc.pattern("/auth/**")).permitAll();
        	auth.requestMatchers(mvc.pattern("/admin/**")).hasRole("ADMIN");
        	auth.requestMatchers(mvc.pattern("/user/**")).hasAnyRole("ADMIN", "USER");  //even admin can have a USER role
            auth.anyRequest().authenticated();
        });
           //this will configure an oauth resource server, and it will check with the JWT token.
           //so every time we send in a request that needs to be authenticated, it will look inside the bearer token authentication header
        
        http
           .oauth2ResourceServer()
           		.jwt()
           		.jwtAuthenticationConverter(jwtAuthenticationConverter());
        http
           .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        return http.build();
    }
	
	
	//this will take out the public key. Decoder will get the data out of the token
	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
		
	}
	
	
	//encoder will take that information, bundle it up with public and private key and return it.
	@Bean 
	public JwtEncoder jwtEncoder() {
		JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks) ;
	}
	
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtConverter = new JwtGrantedAuthoritiesConverter();
		
		/*
		 * Currently when we create a JWT token, that gets decoded by the backend, 
		 * its going to have a claim of "roles"
		 * So this roles knows everything about the User's role like "ADMIN", "USER", etc...
		 * But the problem with this is that, by default spring is gonna be looking for "ROLE_", so 
		 * we did: jwtConverter.setAuthorityPrefix("ROLE_");
		 * I not then spring dont know how to match a user against the role
		 * 
		 */
		jwtConverter.setAuthoritiesClaimName("roles");
		jwtConverter.setAuthorityPrefix("ROLE_");
		
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtConverter);
		return jwtAuthenticationConverter;
		
	}
	
}
