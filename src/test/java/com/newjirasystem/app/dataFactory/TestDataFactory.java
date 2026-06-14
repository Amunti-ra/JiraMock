package com.newjirasystem.app.dataFactory;

import com.newjirasystem.app.comentarios.Comentario;
import com.newjirasystem.app.comentarios.ComentarioDTO;
import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.tickets.*;
import com.newjirasystem.app.usuarios.Rol;
import com.newjirasystem.app.usuarios.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public class TestDataFactory {

    public static Ticket crearTicket() {
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Usuario usuario = TestDataFactory.crearUsuario();

        return new Ticket(
                "TEST-1",
                TipoTicket.TAREA,
                PrioridadTicket.LOW,
                proyecto,
                usuario,
                "prueba",
                "descripcion"
        );
    }

    public static Usuario crearUsuario() {
        return new Usuario("test", Rol.USER, "12345");
    }

    public static Proyecto crearProyecto() {
        return new Proyecto("Test de proyecto", "TEST");
    }

    public static Ticket crearTicketActivo(Usuario usuario, Proyecto proyecto) {

        String clave = proyecto.getCodigoProyecto() + "-" + (proyecto.getContadorTickets() + 1);
        proyecto.aumentarContador();

        return new Ticket(
                clave,
                TipoTicket.TAREA,
                PrioridadTicket.LOW,
                proyecto,
                usuario,
                "test",
                "prueba ticket"
        );
    }

    public static Ticket crearTicketInactivo(Usuario usuario, Proyecto proyecto) {
        Ticket ticket = crearTicketActivo(usuario, proyecto);

        ticket.borrarTicket();

        return ticket;
    }

    public static TicketDTO crearTicketDto() {
        return new TicketDTO(
                1L,
                "test titulo",
                "test descripcion",
                "TEST-1",
                "1",
                TipoTicket.BUG,
                EstadoTicket.POR_HACER,
                PrioridadTicket.LOW,
                "usuario test",
                "asignado test",
                LocalDateTime.now()
        );
    }

    public static List<TicketDTO> crearListaTicketDTO() {
        return List.of(
                new TicketDTO(
                        1L,
                        "test titulo",
                        "test descripcion",
                        "TEST-1",
                        "1",
                        TipoTicket.BUG,
                        EstadoTicket.POR_HACER,
                        PrioridadTicket.LOW,
                        "usuario test",
                        "asignado test",
                        LocalDateTime.now()
                ),
                new TicketDTO(
                        2L,
                        "test titulo",
                        "test descripcion",
                        "TEST-2",
                        "1",
                        TipoTicket.BUG,
                        EstadoTicket.POR_HACER,
                        PrioridadTicket.LOW,
                        "usuario test",
                        "asignado test",
                        LocalDateTime.now()
                )
        );
    }


    public static Comentario crearComentario(Ticket ticket, Usuario usuario) {

        return new Comentario("test comentario", ticket, usuario);

    }


    public static ComentarioDTO crearComentarioDTO() {
        return new ComentarioDTO(
                1L,
                "texto test",
                1L,
                "nombre test",
                LocalDateTime.of(2026, 6, 14,12, 0, 0),
                LocalDateTime.now()
        );
    }

    public static List<ComentarioDTO> crearListaComentarioDTO() {
        return List.of(
                new ComentarioDTO(
                        1L,
                        "texto test",
                        1L,
                        "nombre test",
                        LocalDateTime.of(2026, 6, 14,12, 0, 0),
                        LocalDateTime.now()
                ),
                new ComentarioDTO(
                        2L,
                        "texto test",
                        1L,
                        "nombre test",
                        LocalDateTime.of(2026, 6, 14,12, 0, 0),
                        LocalDateTime.now()
                )

        );
    }


}
