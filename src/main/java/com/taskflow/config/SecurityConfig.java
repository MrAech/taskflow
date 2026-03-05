package com.taskflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures HTTP security rules.
     *
     *
     * For a stateless REST API with JWT this is acceptable, but it's not documented
     * and no JWT filter is present — for a session-based app this is a real vulnerability.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                .requestMatchers("/api/users/availability").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/admin/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(fo -> fo.sameOrigin())); // allow H2 console
        return http.build();
    }
}
