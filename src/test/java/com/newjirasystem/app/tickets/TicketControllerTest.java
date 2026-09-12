package com.newjirasystem.app.tickets;

import com.newjirasystem.app.comentarios.ComentarioDTO;
import com.newjirasystem.app.comentarios.ComentarioService;
import com.newjirasystem.app.config.SecurityConfig;
import com.newjirasystem.app.dataFactory.TestDataFactory;
import com.newjirasystem.app.exception.ProyectoNoEncontradoException;
import com.newjirasystem.app.exception.TicketNoEncontradoException;
import com.newjirasystem.app.auth.CustomSecCheck;
import com.newjirasystem.app.exception.UsuarioNoEncontradoException;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = TicketController.class)
@Import(SecurityConfig.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketMapper ticketMapper;

    @MockitoBean
    TicketService ticketService;

    @MockitoBean
    UsuariosRepository usuariosRepository;

    @MockitoBean
    ComentarioService comentarioService;

    @MockitoBean(name = "customSecCheck")
    CustomSecCheck customSecCheck;


    @Test
    @WithMockUser
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
    @WithMockUser
    void getTickets_WhenUsuarioDoesNotExist_ShouldReturn404NotFound() throws Exception {
        when(ticketService.getFilteredTickets(eq(999L), any(), any(), any())).thenThrow(new UsuarioNoEncontradoException(999L));

        mockMvc.perform(get("/tickets/get")
                        .param("asignadoId", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario con ID 999 no encontrado"));
    }

    @Test
    @WithMockUser
    void getTickets_WhenProyectoDoesNotExist_ShouldReturn404NotFound() throws Exception {
        when(ticketService.getFilteredTickets(any(), eq(999L), any(), any())).thenThrow(new ProyectoNoEncontradoException(999L));

        mockMvc.perform(get("/tickets/get")
                        .param("proyectoId", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Proyecto con ID 999 no encontrado"));
    }

    @Test
    @WithMockUser
    void getTickets_WithOnlyEstadoFilter_ShouldReturnTicketDTOListAnd200OK() throws Exception {
        List<TicketDTO> lista = TestDataFactory.crearListaTicketDTO();
        when(ticketService.getFilteredTickets(isNull(), isNull(), isNull(), eq(EstadoTicket.POR_HACER))).thenReturn(lista);

        mockMvc.perform(get("/tickets/get")
                        .param("estado", "POR_HACER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].estado").value(EstadoTicket.POR_HACER.name()))
                .andExpect(jsonPath("$[0].clave").exists())
                .andExpect(jsonPath("$[0].creador").exists());
    }

    @Test
    @WithMockUser
    void getTickets_WithOnlyPrioridadFilter_ShouldReturnTicketDTOListAnd200OK() throws Exception {
        List<TicketDTO> lista = TestDataFactory.crearListaTicketDTO();
        when(ticketService.getFilteredTickets(isNull(), isNull(), eq(PrioridadTicket.LOW), isNull())).thenReturn(lista);

        mockMvc.perform(get("/tickets/get")
                        .param("prioridad", "LOW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].prioridad").value(PrioridadTicket.LOW.name()))
                .andExpect(jsonPath("$[0].clave").exists())
                .andExpect(jsonPath("$[0].creador").exists());
    }

    @Test
    @WithMockUser
    void getTickets_WithNoParameters_ShouldReturnAllTickets() throws Exception {
        List<TicketDTO> lista = TestDataFactory.crearListaTicketDTO();
        ArrayList<TicketDTO> listaConTicket = new ArrayList<>(lista);

        TicketDTO ticketDTO = TestDataFactory.crearTicketDto();
        listaConTicket.add(ticketDTO);

        when(ticketService.getFilteredTickets(isNull(), isNull(), isNull(), isNull())).thenReturn(listaConTicket);

        mockMvc.perform(get("/tickets/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].prioridad").exists())
                .andExpect(jsonPath("$[0].clave").exists())
                .andExpect(jsonPath("$[0].creador").exists());
    }

    @Test
    @WithMockUser
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
    @WithMockUser
    void getTicketById_WhenTicketDoesNotExist_ShouldReturn404TicketNotFound() throws Exception {
        when(ticketService.getTicketById(999L)).thenThrow(new TicketNoEncontradoException(999L));

        mockMvc.perform(get("/tickets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TICKET_NOT_FOUND"));
    }



    @Test
    @WithMockUser
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
                    .with(csrf())
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
    @WithMockUser
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
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void postTicket_WhenIdCreadorIsMissing_ShouldReturn400BadRequest() throws Exception {
        String entrada = """
                {
                    "titulo": "test titulo",
                    "descripcion": "test descripcion",
                    "prioridad": "LOW",
                    "idProyecto": "1",
                    "tipo": "BUG"
                }
                """;

        mockMvc.perform(post("/tickets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void postTicket_WhenIdProyectoIsMissing_ShouldReturn400BadRequest() throws Exception {
        String entrada = """
                {
                    "titulo": "test titulo",
                    "descripcion": "test descripcion",
                    "prioridad": "LOW",
                    "idCreador": "1",
                    "tipo": "BUG"
                }
                """;

        mockMvc.perform(post("/tickets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
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
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void postTicket_WhenUserIsAnonymous_ShouldReturn401Unauthorized() throws Exception {
                        mockMvc.perform(post("/tickets")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }



    @Test
    @WithMockUser(username = "usuario test")
    void patchTicketById_WhenUsuarioIsOwner_ShouldTicketDTOAndReturn200OK() throws Exception {

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
        when(customSecCheck.esCreadorTicket(eq(1L), eq("usuario test"))).thenReturn(true);


        mockMvc.perform(patch("/tickets/1")
                    .with(csrf())
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
    @WithMockUser(username = "creadorTest")
    void patchTicketById_TicketDoesNotExist_ShouldReturn404NotFound() throws Exception {
        when(customSecCheck.esCreadorTicket(anyLong(), anyString())).thenReturn(true);

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
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void patchTicketById_WhenTituloIsNotValid_ShouldReturn400BadRequest() throws Exception {
        when(customSecCheck.esCreadorTicket(anyLong(), anyString())).thenReturn(true);

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
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).patchTicketById(1L, null);
    }

    @Test
    @WithMockUser
    void patchTicketById_WhenUserIsNotCreator_ShouldReturn403Forbidden() throws Exception {
        when(customSecCheck.esCreadorTicket(anyLong(), anyString())).thenReturn(false);

        String entrada = """
                {
                    "titulo": "test titulo",
                    "descripcion": "test descripcion",
                    "idCreador": "1",
                    "idProyecto": "1",
                    "prioridad": "LOW",
                    "tipo": "BUG"
                }
                """;



        mockMvc.perform(patch("/tickets/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isForbidden());

        verify(ticketService, never()).patchTicketById(1L, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchTicketById_AsAdminButNotCreator_ShouldReturn403Forbidden() throws Exception {
        when(customSecCheck.esCreadorTicket(anyLong(), anyString())).thenReturn(false);

        String entrada = """
                {
                    "titulo": "test titulo",
                    "descripcion": "test descripcion",
                    "idCreador": "1",
                    "idProyecto": "1",
                    "prioridad": "LOW",
                    "tipo": "BUG"
                }
                """;

        mockMvc.perform(patch("/tickets/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isForbidden());

        verify(ticketService, never()).patchTicketById(1L, null);
    }

    @Test
    @WithMockUser
    void patchTicketById_WithValidPartialData_ShouldReturn200OK() throws Exception {
        when(customSecCheck.esCreadorTicket(anyLong(), anyString())).thenReturn(true);

        String entrada = """
                {
                    "titulo": "test titulo actualizado",
                    "estado": "EN_PROGRESO"
                }
                """;

        ActualizarTicketDTO actualizarTicketDTO = new ActualizarTicketDTO(
                "test titulo actualizado",
                null,
                null,
                EstadoTicket.EN_PROGRESO,
                null,
                null);

        mockMvc.perform(patch("/tickets/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isOk());

        verify(ticketService, times(1)).patchTicketById(1L, actualizarTicketDTO);
    }



    @Test
    @WithMockUser(username = "owner")
    void deleteTicketById_WhenUserIsOwner_ShouldReturn204NoContent() throws Exception{
        when(customSecCheck.esCreadorTicket(anyLong(), eq("owner"))).thenReturn(true);

        mockMvc.perform(delete("/tickets/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(ticketService, times(1)).deleteTicketById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTicketById_WhenTicketDoesNotExist_ShouldReturn404NotFound() throws Exception {
        doThrow(new TicketNoEncontradoException(1L)).when(ticketService).deleteTicketById(1L);

        mockMvc.perform(delete("/tickets/1")
                    .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TICKET_NOT_FOUND"));
    }

    @Test
    @WithMockUser(username = "stranger", roles = "USER")
    void deleteTicketById_WhenUserNotOwnerOrAdmin_ShouldReturn403Forbidden() throws Exception {
        when(customSecCheck.esCreadorTicket(anyLong(), anyString())).thenReturn(false);

        mockMvc.perform(delete("/tickets/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(ticketService, never()).deleteTicketById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTicketById_WhenUserIsAdmin_ShouldReturn204NoContent() throws Exception {
        mockMvc.perform(delete("/tickets/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(ticketService, times(1)).deleteTicketById(1L);
        verify(customSecCheck, never()).esCreadorTicket(anyLong(), anyString());
    }



    @Test
    @WithMockUser
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
                        .with(csrf())
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
    @WithMockUser
    void postComentario_WhenTextoIsTooLong_ShouldReturn400BadRequest() throws Exception {
        String texto = "A".repeat(501);

        String entrada = """
                {
                    "texto": "%s",
                    "idAutor": 1
                }
                """.formatted(texto);

        mockMvc.perform(post("/tickets/1/comentarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void postComentario_WhenTextoIsEmpty_ShouldReturn400BadRequest() throws Exception {
        String entrada = """
                {
                    "texto": "",
                    "idAutor": 1
                }
                """;

        mockMvc.perform(post("/tickets/1/comentarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void postComentario_WhenUsuarioDoesNotExist_ShouldReturn404NotFound() throws Exception{
        String entrada = """
                {
                    "texto": "texto test",
                    "idAutor": 1
                }
                """;

        when(comentarioService.postComentario(eq(1L), any())).thenThrow(new UsuarioNoEncontradoException(1L));

        mockMvc.perform(post("/tickets/1/comentarios")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(entrada))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario con ID 1 no encontrado"));
    }

    @Test
    @WithMockUser
    void postComentario_WhenTicketDoesNotExistExist_ShouldReturn404NotFound() throws Exception {
        String entrada = """
                {
                    "texto": "texto test",
                    "idAutor": 1
                }
                """;

        when(comentarioService.postComentario(eq(1L), any())).thenThrow(new TicketNoEncontradoException(1L));

        mockMvc.perform(post("/tickets/1/comentarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entrada))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Ticket con ID 1 no encontrado"));
    }



    @Test
    @WithMockUser
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
    @WithMockUser
    void getComentariosByTicketId_WhenTicketDoesNotExist_ShouldReturn404NotFound() throws Exception {
        when(comentarioService.getComentariosByTicketId(1L)).thenThrow(new TicketNoEncontradoException(1L));

        mockMvc.perform(get("/tickets/1/comentarios"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Ticket con ID 1 no encontrado"));
    }
}
