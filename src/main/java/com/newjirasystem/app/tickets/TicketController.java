package com.newjirasystem.app.tickets;

import com.newjirasystem.app.comentarios.ComentarioDTO;
import com.newjirasystem.app.comentarios.ComentarioService;
import com.newjirasystem.app.comentarios.CrearComentarioDTO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "OK Lista de tickets completa o que cumplan los filtros",
                    content = {@Content(schema = @Schema(implementation = TicketDTO.class))}),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND Ticket, Usuario o Proyecto no encontrado", content = @Content)
    })
    @Tag(name = "Tickets", description = "metodo para la gestion y consulta de tickets")
    @GetMapping("/get")
    public ResponseEntity<List<TicketDTO>> getTickets(@Parameter (description = "Filtrar por Id de usuario que tenga el ticket asignado")
                                                          @RequestParam(required = false) Long asignadoId,
                                                      @Parameter (description = "Filtrar por id del proyecto al cual esta el ticket asignado")
                                                      @RequestParam(required = false) Long proyectoId,
                                                      @RequestParam(required = false) PrioridadTicket prioridad,
                                                      @RequestParam(required = false) EstadoTicket estado) {

        return ResponseEntity.ok(
                ticketService.getFilteredTickets(asignadoId, proyectoId, prioridad, estado));
    }

    /**
     * Obtiene el TicketDTO del ticket que tenga el id solicitado.
     *
     * @param id del ticket
     * @return Si el ticket existe, devuelve un response entity con el TicketDTO y 200 ok.
     * Si no se encuentra, 404.
     */
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "OK Ticket con el ID facilitado",
                    content = {@Content(schema = @Schema(implementation = TicketDTO.class))}),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND Ticket con ID facilitado no encontrado", content = @Content)})
    @Tag(name = "Tickets")
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@Parameter (description = "Id del ticket a buscar") @PathVariable Long id) {
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
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "CREATED TicketDTO con los datos del ticket creado",
                    content = {@Content(schema = @Schema(implementation = TicketDTO.class))}),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND Usuario o Proyecto que se intenta asignar el ticket no existe", content = @Content)})
    @Tag(name = "Tickets")
    @PostMapping
    public ResponseEntity<TicketDTO> postTicket(@Valid @RequestBody CrearTicketDTO ticket) {
        TicketDTO ticketCreado = ticketService.postTicket(ticket);

        return new ResponseEntity<>(ticketCreado, HttpStatus.CREATED);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "OK TicketDTO con los datos del ticket actualizados",
                    content = {@Content(schema = @Schema(implementation = TicketDTO.class))}),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND Ticket que se quiere actualizar no encontrado", content = @Content)})
    @PatchMapping("/{id}")
    @Tag(name = "Tickets")
    public ResponseEntity<TicketDTO> patchTicketById(@PathVariable Long id, @Valid @RequestBody ActualizarTicketDTO actualizarTicketDTO) {
        TicketDTO ticketActualizado = ticketService.patchTicketById(id, actualizarTicketDTO);

        return new ResponseEntity<>(ticketActualizado, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "NO_CONTENT"),
            @ApiResponse(responseCode = "404", description = "NOT_FOUND Ticket que se quiere eliminar no encontrado", content = @Content)})
    @DeleteMapping("/{id}")
    @Tag(name = "Tickets")
    public ResponseEntity<Void> deleteTicketById(@PathVariable Long id) {
        ticketService.deleteTicketById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // mappings que dependen de un ticket pero son de otros componentes
    @Tag(name = "Comentarios")
    @PostMapping("/{idTicket}/comentarios")
    public ResponseEntity<ComentarioDTO> postComentario(@PathVariable Long idTicket, @Valid @RequestBody CrearComentarioDTO dto) {
        ComentarioDTO comentarioCreado = comentarioService.postComentario(idTicket, dto);

        return new ResponseEntity<>(comentarioCreado, HttpStatus.CREATED);
    }

    @Tag(name = "Comentarios")
    @GetMapping("/{idTicket}/comentarios")
    public List<ComentarioDTO> getComentariosByTicketId(@PathVariable Long idTicket) {
        return comentarioService.getComenatariosByTicketId(idTicket);
    }
}
