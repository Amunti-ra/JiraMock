package com.newjirasystem.app.controller;

import com.newjirasystem.app.dto.CrearTicketDTO;
import com.newjirasystem.app.dto.TicketCreadoDTO;
import com.newjirasystem.app.entity.Ticket;
import com.newjirasystem.app.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketCreadoDTO> crearTicket(@RequestBody CrearTicketDTO ticket) {

        TicketCreadoDTO ticketCreado = ticketService.crearTicket(ticket);

        return new ResponseEntity<>(ticketCreado, HttpStatus.CREATED);
    }
}
