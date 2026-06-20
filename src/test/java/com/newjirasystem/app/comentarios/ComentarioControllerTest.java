package com.newjirasystem.app.comentarios;

import com.newjirasystem.app.auth.JwtService;
import com.newjirasystem.app.dataFactory.TestDataFactory;
import com.newjirasystem.app.exception.TicketNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ComentarioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class})
class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComentarioService comentarioService;

    @MockitoBean
    JwtService jwtService;



    @Test
    void patchComentario_WhenDataIsValid_ShouldReturnComentarioDTOAnd200OK() throws Exception {
        String entrada = """
                {
                    "texto": "texto"
                }
                """;

        ComentarioDTO comentarioDTO = TestDataFactory.crearComentarioDTO();

        when(comentarioService.patchComentario(eq(1L), any())).thenReturn(comentarioDTO);

        mockMvc.perform(patch("/comentarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.texto").value(comentarioDTO.texto()))
                .andExpect(jsonPath("$.ticket").value(comentarioDTO.ticket()))
                .andExpect(jsonPath("$.nombreAutor").exists());
    }

    @Test
    void patchComentario_WhenDataIsInvalid_ShouldReturn400BadRequest() throws Exception {
        String texto = "A".repeat(501);

        String entrada = """
                {
                    "texto": "%s"
                }
                """.formatted(texto);

        mockMvc.perform(patch("/comentarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchComentario_WhenTicketDoesNotExist_ShouldReturn404NotFound() throws Exception {
        String entrada = """
                {
                    "texto": "texto"
                }
                """;

        when(comentarioService.patchComentario(eq(1L), any())).thenThrow(new TicketNoEncontradoException(1L));

        mockMvc.perform(patch("/comentarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isNotFound());
    }



    @Test
    void deleteComentarioById_WhenComentarioExists_ShouldReturn204NoContent() throws Exception {
        mockMvc.perform(delete("/comentarios/1")).andExpect(status().isNoContent());

        verify(comentarioService, times(1)).deleteComentario(1L);
    }

    @Test
    void deleteComentarioById_WhenTicketDoesNotExist_ShouldReturn404NotFound() throws Exception {
        doThrow(new TicketNoEncontradoException(1L)).when(comentarioService).deleteComentario(1L);

        mockMvc.perform(delete("/comentarios/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Ticket con ID 1 no encontrado"));
    }
}