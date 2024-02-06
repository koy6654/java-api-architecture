package com.example.koy.util.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.koy.util.errors.handler.SecurityExceptionHandler;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	private final SecurityExceptionHandler securityExceptionHandler;

	public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
		SecurityExceptionHandler securityExceptionHandler) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.securityExceptionHandler = securityExceptionHandler;
	}

	// HTTP security
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(Customizer.withDefaults())

			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(JwtAuthenticationFilter.PUBLIC_ROUTERS)
				.permitAll()
				.anyRequest()
				.authenticated())

			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

			.exceptionHandling((exceptionCustomizer) -> {
				exceptionCustomizer.authenticationEntryPoint(securityExceptionHandler);
			});

		return httpSecurity.build();
	}
}
