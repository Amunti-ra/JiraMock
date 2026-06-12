package com.newjirasystem.app.dataFactory;

import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.tickets.PrioridadTicket;
import com.newjirasystem.app.tickets.Ticket;
import com.newjirasystem.app.tickets.TipoTicket;
import com.newjirasystem.app.usuarios.Rol;
import com.newjirasystem.app.usuarios.Usuario;

public class TestDataFactory {

    public static Ticket crearTicket() {
        Proyecto proyecto = new Proyecto("DAM", "DAM");
        Usuario usuario = new Usuario("mati", Rol.USER, "123");

        return new Ticket(
                "DAM-1",
                TipoTicket.TAREA,
                PrioridadTicket.LOW,
                proyecto,
                usuario,
                "prueba",
                "descripcion"
        );
    }
}
