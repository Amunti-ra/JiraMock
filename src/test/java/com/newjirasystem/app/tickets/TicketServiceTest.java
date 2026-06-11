package com.newjirasystem.app.tickets;

import com.newjirasystem.app.exception.ProyectoNoEncontradoException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
                1L,
                2L,
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
        when(proyectosRepository.findById(testCrearTicketDTO.idPoryecto())).thenReturn(Optional.of(proyectoMock));

        // act

        TicketDTO resultado = ticketService.postTicket(testCrearTicketDTO);

        // assert

        assertNotNull(resultado);
        assertEquals("DAM-1", resultado.clave());
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
        when(proyectosRepository.findById(testCrearTicketDTO.idPoryecto())).thenReturn(Optional.empty());

        // act / assert

        assertThrows(ProyectoNoEncontradoException.class, () -> {
            ticketService.postTicket(testCrearTicketDTO);
        });

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


        Proyecto proyectoMock = new Proyecto(
                "Desarrollo Multiplataforma",
                "DAM"
        );

        // act / assert

        assertThrows(UsuarioNoEncontradoException.class, () -> {
            ticketService.postTicket(testCrearTicketDTO);
        });
    }
}
