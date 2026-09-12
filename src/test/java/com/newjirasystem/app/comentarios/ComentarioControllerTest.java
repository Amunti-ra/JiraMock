package com.newjirasystem.app.comentarios;

import com.newjirasystem.app.auth.JwtService;
import com.newjirasystem.app.config.SecurityConfig;
import com.newjirasystem.app.dataFactory.TestDataFactory;
import com.newjirasystem.app.exception.ComentarioNoEncontradoException;
import com.newjirasystem.app.exception.TicketNoEncontradoException;
import com.newjirasystem.app.auth.CustomSecCheck;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComentarioController.class)
@Import(SecurityConfig.class)
class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComentarioService comentarioService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean(name = "customSecCheck")
    private CustomSecCheck customSecCheck;

    @MockitoBean
    private UsuariosRepository usuariosRepository;



    @Test
    void patchComentario_WhenAnonymous_ShouldReturn401Unauthorized() throws Exception {
        String entrada = """
                {
                    "texto": "texto"
                }
                """;

        mockMvc.perform(patch("/comentarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "userTest")
    void patchComentario_WhenUserIsNotCreator_ShouldReturn403Forbidden() throws Exception {
        String entrada = """
                {
                    "texto": "texto"
                }
                """;

        when(customSecCheck.esCreadorComentario(eq(1L), eq("userTest"))).thenReturn(false);

        mockMvc.perform(patch("/comentarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "userTest")
    void patchComentario_WhenDataIsValid_ShouldReturnComentarioDTOAnd200OK() throws Exception {
        String entrada = """
                {
                    "texto": "texto"
                }
                """;

        ComentarioDTO comentarioDTO = TestDataFactory.crearComentarioDTO();

        when(customSecCheck.esCreadorComentario(eq(1L), eq("userTest"))).thenReturn(true);
        when(comentarioService.patchComentario(eq(1L), any())).thenReturn(comentarioDTO);

        mockMvc.perform(patch("/comentarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.texto").value(comentarioDTO.texto()))
                .andExpect(jsonPath("$.ticket").value(comentarioDTO.ticket()))
                .andExpect(jsonPath("$.nombreAutor").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchComentario_AsAdmin_ShouldReturn403Forbidden() throws Exception {
        String entrada = """
                {
                    "texto": "texto"
                }
                """;

        when(customSecCheck.esCreadorComentario(eq(1L), eq("userTest"))).thenReturn(false);

        mockMvc.perform(patch("/comentarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "userTest")
    void patchComentario_WhenDataIsInvalid_ShouldReturn400BadRequest() throws Exception {
        String texto = "A".repeat(501);

        String entrada = """
                {
                    "texto": "%s"
                }
                """.formatted(texto);

        when(customSecCheck.esCreadorComentario(eq(1L), eq("userTest"))).thenReturn(true);

        mockMvc.perform(patch("/comentarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "userTest")
    void patchComentario_WhenTicketDoesNotExist_ShouldReturn404NotFound() throws Exception {
        String entrada = """
                {
                    "texto": "texto"
                }
                """;

        when(customSecCheck.esCreadorComentario(eq(1L), eq("userTest"))).thenReturn(true);
        when(comentarioService.patchComentario(eq(1L), any())).thenThrow(new TicketNoEncontradoException(1L));

        mockMvc.perform(patch("/comentarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isNotFound());
    }



    @Test
    void deleteComentario_WhenAnonymous_ShouldReturn401Unauthorized() throws Exception {
        mockMvc.perform(delete("/comentarios/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "userTest", roles = "USER")
    void deleteComentario_WhenUserIsNotCreatorAndNotAdmin_ShouldReturn403Forbidden() throws Exception {
        when(customSecCheck.esCreadorComentario(eq(1L), eq("userTest"))).thenReturn(false);

        mockMvc.perform(delete("/comentarios/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "adminTest", roles = "ADMIN")
    void deleteComentario_WhenUserIsAdmin_ShouldReturn204NoContent() throws Exception {
        mockMvc.perform(delete("/comentarios/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(comentarioService, times(1)).deleteComentario(1L);
    }

    @Test
    @WithMockUser(username = "userTest")
    void deleteComentarioById_WhenComentarioExists_ShouldReturn204NoContent() throws Exception {
        when(customSecCheck.esCreadorComentario(eq(1L), eq("userTest"))).thenReturn(true);

        mockMvc.perform(delete("/comentarios/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(comentarioService, times(1)).deleteComentario(1L);
    }

    @Test
    @WithMockUser(username = "userTest")
    void deleteComentarioById_WhenComentarioDoesNotExist_ShouldReturn404NotFound() throws Exception {
        when(customSecCheck.esCreadorComentario(eq(1L), eq("userTest"))).thenReturn(true);
        doThrow(new ComentarioNoEncontradoException(1L)).when(comentarioService).deleteComentario(1L);

        mockMvc.perform(delete("/comentarios/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Comentario con ID 1 no encontrado"));
    }
}
