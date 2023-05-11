package br.com.projetodifm.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.projetodifm.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationProvider authProvider;

    @Autowired
    private JwtAuthenticationFilter authFilter;
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers(
                            "/auth/**",
                            "/auth/refresh/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**").permitAll()
            .requestMatchers("/api/**").authenticated()
            .requestMatchers("/users").denyAll()
            .and()
            .cors()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authProvider)
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
