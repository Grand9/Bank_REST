package com.example.bankcards.config;

import com.example.bankcards.security.JwtFilter;
import com.example.bankcards.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfig {

    private final UserService userService;

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/v1/api/card/add", "/v1/api/card/admin/update", "/v1/api/card/admin/{id}/{status}/update", "/v1/api/card/admin/{id}/delete").hasRole("ADMIN")
                        .requestMatchers("/v1/api/card/get/**", "/v1/api/card/block").hasRole("USER")
                        .requestMatchers("/v1/api/auth/login", "/v1/api/auth/refresh").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userService)
                .exceptionHandling(e -> {
                    e.accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(HttpStatus.UNAUTHORIZED.value()));
                    e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                })
                .logout(log -> {
                    log.logoutUrl("/logout");
                    log.addLogoutHandler((request, response, authentication) ->
                            response.setStatus(HttpStatus.UNAUTHORIZED.value()));
                    log.logoutSuccessHandler((request, response, authentication) ->
                            SecurityContextHolder.clearContext());
                });
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(
                List.of("http://localhost:3000",
                        "https://landing-url-1",
                        "https://landing-url-2",
                        "https://landing-url-3"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedMethods(List.of("*"));
        corsConfig.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource urlBasedConfig = new UrlBasedCorsConfigurationSource();
        urlBasedConfig.registerCorsConfiguration("/api/some-amazing-api/**", corsConfig);
        return urlBasedConfig;
    }
}