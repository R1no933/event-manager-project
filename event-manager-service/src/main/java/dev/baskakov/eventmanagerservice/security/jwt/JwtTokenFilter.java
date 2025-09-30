package dev.baskakov.eventmanagerservice.security.jwt;

import dev.baskakov.eventmanagerservice.user.User;
import dev.baskakov.eventmanagerservice.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;

    public JwtTokenFilter(
            JwtTokenManager jwtTokenManager,
            @Lazy UserService userService
    ) {
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var currentToken = header.substring(7);
        String loginFromToken;

        try {
            loginFromToken = jwtTokenManager.getLoginFromToken(currentToken);
        } catch (Exception ex) {
            logger.error("Invalid JWT Token", ex);
            filterChain.doFilter(request, response);
            return;
        }

        User user = userService.findByLogin(loginFromToken);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority(user.role().toString()))
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(token);
        filterChain.doFilter(request, response);
    }
}
