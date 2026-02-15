package com.example.knightWatch.config;
import com.example.knightWatch.controller.LichessSyncController;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("=== JWT Filter Start ===");
        log.debug("Request URI: {}", request.getRequestURI());
        if (request.getRequestURI().contains("/auth/refresh")) {
            log.debug("Refresh endpoint - skipping JWT validation");
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        log.debug("Authorization Header: {}", authHeader);
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.debug("Token extracted: {}...", token.substring(0, Math.min(20, token.length())));
            try {
                username = jwtUtil.extractUsername(token);
                log.debug("Username from token: {}", username);
            } catch (Exception e) {
                // Invalid token
                log.error("Error extracting username from token: {}", e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            log.warn("No Authorization header or doesn't start with Bearer");
        }

        if (username != null) {
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            log.debug("Existing authentication: {}", existingAuth != null ? existingAuth.getClass().getSimpleName() : "null");

            // Skip if already authenticated with real user (not anonymous)
            if (existingAuth != null && existingAuth.isAuthenticated()
                    && !(existingAuth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
                log.debug("User already authenticated as: {}, skipping JWT auth", existingAuth.getName());
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("Proceeding with JWT authentication");

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.debug("User details loaded: {}", userDetails.getUsername());

            if (jwtUtil.validateToken(token, userDetails)) {
                log.debug("Token is valid");

                // Validate token belongs to active session
                String tokenSessionId = jwtUtil.extractSessionId(token);
                log.debug("Token Session ID: {}", tokenSessionId);

                HttpSession session = request.getSession(false); // Don't create new session
                log.debug("Current Session: {}", session != null ? session.getId() : "null");

                if (session != null && session.getId().equals(tokenSessionId)) {
                    // Token is valid and belongs to this session
                    String sessionUsername = (String) session.getAttribute("username");
                    log.debug("Session Username: {}", sessionUsername);

                    if (username.equals(sessionUsername)) {
                        log.info("Session validation passed - Authenticating user");

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.info("Authentication set in SecurityContext");
                    } else {
                        log.error("Username mismatch - Token: {}, Session: {}", username, sessionUsername);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                } else {
                    log.error("Session validation failed - Token SessionID: {}, Actual SessionID: {}",
                            tokenSessionId, session != null ? session.getId() : "null");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Session expired or invalid");
                    return;
                }
            } else {
                log.error("Token validation failed - token is invalid or expired");
            }
        }

        log.debug("=== JWT Filter End ===");
        filterChain.doFilter(request, response);
    }
}