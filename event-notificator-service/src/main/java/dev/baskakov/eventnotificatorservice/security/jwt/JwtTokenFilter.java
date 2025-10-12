package dev.baskakov.eventnotificatorservice.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtTokenManager jwtTokenManager;

    public JwtTokenFilter(
            JwtTokenManager jwtTokenManager
    ) {
        this.jwtTokenManager = jwtTokenManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            logger.info("Authorization header not found");
            filterChain.doFilter(request, response);
            return;
        }

        var currentToken = header.substring(7);

        try {
            String loginFromToken = jwtTokenManager.getLoginFromToken(currentToken);
            String roleFromToken = jwtTokenManager.getUserRoleFromToken(currentToken);
            Long userId = jwtTokenManager.getUserIdFromToken(currentToken);
            logger.info("Success extract from token - login {}, role {}, userId {}",
                    loginFromToken,
                    roleFromToken,
                    userId);
            var simpleUser = new SimpleUser(userId, loginFromToken, roleFromToken);
            List<GrantedAuthority> authorities = convertUserRoleForAuthorities(roleFromToken);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            simpleUser,
                            null,
                            authorities
                    );
            SecurityContextHolder.getContext().setAuthentication(auth);
            logger.info("Success authorization for token: user {}", simpleUser);
        } catch (Exception ex) {
            logger.error("Invalid JWT Token", ex);
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> convertUserRoleForAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        authorities.add(new SimpleGrantedAuthority(role));
        logger.info("Success converting authorities for role {} to authorities {}", role, authorities);
        return authorities;
    }

    public record SimpleUser(
            Long id,
            String login,
            String role
    ) { }
}
