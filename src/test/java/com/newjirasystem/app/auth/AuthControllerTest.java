package com.newjirasystem.app.auth;

import com.newjirasystem.app.exception.SesionExpiradaException;
import com.newjirasystem.app.exception.TokenInvalidoException;
import com.newjirasystem.app.exception.UsuarioDuplicadoException;
import com.newjirasystem.app.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;


    @Test
    void postLogin_WhenDataIsValid_ShouldReturnTokensAnd200OK() throws Exception {
        String entrada = """
                {
                    "username": "usuario",
                    "password": "12345678"
                }
                """;

        TokenResponseDTO response = new TokenResponseDTO("token", "refreshToken");

        when(authService.login(any(AuthRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("token"))
                .andExpect(jsonPath("$.refresh_token").value("refreshToken"));
    }

    @Test
    void postLogin_WhenDataIsInvalid_ShouldReturn400BadRequest() throws Exception {
        String entrada = """
                {
                    "username": "tes",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postLogin_WhenCredentialsAreWrong_ShouldReturn401Unauthorized() throws Exception {
        String entrada = """
                {
                    "username": "test",
                    "password": "12345678"
                }
                """;

        when(authService.login(any(AuthRequestDTO.class))).thenThrow(BadCredentialsException.class);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isUnauthorized());
    }



    @Test
    void postRegister_WhenDataIsValid_ShouldReturnTokensAnd201Created() throws Exception {
        String entrada = """
                {
                    "username": "usuario",
                    "password": "123456789"
                }
                """;

        TokenResponseDTO response = new TokenResponseDTO("token", "refreshToken");

        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(entrada))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token").value("token"))
                .andExpect(jsonPath("$.refresh_token").value("refreshToken"));
    }

    @Test
    void postRegister_WhenDataIsInvalid_ShouldReturn400BadRequest() throws Exception {
        String entrada = """
                {
                    "username": "usu",
                    "password": "12345"
                }
                """;

        mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postRegister_WhenUsernameAlreadyExists_ShouldReturn409Conflict() throws Exception {
        String entrada = """
                {
                    "username": "usuario",
                    "password": "12345678"
                }
                """;

        when(authService.register(any(RegisterRequestDTO.class))).thenThrow(UsuarioDuplicadoException.class);

        mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(entrada))
                .andExpect(status().isConflict());
    }



    @Test
    void postRefreshToken_WhenRefreshTokenIsValid_ShouldReturnTokensAnd200OK() throws Exception {
        String tokenEntrada = """
                    {
                        "refreshToken": "refreshToken"
                    }
                    """;
        TokenResponseDTO response = new TokenResponseDTO("token", "refreshToken");

        when(authService.refreshToken(any(RefreshRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tokenEntrada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("token"))
                .andExpect(jsonPath("$.refresh_token").value("refreshToken"));
    }

    @Test
    void postRefreshToken_WhenRefreshTokenIsExpired_ShouldReturn401UNAUTHORIZED() throws Exception {
        String entrada = """
                {
                    "refreshToken": "refreshToken"
                }
                """;

        when(authService.refreshToken(any(RefreshRequestDTO.class))).thenThrow(SesionExpiradaException.class);

        mockMvc.perform(post("/auth/refresh")
                        .content(entrada)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postRefreshToken_WhenRefreshTokenIsNotValid_ShouldReturn401UNAUTHORIZED() throws Exception{
        String entrada = """
                {
                    "refreshToken": "refreshToken"
                }
                """;

        when(authService.refreshToken(any(RefreshRequestDTO.class))).thenThrow(TokenInvalidoException.class);

        mockMvc.perform(post("/auth/refresh")
                        .content(entrada)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postRefreshToken_WhenUsuarioIsNotInSystem_ShouldReturn404NOTFOUND() throws Exception {
        String entrada = """
                {
                    "refreshToken": "refreshToken"
                }
                """;

        when(authService.refreshToken(any(RefreshRequestDTO.class))).thenThrow(UsuarioNoEncontradoException.class);

        mockMvc.perform(post("/auth/refresh")
                        .content(entrada)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}