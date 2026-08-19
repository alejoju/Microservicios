package mx.com.adrian.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.com.adrian.demo.model.AuthResponse;
import mx.com.adrian.demo.model.LoginRequest;
import mx.com.adrian.demo.model.RefreshTokenRequest;
import mx.com.adrian.demo.service.JwtService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/producer/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Endpoints para login y refresh token")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion", description = "Autentica al usuario y devuelve access token y refresh token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Bearer", jwtExpiration));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token", description = "Usa un refresh token valido para obtener un nuevo access token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        String username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(refreshToken, user)) {
                String newAccessToken = jwtService.generateAccessToken(user);
                return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken, "Bearer", jwtExpiration));
            }
        }
        throw new RuntimeException("Refresh token invalido o expirado");
    }
}