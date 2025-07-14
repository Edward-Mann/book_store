package com.bnpparibasfortis.book_store.security;

import com.bnpparibasfortis.book_store.mapper.CustomerMapper;
import com.bnpparibasfortis.book_store.service.CustomerService;
import com.bnpparibasfortis.book_store.util.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Bookstore API.
 * Configures HTTP security with basic authentication and session management.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Defines the security filter chain for the application.
     *
     * @param http the HttpSecurity to modify
     * @return the configured SecurityFilterChain
     * @throws Exception on configuration error
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomAuthSuccessHandler customAuthSuccessHandler,
                                                   CustomAuthFailureHandler customAuthFailureHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                        .requestMatchers(AppConstants.REGISTER_URL, AppConstants.LOGIN_URL).permitAll()
                        .requestMatchers("/api/admin/**").hasRole(AppConstants.ADMIN)
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .httpBasic(httpBasic -> {})
                .formLogin(form -> form
                        .loginProcessingUrl(AppConstants.LOGIN_URL)
                        .successHandler(customAuthSuccessHandler)
                        .failureHandler(customAuthFailureHandler)
                        .permitAll()
                ).logout(logout -> logout
                        .logoutUrl(AppConstants.LOGOUT_URL)
                        .logoutSuccessUrl(AppConstants.LOGIN_URL)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );
        return http.build();
    }

    /**
     * customAuthFailureHandler bean for handling failed authentication.
     *
     * @return customAuthFailureHandler instance
     */
    @Bean
    public CustomAuthFailureHandler customAuthFailureHandler() {
        return new CustomAuthFailureHandler();
    }

    /**
     * customAuthSuccessHandler bean for handling successful authentication.
     *
     * @return CustomAuthSuccessHandler instance
     */
    @Bean
    public CustomAuthSuccessHandler customAuthSuccessHandler(CustomerService customerService, CustomerMapper customerMapper) {
        return new CustomAuthSuccessHandler(customerService, customerMapper);
    }


    /**
     * Password encoder bean for encoding and verifying passwords.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager bean for handling authentication.
     *
     * @param config the authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception on configuration error
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
