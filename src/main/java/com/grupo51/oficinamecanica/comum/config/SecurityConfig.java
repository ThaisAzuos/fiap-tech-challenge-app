package com.grupo51.oficinamecanica.comum.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF: Desabilitado porque usamos JWT stateless
                // NOTA: Em produção considerar um token CSRF específico
                .csrf(csrf -> csrf.disable())
                
                // Session: Stateless (sem sessão server-side)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Headers de Segurança (Proteção contra ataques comuns)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())                    // X-Frame-Options: DENY
                        .contentTypeOptions(cto -> cto.disable())              // X-Content-Type-Options: nosniff
                        .xssProtection(xss -> xss.disable())                   // Usar Content-Security-Policy em vez de X-XSS-Protection
                )
                
                // Autorização por URL
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, "/login").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/v3/api-docs").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll();
                    req.requestMatchers("/api/v1/atendentes/**").hasRole("ATENDENTE");
                    req.requestMatchers("/api/v1/gerentes/**").hasRole("GERENTE");
                    req.requestMatchers("/api/v1/mecanicos/**").hasRole("MECANICO");
                    req.anyRequest().authenticated();
                })
                
                // JWT Filter
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
