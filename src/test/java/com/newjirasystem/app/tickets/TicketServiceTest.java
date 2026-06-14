package com.newjirasystem.app.tickets;

import com.newjirasystem.app.dataFactory.TestDataFactory;
import com.newjirasystem.app.exception.ProyectoNoEncontradoException;
import com.newjirasystem.app.exception.TicketNoEncontradoException;
import com.newjirasystem.app.exception.UsuarioNoEncontradoException;
import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.proyectos.ProyectosRepository;
import com.newjirasystem.app.usuarios.Rol;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @InjectMocks
    private TicketService ticketService;

    @Spy
    private TicketMapper ticketMapper;

    @Mock
    private TicketsRepository ticketsRepository;

    @Mock
    private ProyectosRepository proyectosRepository;

    @Mock
    private UsuariosRepository usuariosRepository;


    @Test
    void postTicket_WhenProyectoAndUserExist_ShouldCreateTicketSuccessfully() {
        // arrange
        CrearTicketDTO testCrearTicketDTO = new CrearTicketDTO(
                "titulo",
                "descripcion",
                0L,
                0L,
                PrioridadTicket.LOW,
                TipoTicket.TAREA);


        Usuario usuarioMock = new Usuario(
                "mati",
                Rol.USER,
                "7575757"
        );

        Proyecto proyectoMock = new Proyecto(
                "Desarrollo Multiplataforma",
                "DAM"
        );

        when(usuariosRepository.findById((testCrearTicketDTO.idCreador()))).thenReturn(Optional.of(usuarioMock));
        when(proyectosRepository.findById(testCrearTicketDTO.idProyecto())).thenReturn(Optional.of(proyectoMock));

        // act

        TicketDTO resultado = ticketService.postTicket(testCrearTicketDTO);

        // assert

        assertNotNull(resultado);
        assertEquals("DAM-1", resultado.clave());
        assertEquals(1, proyectoMock.getContadorTickets());
        assertEquals(EstadoTicket.POR_HACER, resultado.estado());
        assertEquals(proyectoMock.getNombre(), resultado.proyecto());
        assertEquals(usuarioMock.getNombre(), resultado.creador());

        assertEquals(testCrearTicketDTO.prioridad(), resultado.prioridad());
        assertEquals(testCrearTicketDTO.titulo(), resultado.titulo());
    }

    @Test
    void postTicket_WhenProyectoDoesNotExist_ShouldThrowProyectoNoEncontradoException() {
        // arrange
        CrearTicketDTO testCrearTicketDTO = new CrearTicketDTO(
                "titulo",
                "descripcion",
                1L,
                2L,
                PrioridadTicket.LOW,
                TipoTicket.TAREA);


        Usuario usuarioMock = new Usuario(
                "mati",
                Rol.USER,
                "7575757"
        );

        when(usuariosRepository.findById((testCrearTicketDTO.idCreador()))).thenReturn(Optional.of(usuarioMock));
        when(proyectosRepository.findById(testCrearTicketDTO.idProyecto())).thenReturn(Optional.empty());

        // act / assert

        assertThrows(ProyectoNoEncontradoException.class, () -> ticketService.postTicket(testCrearTicketDTO));

    }

    @Test
    void postTicket_WhenUsuarioDoesNotExist_ShouldThrowUsuarioNoEncontradoException() {
        // arrange
        CrearTicketDTO testCrearTicketDTO = new CrearTicketDTO(
                "titulo",
                "descripcion",
                1L,
                2L,
                PrioridadTicket.LOW,
                TipoTicket.TAREA);

        // act / assert
        assertThrows(UsuarioNoEncontradoException.class, () -> ticketService.postTicket(testCrearTicketDTO));
    }



    @Test
    void getTicketByIdWhenTicketExists_ShouldReturnTicketDTO() {
        Ticket ticket = TestDataFactory.crearTicket();

        ReflectionTestUtils.setField(ticket, "id", 1L);

        when(ticketsRepository.findByIdAndActivoTrue(ticket.getId())).thenReturn(Optional.of(ticket));

        TicketDTO resultado = ticketService.getTicketById(ticket.getId());

        assertNotNull(resultado);
        assertEquals(ticket.getId(), resultado.id());
        assertEquals(ticket.getClave(), resultado.clave());
    }

    @Test
    void getTicketById_WhenTicketIdDoesNotExists_ShouldThrowTicketNoEncontradoException() {

        when(ticketsRepository.findByIdAndActivoTrue(999L)).thenReturn(Optional.empty());

        assertThrows(TicketNoEncontradoException.class, () -> ticketService.getTicketById(999L));
    }



    @Test
    void getTickets_ShouldReturnMappedTickets() {
        Ticket ticket1 = TestDataFactory.crearTicket();
        Ticket ticket2 = TestDataFactory.crearTicket();

        when(ticketsRepository.findAllByActivoTrue()).thenReturn(List.of(ticket1, ticket2));

        List<TicketDTO> resultado = ticketService.getTickets();

        assertEquals(2, resultado.size());
        assertEquals("TEST-1", resultado.getFirst().clave());
        assertEquals(PrioridadTicket.LOW, resultado.getFirst().prioridad());
    }

    @Test
    void getFilteredTicketsWhenAllParametersAreNull_ShouldReturnAllTickets() {
        Ticket ticket1 = TestDataFactory.crearTicket();
        Ticket ticket2 = TestDataFactory.crearTicket();

        when(ticketsRepository.findAll(any(Specification.class))).thenReturn(List.of(ticket1, ticket2));

        List<TicketDTO> resultado = ticketService.getFilteredTickets(null, null, null, null);

        assertEquals(2, resultado.size());
    }

    @Test
    void getFilteredTickets_ShouldReturnMappedTicketsMatchingFilters() {
        Ticket ticket1 = TestDataFactory.crearTicket();

        when(usuariosRepository.existsById(1L)).thenReturn(true);
        when(proyectosRepository.existsById(1L)).thenReturn(true);

        when(ticketsRepository.findAll(any(Specification.class))).thenReturn(List.of(ticket1));

        List<TicketDTO> resultado = ticketService.getFilteredTickets(1L, 1L, PrioridadTicket.LOW, EstadoTicket.POR_HACER);

        assertEquals(1, resultado.size());
        assertEquals("TEST-1", resultado.getFirst().clave());
        assertEquals(PrioridadTicket.LOW, resultado.getFirst().prioridad());
    }

    @Test
    void getFilteredTicketsWhenUsuarioDoesNotExist_ShouldThrowUsuarioNoEncontradoException() {
        when(usuariosRepository.existsById(1L)).thenReturn(false);

        assertThrows(UsuarioNoEncontradoException.class, () -> ticketService.getFilteredTickets(1L, null, null, null));
    }

    @Test
    void getFilteredTicketsWhenProyectoDoesNotExist_ShouldThrowProyectoNoEncontradoException() {
        when(proyectosRepository.existsById(1L)).thenReturn(false);

        assertThrows(ProyectoNoEncontradoException.class, () -> ticketService.getFilteredTickets(null, 1L, null, null));
    }


    @Test
    void patchTicketWhenTicketIdExists_ShouldReturnUpdatedTicketDTO() {
        Ticket ticket = TestDataFactory.crearTicket();
        ReflectionTestUtils.setField(ticket, "id", 1L);

        Usuario usuario = TestDataFactory.crearUsuario();
        ReflectionTestUtils.setField(usuario, "id", 1L);
        ReflectionTestUtils.setField(usuario, "nombre", "nombreTest");

        ActualizarTicketDTO dto = new ActualizarTicketDTO("test",
                "descripcion test",
                usuario.getId(),
                EstadoTicket.EN_PROGRESO,
                PrioridadTicket.MEDIUM,
                TipoTicket.EPICO);

        when(ticketsRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(ticket));
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuario));

        TicketDTO resultado = ticketService.patchTicketById(ticket.getId(), dto);

        assertEquals(dto.titulo(), resultado.titulo());
        assertEquals(dto.descripcion(), resultado.descripcion());
        assertEquals(usuario.getNombre(), resultado.asignadoA());
        assertEquals(dto.estado(), resultado.estado());
        assertEquals(dto.prioridad(), resultado.prioridad());
        assertEquals(dto.tipo(), resultado.tipo());

    }

    @Test
    void patchTicketWhenTickedIdDoesNotExist_ShouldThrowTicketNotFoundException() {
        ActualizarTicketDTO dto = new ActualizarTicketDTO("test",
                "descripcion test",
                1L,
                EstadoTicket.EN_PROGRESO,
                PrioridadTicket.MEDIUM,
                TipoTicket.EPICO);

        when(ticketsRepository.findByIdAndActivoTrue(999L)).thenReturn(Optional.empty());

        assertThrows(TicketNoEncontradoException.class, () -> ticketService.patchTicketById(999L, dto));
    }

    @Test
    void patchTicketWhenTicketIdAsignadoDoesNotExist_ShouldThrowUsuarioNoEncontradoException() {
        Ticket ticket = TestDataFactory.crearTicket();
        ReflectionTestUtils.setField(ticket, "id", 1L);

        ActualizarTicketDTO dto = new ActualizarTicketDTO("test",
                "descripcion test",
                1L,
                EstadoTicket.EN_PROGRESO,
                PrioridadTicket.MEDIUM,
                TipoTicket.EPICO);

        when(usuariosRepository.findById(1L)).thenReturn(Optional.empty());
        when(ticketsRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(ticket));

        assertThrows(UsuarioNoEncontradoException.class, () -> ticketService.patchTicketById(1L, dto));
    }



    @Test
    void deleteTicketWhenTicketExists() {
        Ticket ticket = TestDataFactory.crearTicket();

        ReflectionTestUtils.setField(ticket, "id", 1L);

        when(ticketsRepository.findByIdAndActivoTrue(ticket.getId())).thenReturn(Optional.of(ticket));

        ticketService.deleteTicketById(ticket.getId());

        assertFalse(ticket.isActivo());
    }

    @Test
    void deleteTicketWhenTicketDoesNotExists_ShouldThrowTicketNoEncontradoException() {
        when(ticketsRepository.findByIdAndActivoTrue(999L)).thenReturn(Optional.empty());

        assertThrows(TicketNoEncontradoException.class, () -> ticketService.deleteTicketById(999L));
    }
}
