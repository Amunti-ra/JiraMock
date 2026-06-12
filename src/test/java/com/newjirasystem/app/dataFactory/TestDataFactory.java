package com.newjirasystem.app.dataFactory;

import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.tickets.PrioridadTicket;
import com.newjirasystem.app.tickets.Ticket;
import com.newjirasystem.app.tickets.TipoTicket;
import com.newjirasystem.app.usuarios.Rol;
import com.newjirasystem.app.usuarios.Usuario;

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
}
