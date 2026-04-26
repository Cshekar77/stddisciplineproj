package com.studentdiscipline.config;

import com.studentdiscipline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()

                // Admin endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Teacher endpoints
                .requestMatchers("/teacher/**").hasRole("TEACHER")

                // Student endpoints
                .requestMatchers("/student/**").hasRole("STUDENT")

                // ── CASE MANAGEMENT (Member 1 addition) ──────────────
                .requestMatchers("/api/cases/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/admin/case/**").hasRole("ADMIN")
                .requestMatchers("/teacher/case/**").hasRole("TEACHER")
                // ─────────────────────────────────────────────────────

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
