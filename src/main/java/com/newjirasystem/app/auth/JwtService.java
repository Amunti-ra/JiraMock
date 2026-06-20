package com.newjirasystem.app.auth;

import com.newjirasystem.app.usuarios.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long tiempoExpiracion;
    private final long tiempoExpiracionRefreshToken;

    public JwtService(@Value("${jira.jwt.secret}") String secretKey, @Value("${security.jwt.expiration-time}") long tiempoExpiracion, @Value("${security.jwt.refresh-expiration-time}") long tiempoExpiracionRefreshToken) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.tiempoExpiracion = tiempoExpiracion;
        this.tiempoExpiracionRefreshToken = tiempoExpiracionRefreshToken;
    }

    public String generarToken(Usuario usuario) {
        return buildToken(usuario, tiempoExpiracion);
    }

    public String generarRefreshToken(Usuario usuario) {
        return buildToken(usuario, tiempoExpiracionRefreshToken);
    }

    private String buildToken(Usuario usuario, long expiracion) {
        return Jwts.builder()
                .subject(usuario.getNombre())
                .claim("rol", usuario.getRol().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiracion))
                .signWith(secretKey)
                .compact();
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        Claims claims = obtenerClaims(token);

        boolean esUsuarioValido = userDetails.getUsername().equals(claims.getSubject());
        boolean esRolValido = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_" + claims.get("rol")));


        boolean esTokenExpirado = estaVigente(token);

        return esUsuarioValido && esRolValido && esTokenExpirado;
    }

    public boolean esRefreshTokenValido(String token, UserDetails usuario) {
        try {
            Claims claims = obtenerClaims(token);

            boolean esUsuarioValido = usuario.getUsername().equals(claims.getSubject());
            boolean estaVigente = estaVigente(token);
            boolean esRolValido = usuario.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority()
                            .equals("ROLE_" + claims.get("rol")));

            return esUsuarioValido && esRolValido && estaVigente;

        } catch (Exception e) {
            return false;
        }
    }

    public String obtenerNombreUsuario(String token) {
        return obtenerClaims(token).getSubject();
    }

    private boolean estaVigente(String token) {
        Claims claims = obtenerClaims(token);

        return claims.getExpiration().after(new Date());
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
