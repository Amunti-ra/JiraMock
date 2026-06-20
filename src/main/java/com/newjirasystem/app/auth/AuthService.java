package com.newjirasystem.app.auth;

import com.newjirasystem.app.exception.CredencialesInvalidasException;
import com.newjirasystem.app.exception.SesionExpiradaException;
import com.newjirasystem.app.exception.UsuarioNoEncontradoException;
import com.newjirasystem.app.usuarios.Rol;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password())
            );
        } catch (AuthenticationException e) {
            throw new CredencialesInvalidasException();
        }

        Usuario usuario = usuariosRepository.findByNombre(request.username())
                .orElseThrow(CredencialesInvalidasException::new);

        String jwtToken = jwtService.generarToken(usuario);
        String jwtRefreshToken = jwtService.generarRefreshToken(usuario);

        return new TokenResponseDTO(jwtToken, jwtRefreshToken);
    }

    @Transactional
    public TokenResponseDTO register(RegisterRequestDTO request) {
        Usuario usuario = new Usuario(request.username(), Rol.USER, passwordEncoder.encode(request.password()));

        usuariosRepository.save(usuario);

        String jwtToken = jwtService.generarToken(usuario);
        String jwtRefreshToken = jwtService.generarRefreshToken(usuario);

        return new TokenResponseDTO(jwtToken, jwtRefreshToken);
    }

    public TokenResponseDTO refreshToken(RefreshRequestDTO refreshToken) {
        String token = refreshToken.refreshToken();

        try {
            String nombreUsuario = jwtService.obtenerNombreUsuario(token);

            Usuario usuario = usuariosRepository.findByNombre(nombreUsuario)
                    .orElseThrow(() -> new UsuarioNoEncontradoException(nombreUsuario));

            if (!jwtService.esRefreshTokenValido(token, usuario)) {
                throw new RuntimeException("Refresh token invalido o expirado");
            }

            String nuevoJwtToken = jwtService.generarToken(usuario);
            String nuevoRefreshToken = jwtService.generarRefreshToken(usuario);

            return new TokenResponseDTO(nuevoJwtToken, nuevoRefreshToken);

        } catch (Exception e) {
            throw new SesionExpiradaException();
        }
    }

}
