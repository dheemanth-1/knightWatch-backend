package com.example.knightWatch.controller;

import com.example.knightWatch.config.JwtUtil;
import com.example.knightWatch.dto.AuthRequest;
import com.example.knightWatch.dto.UserDTO;
import com.example.knightWatch.exceptions.UserAlreadyExistsException;
import com.example.knightWatch.model.User;
import com.example.knightWatch.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        userRepository.save(user);
        UserDTO dto = new UserDTO(user.getId(), user.getUsername(), user.getRoles(), user.getEmail());
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Create session first
            HttpSession session = httpRequest.getSession(true);
            String sessionId = session.getId();

            // Generate tokens with session ID
            String accessToken = jwtUtil.generateAccessToken(auth.getName(), sessionId);
            String refreshToken = jwtUtil.generateRefreshToken(auth.getName(), sessionId);

            // Store important data in Redis session
            session.setAttribute("username", auth.getName());
            session.setAttribute("refreshToken", refreshToken);
            session.setAttribute("loginTime", System.currentTimeMillis());
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            User user = userRepository.findByUsername(request.getUsername()).get();
            UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getRoles(), user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "user", userDTO,
                    "expiresIn", jwtUtil.getAccessTokenValidityInSeconds() // 15 minutes in seconds
            ));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String refreshToken = request.get("refreshToken");
        System.out.println("this is the goddamn refresh token :" + refreshToken);

        if (refreshToken == null) {
            log.error("No refersh token");
            return ResponseEntity.status(401).body("Refresh token is required");
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            String tokenSessionId = jwtUtil.extractSessionId(refreshToken);
            String tokenType = jwtUtil.extractTokenType(refreshToken);
            log.info("user " + username);
            log.info("token session id " + tokenSessionId);
            log.info("token type " + tokenType);
            // Verify it's a refresh token
            if (!"refresh".equals(tokenType)) {
                return ResponseEntity.status(401).body("Invalid token type");
            }

            // Get session and validate
            HttpSession session = httpRequest.getSession(false);
            log.info("Current Session: {}", session != null ? session.getId() : "NULL");
            if (session == null || !session.getId().equals(tokenSessionId)) {
                log.error("Invalid token type: {}", tokenType);
                return ResponseEntity.status(401).body("Session expired");
            }

            // Verify refresh token matches the one in session
            String storedRefreshToken = (String) session.getAttribute("refreshToken");
            if (!refreshToken.equals(storedRefreshToken)) {
                log.error("Refresh token mismatch");
                return ResponseEntity.status(401).body("Invalid refresh token");
            }

            // Generate new access token (keep same refresh token)
            String newAccessToken = jwtUtil.generateAccessToken(username, session.getId());
            log.info("✓ New access token generated successfully");
            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken,
                    "expiresIn", jwtUtil.getAccessTokenValidityInSeconds()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // This removes session from Redis
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }
}
