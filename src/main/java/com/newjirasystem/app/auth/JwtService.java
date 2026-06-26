package com.newjirasystem.app.auth;

import com.newjirasystem.app.config.JwtProperties;
import com.newjirasystem.app.usuarios.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final Clock clock;
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties, Clock clock) {
        this.jwtProperties = jwtProperties;
        this.clock = clock;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(Usuario usuario) {
        return buildToken(usuario, jwtProperties.expirationInMs(), "access_token");
    }

    public String generarRefreshToken(Usuario usuario) {
        return buildToken(usuario, jwtProperties.refreshExpirationInMs(), "refresh_token");
    }

    private String buildToken(Usuario usuario, int expiracion, String tipo) {
        Instant ahora = clock.instant();
        Instant fechaExpiracion = ahora.plusMillis(expiracion);

        return Jwts.builder()
                .subject(usuario.getNombre())
                .claim("rol", usuario.getRol().toString())
                .claim("type", tipo)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(fechaExpiracion))
                .signWith(secretKey)
                .compact();
    }

    public boolean esTokenVerificado(String token, UserDetails userDetails) {
        try {
            Claims claims = obtenerClaims(token);

            boolean esTipoAccessToken = claims.get("type").equals("access_token");
            boolean esUsuarioValido = userDetails.getUsername().equals(claims.getSubject());
            boolean esRolValido = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority()
                            .equals("ROLE_" + claims.get("rol")));


            return esUsuarioValido && esRolValido && esTipoAccessToken;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean esRefreshTokenVerificado(String token, UserDetails usuario) {
        try {
            Claims claims = obtenerClaims(token);

            boolean esTipoRefreshToken = claims.get("type").equals("refresh_token");
            boolean esUsuarioValido = usuario.getUsername().equals(claims.getSubject());
            boolean esRolValido = usuario.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority()
                            .equals("ROLE_" + claims.get("rol")));

            return esUsuarioValido && esRolValido && esTipoRefreshToken;

        } catch (Exception e) {
            return false;
        }
    }

    public String obtenerNombreUsuario(String token) {
        return obtenerClaims(token).getSubject();
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .clock(()-> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
