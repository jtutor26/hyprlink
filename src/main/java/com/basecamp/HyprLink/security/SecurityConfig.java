package com.basecamp.HyprLink.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // This bean tells Spring to use BCrypt for hashing passwords securely
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                // 1. Allow public access to home, login, assets, AND the error page!
                .requestMatchers("/", "/login", "/register", "/register/check", "/css/**", "/images/**", "/error").permitAll()

                // 2. Allow public access to all profile pages
                .requestMatchers("/profile/**", "/templates").permitAll()

                // 3. Everything else requires a login (This MUST be the last rule!)
                .anyRequest().authenticated()
        )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .defaultSuccessUrl("/dashboard", true) // Where to go after successful login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}