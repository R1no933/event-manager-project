package dev.baskakov.eventmanagerservice.security.configuration;

import dev.baskakov.eventmanagerservice.exception.CustomAccessDeniedHandler;
import dev.baskakov.eventmanagerservice.exception.CustomAuthenticationEntryPoint;
import dev.baskakov.eventmanagerservice.security.jwt.JwtTokenFilter;
import dev.baskakov.eventmanagerservice.user.utils.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    private final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources",
            "/webjars/**",
            "/configuration/ui",
            "/configuration/security",
            "/event-manager-openapi.yaml",
            "/api-docs/**",
            "/openapi.json",
            "/openapi.yaml"
    };

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenFilter  jwtTokenFilter;
    private final CustomAccessDeniedHandler  customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint  customAuthenticationEntryPoint;

    public SecurityConfiguration(
            CustomUserDetailsService customUserDetailsService,
            JwtTokenFilter jwtTokenFilter, CustomAccessDeniedHandler customAccessDeniedHandler, CustomAuthenticationEntryPoint customAuthenticationEntryPoint
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenFilter = jwtTokenFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a ->
                        a
                                .requestMatchers(SWAGGER_WHITELIST).permitAll()

                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users/auth").permitAll()
                                .requestMatchers(HttpMethod.GET, "/users/**").hasAnyAuthority("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/locations").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/locations/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/locations/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/locations/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/locations/**").hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.POST, "/events").hasAuthority("USER")
                                .requestMatchers(HttpMethod.DELETE, "/events/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.GET, "/events/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/events/**").hasAnyAuthority("ADMIN", "USER")

                                .requestMatchers(HttpMethod.POST, "/events/search").hasAnyAuthority("ADMIN", "USER")

                                .requestMatchers(HttpMethod.GET, "/events/my").hasAuthority("USER")

                                .requestMatchers(HttpMethod.POST, "/events/registrations/**").hasAuthority("USER")
                                .requestMatchers(HttpMethod.DELETE, "/events/registrations/cancel/**").hasAuthority("USER")
                                .requestMatchers(HttpMethod.GET, "/events/registrations/my").hasAuthority("USER")

                                .anyRequest()
                                .authenticated()
                )
                .exceptionHandling(ex ->
                        ex
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(jwtTokenFilter, AnonymousAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
