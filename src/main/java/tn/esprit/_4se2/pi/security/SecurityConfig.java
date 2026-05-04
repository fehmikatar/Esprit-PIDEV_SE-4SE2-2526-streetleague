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

    // ✅ SecurityFilterChain #1 - Pour /uploads/** et WebSocket - PRIORITÉ MAXIMALE
    @Bean
    @Order(1)
    public SecurityFilterChain publicEndpointsSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/uploads/**", "/ws/**", "/ws-chat/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        
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
                        .requestMatchers("/api/products/recommendations/**").permitAll()
                        .requestMatchers("/api/sponsored/**").permitAll()
                        .requestMatchers("/api/health-profiles/**").permitAll()   // ← AJOUTER
                        .requestMatchers("/api/matching/**").permitAll()
                        .requestMatchers("/api/health/**").permitAll()
                        .requestMatchers("/api/health-profiles/*/activities/**").permitAll()  // ← AJOUTER
                        .requestMatchers("/api/health-profiles/user/*/activities/**").permitAll() // ← AJOUTER
                        .requestMatchers("/api/predict/**").permitAll()                           // ← AJOUTER
                        .requestMatchers("/api/chat/**").permitAll()                              // ← AJOUTER
                        .requestMatchers("/api/matching/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/feedbacks/space/*/owner").hasAnyRole("FIELD_OWNER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/feedbacks/owner/**").hasAnyRole("FIELD_OWNER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/feedbacks/*/reply").hasAnyRole("FIELD_OWNER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/feedbacks/*/owner").hasAnyRole("FIELD_OWNER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/feedbacks/*/owner/me").hasAnyRole("FIELD_OWNER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/feedbacks/summary").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/feedbacks/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/feedbacks").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/feedbacks/*").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/feedbacks/*").authenticated()
                        .requestMatchers("/api/cart/confirm-delivery/**").permitAll()


                        // ── WebSocket upgrade paths (SockJS handshake) ─
                        .requestMatchers("/ws/**", "/ws-chat/**").permitAll()

                        // ── Authenticated users can update their own profile and upload images ──
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/users/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/users/*/profile-image").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/*/profile-image").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/*/profile-image/content").authenticated()

                        // ── Role-restricted admin/field-owner/player ──
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/bookings/my-bookings").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/bookings/my-team-bookings").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/bookings/owner/**").hasAnyRole("FIELD_OWNER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/sport-spaces/owner/**").hasAnyRole("FIELD_OWNER", "ADMIN")
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
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
