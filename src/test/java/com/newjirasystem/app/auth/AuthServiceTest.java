package com.newjirasystem.app.auth;

import com.newjirasystem.app.dataFactory.TestDataFactory;
import com.newjirasystem.app.exception.SesionExpiradaException;
import com.newjirasystem.app.exception.TokenInvalidoException;
import com.newjirasystem.app.exception.UsuarioDuplicadoException;
import com.newjirasystem.app.exception.UsuarioNoEncontradoException;
import com.newjirasystem.app.usuarios.Rol;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuariosRepository usuariosRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    PasswordEncoder passwordEncoder;


    @Test
    void login_WhenCredentialsAreValid_ShouldReturnTokensAnd200OK() {
        // arrange
        AuthRequestDTO requestDTO = new AuthRequestDTO("username", "password");
        Usuario usuario = new Usuario("admin", Rol.ADMIN, "password");

        Authentication authenticationCorrecta = new UsernamePasswordAuthenticationToken(
                usuario, null, usuario.getAuthorities());


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationCorrecta);

        when(usuariosRepository.findByNombre(requestDTO.username())).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken(usuario)).thenReturn("token");
        when(jwtService.generarRefreshToken(usuario)).thenReturn("refreshToken");

        // act
        TokenResponseDTO response = authService.login(requestDTO);

        // assert
        assertNotNull(response);
        assertEquals("refreshToken", response.refreshToken());
        assertEquals("token", response.accessToken());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService).generarToken(usuario);
        verify(jwtService).generarRefreshToken(usuario);
    }

    @Test
    void login_WhenCredentialsAreInvalid_ShouldReturn401Unauthorized() {
        AuthRequestDTO requestDTO = new AuthRequestDTO("username", "password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciales invalidas"));

        assertThrows(BadCredentialsException.class, () -> authService.login(requestDTO));
    }



    @Test
    void register_WhenCredentialsAreValid_ShouldReturnTokensAnd200OK() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("usuario", "password");
        Usuario usuario = TestDataFactory.crearUsuario();

        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(usuariosRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("token");
        when(jwtService.generarRefreshToken(any(Usuario.class))).thenReturn("refreshToken");

        TokenResponseDTO response = authService.register(requestDTO);


        assertNotNull(response);
        assertEquals("token", response.accessToken());
        assertEquals("refreshToken", response.refreshToken());
        verify(usuariosRepository, times(1)).save(any(Usuario.class));
        verify(passwordEncoder).encode(requestDTO.password());
    }

    @Test
    void register_WhenUserAlreadyExists_ShouldThrowUsuarioDuplicadoException() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("usuario", "password");

        when(usuariosRepository.existsByNombre(requestDTO.username())).thenReturn(true);

        assertThrows(UsuarioDuplicadoException.class, () -> authService.register(requestDTO));
    }



    @Test
    void refreshToken_WhenTokenIsValid_ShouldReturnNewToken() {
        RefreshRequestDTO requestDTO = new RefreshRequestDTO("refreshToken");
        Usuario usuario = TestDataFactory.crearUsuario();

        when(jwtService.obtenerNombreUsuario(requestDTO.refreshToken())).thenReturn(usuario.getNombre());
        when(usuariosRepository.findByNombre(usuario.getNombre())).thenReturn(Optional.of(usuario));

        when(jwtService.esRefreshTokenVerificado(eq(requestDTO.refreshToken()), any(Usuario.class)))
                .thenReturn(true);

        when(jwtService.generarToken(any(Usuario.class))).thenReturn("newToken");
        when(jwtService.generarRefreshToken(any(Usuario.class))).thenReturn("newRefreshToken");


        TokenResponseDTO response = authService.refreshToken(requestDTO);


        assertNotNull(response);
        assertEquals("newToken", response.accessToken());
        assertEquals("newRefreshToken", response.refreshToken());
    }

    @Test
    void refreshToken_WhenTokenIsInvalid_ShouldThrowTokenInvalidoException() {
        RefreshRequestDTO requestDTO = new RefreshRequestDTO("refreshToken");

        when(jwtService.obtenerNombreUsuario(requestDTO.refreshToken())).thenThrow(MalformedJwtException.class);

        assertThrows(TokenInvalidoException.class, () -> authService.refreshToken(requestDTO));
    }

    @Test
    void refreshToken_WhenTokenHasExpired_ShouldThrowTokenSesionExpiradaException() {
        RefreshRequestDTO requestDTO = new RefreshRequestDTO("refreshToken");

        when(jwtService.obtenerNombreUsuario(requestDTO.refreshToken())).thenThrow(ExpiredJwtException.class);

        assertThrows(SesionExpiradaException.class, () -> authService.refreshToken(requestDTO));
    }

    @Test
    void refreshToken_WhenTokenIsValidButUsuarioDoesNotExist_ShouldThrowUsuarioNoEncontradoException() {
        RefreshRequestDTO requestDTO = new RefreshRequestDTO("refreshToken");
        Usuario usuario = TestDataFactory.crearUsuario();

        when(jwtService.obtenerNombreUsuario(requestDTO.refreshToken())).thenReturn(usuario.getNombre());
        when(usuariosRepository.findByNombre(usuario.getNombre())).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> authService.refreshToken(requestDTO));
    }

    @Test
    void refreshToken_WhenTokenIsNotVerificado_ShouldThrowTokenInvalidoException() {
        RefreshRequestDTO requestDTO = new RefreshRequestDTO("refreshToken");
        Usuario usuario = TestDataFactory.crearUsuario();

        when(jwtService.obtenerNombreUsuario(requestDTO.refreshToken())).thenReturn(usuario.getNombre());
        when(usuariosRepository.findByNombre(usuario.getNombre())).thenReturn(Optional.of(usuario));

        when(jwtService.esRefreshTokenVerificado(eq(requestDTO.refreshToken()), any(Usuario.class)))
                .thenReturn(false);

        assertThrows(TokenInvalidoException.class, () -> authService.refreshToken(requestDTO));
    }
}