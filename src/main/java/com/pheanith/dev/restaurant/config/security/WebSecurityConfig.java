package com.pheanith.dev.restaurant.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	private AuthEntryPointJWT unauthorizeHandler;

	@Bean
	public AuthTokenFilter authenticationJWTTokenfilter() {
		return new AuthTokenFilter();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(bCryptEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder bCryptEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizeHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/api/courses").permitAll()
						.requestMatchers("/api/courses/**").permitAll()
						.requestMatchers("/api/user").permitAll()
						.requestMatchers("/api/user/**").permitAll()
						.requestMatchers("/api/enrollments").permitAll()
						.requestMatchers("/api/enrollments/**").permitAll()
						.requestMatchers("/h2-console", "/h2-console/**").permitAll()
						.requestMatchers("/", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
						.requestMatchers("/api/v1/auth/**", "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**",
								"/swagger-resources", "/swagger-resources/**", "/configuration/ui",
								"/configuration/security", "/swagger-ui/**", "swagger-ui/index.html", "/webjars/**",
								"/swagger-ui.html", "/api/auth/**", "/api/test/**", "/authenticate",
								"/.well-known/appspecific/com.chrome.devtools.json", "/h2-console", "/test", "/error")
						.permitAll().anyRequest().authenticated());
		http.authenticationProvider(authenticationProvider());
		http.addFilterBefore(authenticationJWTTokenfilter(), UsernamePasswordAuthenticationFilter.class);
		http.cors();

		return http.build();
	}

}
