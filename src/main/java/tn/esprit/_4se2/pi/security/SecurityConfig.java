package tn.esprit._4se2.pi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth

<<<<<<< Updated upstream
                        // ── Public endpoints ──────────────────────────
                        .requestMatchers("/api/auth/**").permitAll()
=======
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // WebSocket/SockJS handshake endpoints
                        .requestMatchers("/ws", "/ws/**", "/ws-chat", "/ws-chat/**").permitAll()

                        // ── Public endpoints ──────────────────────────
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/password/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/matching/recommend/teams").permitAll()
>>>>>>> Stashed changes
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // ── Role-restricted admin/field-owner/player ──
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/teams").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers("/api/field-owners/**").hasRole("FIELD_OWNER")
<<<<<<< Updated upstream
                        .requestMatchers("/api/players/**").hasRole("PLAYER")
=======
                        // Allow admins to read players (needed for performance management)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/players/**").hasAnyRole("ADMIN", "PLAYER")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/players/**").hasAnyRole("ADMIN", "PLAYER", "FIELD_OWNER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/players/**").hasAnyRole("ADMIN", "PLAYER", "FIELD_OWNER")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/players/**").hasAnyRole("ADMIN", "PLAYER", "FIELD_OWNER")
>>>>>>> Stashed changes

                        // ── Team module — all authenticated ───────────
                        // Admin-only list for pending join requests
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/teams/join-requests",
                                "/api/team-members/requests"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PATCH,
                                "/api/teams/join-requests/*/approve"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/teams/join-requests/*/reject"
                        ).hasRole("ADMIN")

                        // Read-only team info is open to any logged-in user
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
<<<<<<< Updated upstream
                                "/api/teams",
                                "/api/teams/*",
                                "/api/teams/*/members",
                                "/api/teams/*/posts"
=======
                                "/api/teams/**"
                        ).authenticated()
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/teams/*/join-requests"
>>>>>>> Stashed changes
                        ).authenticated()

                        // All community GET endpoints open to authenticated users
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/communities/**",
                                "/api/communities/*/posts",
                                "/api/posts/*/comments",
                                "/api/matching/**"
                        ).authenticated()

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/matching/**"
                        ).authenticated()

                        // Post mutations — create post, like, comment
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/communities/*/posts",
                                "/api/posts/*/like",
                                "/api/posts/*/comments"
                        ).authenticated()

                        // All other requests require authentication
                        // (business-level role checks are in the service layer)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}