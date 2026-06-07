package com.newjirasystem.app.tickets;

public enum EstadoTicket {
    POR_HACER(1),
    EN_PROGRESO(2),
    RESUELTO(3),
    BORRADO(4);

    private final int id;

    EstadoTicket(int id) {
        this.id = id;
    }

    public int getId() {return id;}
}
