package com.newjirasystem.app.entity;

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
