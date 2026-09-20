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

                        // Login නැතුව බලන්න පුළුවන් pages/files
                        .requestMatchers(
                                "/",
                                "/home",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        // අනිත් pages වලට login ඕන
                        .anyRequest()
                        .authenticated()
                )

                // දැනට Spring Boot default login page එක use කරනවා
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}