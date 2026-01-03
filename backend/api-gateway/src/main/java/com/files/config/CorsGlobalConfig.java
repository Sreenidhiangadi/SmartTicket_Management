package com.files.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class CorsGlobalConfig {

	   @Bean
	    public CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration config = new CorsConfiguration();
	        config.addAllowedOrigin("http://localhost:4200");
	        config.addAllowedMethod("*");
	        config.addAllowedHeader("*");
	        config.setAllowCredentials(true);

	        UrlBasedCorsConfigurationSource source =
	                new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", config);

	        return source;
	    }
}
