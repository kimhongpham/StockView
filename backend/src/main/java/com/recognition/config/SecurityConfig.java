package com.recognition.config;

//import java.util.Arrays;
//
//import io.netty.handler.codec.http.HttpMethod;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import com.recognition.security.CustomAccessDeniedHandler;
//import com.recognition.security.JwtAuthenticationEntryPoint;
//import com.recognition.security.JwtAuthenticationFilter;
//
//import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//@RequiredArgsConstructor
@Profile("!dev")
public class SecurityConfig {

//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final CustomAccessDeniedHandler customAccessDeniedHandler;
//
//    @Value("${cors.allowed-origins:*}")
//    private String allowedOrigins;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // CSRF
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                        .ignoringRequestMatchers("/api/v1/auth/**", "/api/v1/webhooks/**"))
//
//                // CORS
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//
//                // Session Management
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                // Security Headers (kết hợp cả manual + built-in best practice)
//                .headers(headers -> headers
//                        .frameOptions().deny()
//                        .contentTypeOptions().and()
//                        .httpStrictTransportSecurity(hsts -> hsts
//                                .maxAgeInSeconds(31536000)
//                                .includeSubdomains(true))
//                        .addHeaderWriter((request, response) -> {
//                            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
//                        }))
//
//                // Authorization
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1/auth/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/assets/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/prices/**").permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        .requestMatchers("/actuator/health").permitAll()
//                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated())
//
//                // Exception Handling
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                        .accessDeniedHandler(customAccessDeniedHandler))
//
//                // JWT Filter
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        configuration.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
}

