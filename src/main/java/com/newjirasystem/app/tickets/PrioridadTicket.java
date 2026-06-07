package com.newjirasystem.app.tickets;

public enum PrioridadTicket {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    BLOCKER(4);

    private final int id;

    PrioridadTicket(int id) {
        this.id = id;
    }

    public int getId() {return id;}
}
