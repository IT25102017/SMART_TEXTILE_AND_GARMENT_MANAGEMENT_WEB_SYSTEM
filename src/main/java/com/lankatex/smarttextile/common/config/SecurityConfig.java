package com.lankatex.smarttextile.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // Pages and static resources that can be accessed without login
                        .requestMatchers(
                                "/",
                                "/home",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        // All other pages require authentication
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form

                        // Use our custom login page
                        .loginPage("/login")

                        // URL used to process the login form
                        .loginProcessingUrl("/login")

                        // Redirect to the home page after successful login
                        .defaultSuccessUrl("/", true)

                        // Redirect back to the login page if authentication fails
                        .failureUrl("/login?error=true")

                        .permitAll()
                )

                .logout(logout -> logout

                        // URL used to perform logout
                        .logoutUrl("/logout")

                        // Redirect to the login page after successful logout
                        .logoutSuccessUrl("/login?logout=true")

                        .permitAll()
                );

        return http.build();
    }
}