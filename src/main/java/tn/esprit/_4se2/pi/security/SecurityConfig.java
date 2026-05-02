package tn.esprit._4se2.pi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tn.esprit._4se2.pi.security.jwt.JwtAuthFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;

    // ✅ SecurityFilterChain #1 - Pour /uploads/** SEULEMENT - PRIORITÉ MAXIMALE
    @Bean
    @Order(1)
    public SecurityFilterChain uploadSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/uploads/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }

    // ✅ SecurityFilterChain #2 - Pour tout le reste de l'application
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // ── Public endpoints ──────────────────────────
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/password/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // ── WebSocket upgrade paths (SockJS handshake) ─
                        .requestMatchers("/ws/**", "/ws-chat/**").permitAll()

                        // ── Authenticated users can update their own profile and upload images ──
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/users/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/users/*/profile-image").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/*/profile-image").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/*/profile-image/content").authenticated()

                        // ── Role-restricted admin/field-owner/player ──
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers("/api/field-owners/**").hasRole("FIELD_OWNER")
                        // Allow admins to read players (needed for performance management)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/players/**").hasAnyRole("ADMIN", "PLAYER")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/players/**").hasRole("PLAYER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/players/**").hasRole("PLAYER")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/players/**").hasRole("PLAYER")

                        // ── Team module — all authenticated ───────────
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/teams/**"
                        ).hasAnyRole("ADMIN", "PLAYER", "FIELD_OWNER")

                        // ── Community feed ────────────────────────────
                        .requestMatchers("/api/community/**").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}