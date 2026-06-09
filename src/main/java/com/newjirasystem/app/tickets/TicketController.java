package com.newjirasystem.app.tickets;

import com.newjirasystem.app.comentarios.ComentarioDTO;
import com.newjirasystem.app.comentarios.ComentarioService;
import com.newjirasystem.app.comentarios.CrearComentarioDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final ComentarioService comentarioService;

    public TicketController(TicketService ticketService, ComentarioService comentarioService) {
        this.ticketService = ticketService;
        this.comentarioService = comentarioService;
    }

    // mappings de tickets
    /**
     * Obtiene una lista con los tickets de la base de datos en forma de TicketDTO
     *
     * @return lista con los ticketsDTO
     */
    @GetMapping
    public ResponseEntity<List<TicketDTO>> getTickets(@RequestParam(required = false) Long asignadoId,
                                                      @RequestParam(required = false) Long proyectoId,
                                                      @RequestParam(required = false) String prioridad) {

        if (asignadoId != null) {
            return new ResponseEntity<>(ticketService.getTicketsByAsignadoId(asignadoId), HttpStatus.OK);
        }

        if (proyectoId != null) {
            return new ResponseEntity<>(ticketService.getTicketsByProyectoId(proyectoId), HttpStatus.OK);
        }

        if (prioridad != null) {
            return new ResponseEntity<>(ticketService.getTicketsByPrioridad(prioridad), HttpStatus.OK);
        }

        return new ResponseEntity<>(ticketService.getTickets(), HttpStatus.OK);
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
        TicketDTO ticket = ticketService.getTicketById(id);

        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    /**
     * Crea un nuevo ticket en el sistema basado en los detalles proporcionados.
     *
     * @param ticket los detalles del ticket a crear, encapsulados en un {@link CrearTicketDTO}.
     * Este objeto debe ser válido e incluir los campos obligatorios como el título,
     * el id del creador, el id del proyecto y, opcionalmente, la prioridad y el tipo.
     *
     * @return un {@link ResponseEntity} que contiene el ticket creado como un {@link TicketDTO}
     * con un estado 201 CREATED.
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


    // mappings que dependen de un ticket pero son de otros componentes

    @PostMapping("/{idTicket}/comentarios")
    public ResponseEntity<ComentarioDTO> postComentario(@PathVariable Long idTicket, @Valid @RequestBody CrearComentarioDTO dto) {
        ComentarioDTO comentarioCreado = comentarioService.postComentario(idTicket, dto);

        return new ResponseEntity<>(comentarioCreado, HttpStatus.CREATED);
    }

    @GetMapping("/{idTicket}/comentarios")
    public List<ComentarioDTO> getComentariosByTicketId(@PathVariable Long idTicket) {
        return comentarioService.getComenatariosByTicketId(idTicket);
    }
}
