package com.newjirasystem.app.auth;

import com.newjirasystem.app.exception.SesionExpiradaException;
import com.newjirasystem.app.exception.TokenInvalidoException;
import com.newjirasystem.app.exception.UsuarioDuplicadoException;
import com.newjirasystem.app.exception.UsuarioNoEncontradoException;
import com.newjirasystem.app.usuarios.Rol;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuariosRepository usuariosRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public TokenResponseDTO login(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password())
        );

        Usuario usuario = usuariosRepository.findByNombre(request.username())
                .orElseThrow(() -> new UsuarioNoEncontradoException(request.username()));

        String jwtToken = jwtService.generarToken(usuario);
        String jwtRefreshToken = jwtService.generarRefreshToken(usuario);

        return new TokenResponseDTO(jwtToken, jwtRefreshToken);
    }

    @Transactional
    public TokenResponseDTO register(RegisterRequestDTO request) {
        if (usuariosRepository.existsByNombre(request.username())) {
            throw new UsuarioDuplicadoException();
        }

        Usuario usuario = new Usuario(request.username(), Rol.USER, passwordEncoder.encode(request.password()));

        usuariosRepository.save(usuario);

        String jwtToken = jwtService.generarToken(usuario);
        String jwtRefreshToken = jwtService.generarRefreshToken(usuario);

        return new TokenResponseDTO(jwtToken, jwtRefreshToken);
    }

    public TokenResponseDTO refreshToken(RefreshRequestDTO refreshToken) {
        String token = refreshToken.refreshToken();
        String nombreUsuario;

        try {
            nombreUsuario = jwtService.obtenerNombreUsuario(token);
        } catch (ExpiredJwtException e) {
            throw new SesionExpiradaException("Token de refresco expirado, vuelve a iniciar sesión");
        } catch (SignatureException | MalformedJwtException e) {
            throw new TokenInvalidoException("El token de refresco ha sido manipulado o es inválido");
        } catch (Exception e) {
            throw new TokenInvalidoException("Error al procesar el token");
        }

        Usuario usuario = usuariosRepository.findByNombre(nombreUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException(nombreUsuario));

        if (!jwtService.esRefreshTokenVerificado(token, usuario)) {
            throw new TokenInvalidoException("Refresh token invalido o expirado");
        }

        String nuevoJwtToken = jwtService.generarToken(usuario);
        String nuevoRefreshToken = jwtService.generarRefreshToken(usuario);

        return new TokenResponseDTO(nuevoJwtToken, nuevoRefreshToken);
    }

}
