package com.newjirasystem.app.auth;

import com.newjirasystem.app.config.JwtProperties;
import com.newjirasystem.app.usuarios.Rol;
import com.newjirasystem.app.usuarios.Usuario;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    private JwtService jwtService;

    private static final Instant FIXES_INSTANT = Instant.parse("2026-01-01T12:00:00Z");

    private JwtProperties props;

    @BeforeEach
    void setUp() {
        props = new JwtProperties(
                "clave_secreta_super_larga_y_segura_para_el_test_12345",
                6000,
                12000
        );
    }


    @Test
    void generarToken_ShouldGenerateValidAccessToken() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String token = jwtService.generarToken(usuario);

        assertNotNull(token);
        assertEquals(token, jwtService.generarToken(usuario));
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generarRefreshToken_ShouldGenerateValidRefreshToken() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String refreshToken = jwtService.generarRefreshToken(usuario);

        assertNotNull(refreshToken);
        assertEquals(3, refreshToken.split("\\.").length);
    }



    @Test
    void esTokenVerificado_WhenTokenIsValid_ShouldReturnTrue() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String token = jwtService.generarToken(usuario);

        assertTrue(jwtService.esTokenVerificado(token, usuario));
    }

    @Test
    void esTokenVerificado_WhenUsuarioDoesNotMatch_ShouldReturnFalse() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");
        Usuario otroUsuario = new Usuario("otroUsuario", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String token = jwtService.generarToken(usuario);

        assertFalse(jwtService.esTokenVerificado(token, otroUsuario));
    }

    @Test
    void esTokenVerificado_WhenTokenIsAccessToken_ShouldReturnTrue() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String token = jwtService.generarToken(usuario);

        assertTrue(jwtService.esTokenVerificado(token, usuario));
    }

    @Test
    void esTokenVerificado_WhenTokenIsRefreshToken_ShouldReturnFalse() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String token = jwtService.generarRefreshToken(usuario);

        assertFalse(jwtService.esTokenVerificado(token, usuario));
    }

    @Test
    void esTokenVerificado_WhenTokenHasBeenManipulated_ShouldReturnFalse() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String token = jwtService.generarToken(usuario);

        String tokenModificado = token.substring(0, token.length() - 1);

        assertFalse(jwtService.esTokenVerificado(tokenModificado, usuario));
    }

    @Test
    void esTokenVerificado_WhenTokenHasExpired_ShouldReturnFalse() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");
        JwtService creador = crearJwtService(FIXES_INSTANT);
        String token = creador.generarToken(usuario);

        Instant fechaExpirado =  Instant.parse("2026-01-01T12:15:01Z");
        JwtService validador = crearJwtService(fechaExpirado);

        assertFalse(validador.esTokenVerificado(token, usuario));
    }



    @Test
    void esRefreshTokenVerificado_WhenRefreshTokenIsValid_ShouldReturnTrue() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String refreshToken = jwtService.generarRefreshToken(usuario);

        assertTrue(jwtService.esRefreshTokenVerificado(refreshToken, usuario));
    }

    @Test
    void esRefreshTokenVerificado_WhenUsuarioDoesNotMatch_ShouldReturnFalse() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");
        Usuario otroUsuario = new Usuario("otroUsuario", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String refreshToken = jwtService.generarRefreshToken(usuario);

        assertFalse(jwtService.esRefreshTokenVerificado(refreshToken, otroUsuario));
    }

    @Test
    void esRefreshTokenVerificado_WhenRefreshTokenIsAccessToken_ShouldReturnFalse() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String token = jwtService.generarToken(usuario);

        assertFalse(jwtService.esRefreshTokenVerificado(token, usuario));
    }

    @Test
    void esRefreshTokenVerificado_WhenRefreshTokenIsRefreshToken_ShouldReturnTrue() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String refreshToken = jwtService.generarRefreshToken(usuario);

        assertTrue(jwtService.esRefreshTokenVerificado(refreshToken, usuario));
    }

    @Test
    void esRefreshTokenVerificado_WhenTokenHasBeenManipulated_ShouldReturnFalse() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String refreshToken = jwtService.generarRefreshToken(usuario);

        String refreshTokenModificado = refreshToken.substring(0, refreshToken.length() - 1);

        assertFalse(jwtService.esRefreshTokenVerificado(refreshTokenModificado, usuario));
    }

    @Test
    void esRefreshTokenVerificado_WhenTokenHasExpired_ShouldReturnFalse() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");
        JwtService creador = crearJwtService(FIXES_INSTANT);
        String refreshToken = creador.generarRefreshToken(usuario);

        Instant fechaExpirado =  Instant.parse("2026-01-02T12:00:01Z");
        JwtService validador = crearJwtService(fechaExpirado);

        assertFalse(validador.esRefreshTokenVerificado(refreshToken, usuario));
    }



    @Test
    void obtenerNombreUsuario_WhenTokenIsValid_ShouldReturnCorrectUsername() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        jwtService = crearJwtService(FIXES_INSTANT);

        String token = jwtService.generarToken(usuario);

        assertEquals(usuario.getNombre(), jwtService.obtenerNombreUsuario(token));
    }



    @Test
    void esTokenExpirado_WhenTokenIsExpired_ShouldThrowExpiredJwtException() {
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");
        JwtService creador = crearJwtService(FIXES_INSTANT);
        String token = creador.generarToken(usuario);

        Instant fechaExpirado =  Instant.parse("2026-01-01T12:15:01Z");
        JwtService validador = crearJwtService(fechaExpirado);

        assertThrows(ExpiredJwtException.class, () -> validador.obtenerNombreUsuario(token));
    }




    private JwtService crearJwtService(Instant instant) {
        Clock clock = Clock.fixed(instant, ZoneId.systemDefault());
        return new JwtService(props, clock);
    }

}