package com.newjirasystem.app.tickets;

import com.newjirasystem.app.comentarios.ComentarioDTO;
import com.newjirasystem.app.comentarios.ComentarioService;
import com.newjirasystem.app.dataFactory.TestDataFactory;
import com.newjirasystem.app.exception.ProyectoNoEncontradoException;
import com.newjirasystem.app.exception.TicketNoEncontradoException;
import com.newjirasystem.app.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketMapper ticketMapper;

    @MockitoBean
    TicketService ticketService;

    @MockitoBean
    ComentarioService comentarioService;



    @Test
    void getTickets_WhenRequestIsValid_ShouldReturnListOfTicketsAnd200OK() throws Exception {
        List<TicketDTO> lista = TestDataFactory.crearListaTicketDTO();
        when(ticketService.getFilteredTickets(any(), any(), any(), any())).thenReturn(lista);

        mockMvc.perform(get("/tickets/get")
                        .param("asignadoId", "1")
                        .param("prioridad", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(lista.getFirst().id()))
                .andExpect(jsonPath("$[0].prioridad").value("LOW"))
                .andExpect(jsonPath("$[0].tipo").value("BUG"))
                .andExpect(jsonPath("$[0].titulo").value("test titulo"))
                .andExpect(jsonPath("$[1].id").value(lista.get(1).id()));
    }

    @Test
    void getTickets_WhenUsuarioDoesNotExist_ShouldReturn404NotFound() throws Exception {
        when(ticketService.getFilteredTickets(eq(999L), any(), any(), any())).thenThrow(new UsuarioNoEncontradoException(999L));

        mockMvc.perform(get("/tickets/get")
                        .param("asignadoId", "999")) // Forzamos el ID inexistente
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario con ID 999 no encontrado"));
    }

    @Test
    void getTickets_WhenProyectoDoesNotExist_ShouldReturn404NotFound() throws Exception {
        when(ticketService.getFilteredTickets(any(), eq(999L), any(), any())).thenThrow(new ProyectoNoEncontradoException(999L));

        mockMvc.perform(get("/tickets/get")
                        .param("proyectoId", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Proyecto con ID 999 no encontrado"));
    }


    @Test
    void getTicketByIdWhenTicketExists_ShouldReturnTicketDTOAnd200OK() throws Exception {
        TicketDTO dto = TestDataFactory.crearTicketDto();

        when(ticketService.getTicketById(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dto.id()))
                .andExpect(jsonPath("$.clave").value(dto.clave()))
                .andExpect(jsonPath("$.creador").value(dto.creador()));
    }

    @Test
    void getTicketById_WhenTicketDoesNotExist_ShouldReturn404TicketNotFound() throws Exception {
        when(ticketService.getTicketById(999L)).thenThrow(new TicketNoEncontradoException(999L));

        mockMvc.perform(get("/tickets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TICKET_NOT_FOUND"));
    }



    @Test
    void postTicket_WhenDataIsValid_ShouldReturnTicketDTOAnd201Created() throws Exception{
        TicketDTO ticketDTO = TestDataFactory.crearTicketDto();
        String entrada =  """
                {
                    "titulo": "test titulo",
                    "descripcion": "test descripcion",
                    "idCreador": "1",
                    "idProyecto": "1",
                    "prioridad": "LOW",
                    "tipo": "BUG"
                }
                """;


        when(ticketService.postTicket(any())).thenReturn(ticketDTO);

        mockMvc.perform(post("/tickets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(entrada))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("test titulo"))
                .andExpect(jsonPath("$.prioridad").value("LOW"))
                .andExpect(jsonPath("$.creador").exists())
                .andExpect(jsonPath("$.proyecto").exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "   ",
            "",
            "A"
    })
    void postTicket_WhenTituloIsInvalid_ShouldReturn400BadRequest(String tituloNoValido) throws Exception{
        if (tituloNoValido.equals("A")) {
            tituloNoValido = "A".repeat(151);
        }

        String entrada = """
                {
                    "titulo": "%s",
                    "descripcion": "test descripcion",
                    "idCreador": "1",
                    "idProyecto": "1",
                    "prioridad": "LOW",
                    "tipo": "BUG"
                }
                """.formatted(tituloNoValido);

        mockMvc.perform(post("/tickets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postTicket_WhenRequiredFieldsAreMissing_ShouldReturn400BadRequest() throws Exception {
        String entrada = """
                {
                    "titulo": "test titulo",
                    "descripcion": "test descripcion",
                    "prioridad": "LOW",
                    "tipo": "BUG"
                }
                """;

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postTicket_WhenEnumValuesAreInvalid_ShouldReturn400BadRequest() throws Exception {
        String entrada = """
                {
                    "titulo": "test titulo",
                    "descripcion": "test descripcion",
                    "idCreador": "1",
                    "idProyecto": "1",
                    "prioridad": "SI",
                    "tipo": "DRAGON"
                }
                """;


        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }



    @Test
    void patchTicketById_WhenTituloIsValid_ShouldTicketDTOAndReturn200OK() throws Exception {
        String entrada = """
                {
                    "titulo": "test titulo actualizado",
                    "descripcion": "test descripcion actualizada",
                    "idAsignado": 1,
                    "estado": "EN_PROGRESO",
                    "prioridad": "HIGH",
                    "tipo": "EPICO"
                }
                """;

        TicketDTO ticketDTO = TestDataFactory.crearTicketDto();

        when(ticketService.patchTicketById(eq(1L), any())).thenReturn(ticketDTO);

        mockMvc.perform(patch("/tickets/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(entrada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value(ticketDTO.titulo()))
                .andExpect(jsonPath("$.descripcion").value(ticketDTO.descripcion()))
                .andExpect(jsonPath("$.prioridad").value(ticketDTO.prioridad().toString()))
                .andExpect(jsonPath("$.estado").value(ticketDTO.estado().toString()));
    }

    @Test
    void patchTicketById_TicketDoesNotExist_ShouldReturn404NotFound() throws Exception {
        String entrada = """
                {
                    "titulo": "test titulo actualizado",
                    "descripcion": "test descripcion actualizada",
                    "idAsignado": 1,
                    "estado": "EN_PROGRESO",
                    "prioridad": "HIGH",
                    "tipo": "EPICO"
                }
                """;

        when(ticketService.patchTicketById(eq(1L), any())).thenThrow(new TicketNoEncontradoException(1L));

        mockMvc.perform(patch("/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchTicketById_WhenTituloIsNotValid_ShouldReturn400BadRequest() throws Exception {
        String tituloLargo = "A".repeat(151);

        String entrada = """
                {
                    "titulo": "%s",
                    "descripcion": "test descripcion actualizada",
                    "idAsignado": 1,
                    "estado": "EN_PROGRESO",
                    "prioridad": "HIGH",
                    "tipo": "EPICO"
                }
                """.formatted(tituloLargo);

        mockMvc.perform(patch("/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }



    @Test
    void deleteTicketById_WhenTicketExists_ShouldReturn204NoContent() throws Exception{
        mockMvc.perform(delete("/tickets/1")).andExpect(status().isNoContent());

        verify(ticketService, times(1)).deleteTicketById(1L);
    }

    @Test
    void deleteTicketById_WhenTicketDoesNotExist_ShouldReturn404NotFound() throws Exception {
        doThrow(new TicketNoEncontradoException(1L)).when(ticketService).deleteTicketById(1L);

        mockMvc.perform(delete("/tickets/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TICKET_NOT_FOUND"));
    }



    @Test
    void postComentario_WhenTicketExistsAndDataIsValid_ShouldComentarioDTOReturn201Created() throws Exception {
        String entrada = """
                {
                    "texto": "texto test",
                    "idAutor": 1
                }
                """;
        ComentarioDTO comentarioDTO = TestDataFactory.crearComentarioDTO();

        when(comentarioService.postComentario(eq(1L), any())).thenReturn(comentarioDTO);

        mockMvc.perform(post("/tickets/1/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.texto").value(comentarioDTO.texto()))
                .andExpect(jsonPath("$.nombreAutor").value(comentarioDTO.nombreAutor()))
                .andExpect(jsonPath("$.fechaCreacion").exists());

        verify(comentarioService, times(1)).postComentario(eq(1L), any());
    }

    @Test
    void postComentario_WhenUsuarioDoesNotExist_ShouldReturn404NotFound() throws Exception{
        String entrada = """
                {
                    "texto": "texto test",
                    "idAutor": 1
                }
                """;

        when(comentarioService.postComentario(eq(1L), any())).thenThrow(new UsuarioNoEncontradoException(1L));

        mockMvc.perform(post("/tickets/1/comentarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(entrada))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario con ID 1 no encontrado"));
    }

    @Test
    void postComentario_WhenTicketDoesNotExistExist_ShouldReturn404NotFound() throws Exception {
        String entrada = """
                {
                    "texto": "texto test",
                    "idAutor": 1
                }
                """;

        when(comentarioService.postComentario(eq(1L), any())).thenThrow(new TicketNoEncontradoException(1L));

        mockMvc.perform(post("/tickets/1/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Ticket con ID 1 no encontrado"));
    }



    @Test
    void getComentariosByTicketId_WhenTicketExists_ShouldReturnComentarioDTOListAnd200OK() throws Exception {
        List<ComentarioDTO> lista = TestDataFactory.crearListaComentarioDTO();

        when(comentarioService.getComentariosByTicketId(1L)).thenReturn(lista);

        mockMvc.perform(get("/tickets/1/comentarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(lista.getFirst().id()))
                .andExpect(jsonPath("$[0].texto").value(lista.getFirst().texto()))
                .andExpect(jsonPath("$[1].id").value(lista.get(1).id()));
    }

    @Test
    void getComentariosByTicketId_WhenTicketDoesNotExist_ShouldReturn404NotFound() throws Exception {
        when(comentarioService.getComentariosByTicketId(1L)).thenThrow(new TicketNoEncontradoException(1L));

        mockMvc.perform(get("/tickets/1/comentarios"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Ticket con ID 1 no encontrado"));
    }
}