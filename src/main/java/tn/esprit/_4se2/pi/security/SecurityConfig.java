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

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/password/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers("/api/cart/**").permitAll() 
                        .requestMatchers("/api/favorites/check/**").permitAll()
                        .requestMatchers("/api/favorites/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/sponsored/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/sponsored/impression").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/sponsored/click/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/sponsored/purchase/**").permitAll()

                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/users/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/users/*/profile-image").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/*/profile-image").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/*/profile-image/content").authenticated()

                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers("/api/field-owners/**").hasRole("FIELD_OWNER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/players/**").hasAnyRole("ADMIN", "PLAYER")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/players/**").hasRole("PLAYER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/players/**").hasRole("PLAYER")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/players/**").hasRole("PLAYER")


                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/teams",
                                "/api/teams/*",
                                "/api/teams/*/members",
                                "/api/teams/*/posts"
                        ).authenticated()

                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/communities/**",
                                "/api/community/posts",
                                "/api/posts/*/comments"
                        ).authenticated()

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/communities/*/posts"
                        ).authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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