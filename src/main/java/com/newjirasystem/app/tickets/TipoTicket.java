package com.newjirasystem.app.tickets;

public enum TipoTicket {
    EPICA(4),
    TAREA(3),
    BUG(2),
    SUBTAREA(1);

    private final int id;

    TipoTicket(int id) {
        this.id = id;
    }

    public int getId() {return id;}
}
