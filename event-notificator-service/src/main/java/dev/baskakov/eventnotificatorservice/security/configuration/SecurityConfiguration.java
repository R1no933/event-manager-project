package dev.baskakov.eventnotificatorservice.security.configuration;

import dev.baskakov.eventnotificatorservice.exceptions.CustomAccessDeniedHandler;
import dev.baskakov.eventnotificatorservice.exceptions.CustomAuthenticationEntryPoint;
import dev.baskakov.eventnotificatorservice.security.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
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

    private final JwtTokenFilter jwtTokenFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfiguration(
            JwtTokenFilter jwtTokenFilter,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint
    ) {
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
                                .requestMatchers(HttpMethod.GET, "/notifications").hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/notifications").hasAnyAuthority("USER", "ADMIN"))
                .exceptionHandling(ex ->
                        ex
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(jwtTokenFilter, AnonymousAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
