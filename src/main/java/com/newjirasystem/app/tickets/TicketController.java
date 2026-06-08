package com.newjirasystem.app.tickets;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Obtiene una lista con los tickets de la base de datos en forma de TicketDTO
     *
     * @return lista con los ticketsDTO
     */
    @GetMapping
    public List<TicketDTO> getTickets() {
        return ticketService.getTickets();
    }

    /**
     * Obtiene el TicketDTO del ticket que tenga el id solicitado.
     *
     * @param id del ticket
     * @return Si el ticket existe, devuelve un response entity con el TicketDTO y 200 ok.
     * Si no se encuentra, 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ticketDTO -> new ResponseEntity<>(ticketDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Crea un ticket.
     *
     * Recibe en el request body los detalles para crear un ticket, recibe del TicketService un
     * TicketDTO para poder devolver con todos los detalles el nuevo ticket creado junto con la
     * respuesta HTTP
     *
     * @param ticket
     * @return TicketDTO y respuesta HTTP 201 creado.
     */
    @PostMapping
    public ResponseEntity<TicketDTO> postTicket(@Valid @RequestBody CrearTicketDTO ticket) {
        TicketDTO ticketCreado = ticketService.postTicket(ticket);

        return new ResponseEntity<>(ticketCreado, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> putTicketById(@PathVariable Long id, @Valid @RequestBody ActualizarTicketDTO actualizarTicketDTO) {
        TicketDTO ticketActualizado = ticketService.putTicketById(id, actualizarTicketDTO);

        return new ResponseEntity<>(ticketActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable Long id) {
        ticketService.deleteTicketById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
