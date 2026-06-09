package com.newjirasystem.app.tickets;

import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.proyectos.ProyectosRepository;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    private final UsuariosRepository usuariosRepository;
    private final TicketsRepository ticketsRepository;
    private final ProyectosRepository proyectosRepository;
    private final TicketMapper ticketMapper;

    public TicketService(UsuariosRepository usuariosRepository, TicketsRepository ticketsRepository, ProyectosRepository proyectosRepository, TicketMapper ticketMapper) {
        this.usuariosRepository = usuariosRepository;
        this.ticketsRepository = ticketsRepository;
        this.proyectosRepository = proyectosRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional
    public TicketDTO postTicket(CrearTicketDTO crearTicketDTO) {
        // saca datos del dto
        String titulo = crearTicketDTO.titulo();
        String descripcion = crearTicketDTO.descripcion();
        Long idCreador = crearTicketDTO.idCreador();
        Long idProyecto = crearTicketDTO.idPoryecto();
        String prioridad = crearTicketDTO.prioridad();
        String tipo = crearTicketDTO.tipo();

        // transforma la string al enum de tipo y prioridad
        TipoTicket tipoTicket = TipoTicket.valueOf(tipo);
        PrioridadTicket prioridadTicket = PrioridadTicket.valueOf(prioridad);

        // busca al usuario creado y al proyecto al que se asignará el ticket
        Usuario creador = this.usuariosRepository.findById(idCreador)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario creado no existe"));

        Proyecto proyecto = this.proyectosRepository.findById(idProyecto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El proyecto asignado no existe"));


        // saca el número actual de tickets del proyecto, le suma 1, actualiza el número de tickets
        // y usa el nuevo número para generar la clave del ticket
        int siguienteNumero = proyecto.getContadorTickets() + 1;
        proyecto.setContadorTickets(siguienteNumero);
        String clave = proyecto.getCodigoProyecto() + "-" + siguienteNumero;

        // guarda el proyecto, ya que se aumentó el nº de tickets
        this.proyectosRepository.save(proyecto);

        // construye un ticket con los parámetros sacados antes
        Ticket ticket = new Ticket(clave, tipoTicket, prioridadTicket, proyecto, creador, titulo, descripcion);

        // guarda el ticket en el respositorio
        this.ticketsRepository.save(ticket);

        // mapea el ticket a un ticketDTO para devolverlo al front
        return this.ticketMapper.toTicketDTO(ticket);
    }

    public List<TicketDTO> getTickets() {

        List<Ticket> listaTicket = ticketsRepository.findByEstadoNot(EstadoTicket.BORRADO);

        return listaTicket.stream()
                .map(this.ticketMapper::toTicketDTO)
                .toList();
    }

    public TicketDTO getTicketById(Long id) {
        return ticketsRepository.findByIdAndEstadoNot(id, EstadoTicket.BORRADO)
                .map(this.ticketMapper::toTicketDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado"));
    }

    /**
     * Actualiza un ticket ya existente buscándolo por su ID con los datos de ActualizarTicketDTO.
     * Si algún campo del ActualizarTicketDTO es null, ese campo no será actualizado en el ticket.
     * Updates an existing ticket identified by its ID with the provided data from the ActualizarTicketDTO.
     * If a field in the ActualizarTicketDTO is null, that field will not be updated in the ticket.
     *
     * @param id  ID del ticket a actualizar
     * @param dto un objeto ActualizarTicketDTO que contendrá los datos a actualizar
     * @return un TicketDTO reprensentando el ticket con los datos actualizados
     * @throws RuntimeException si el ID del ticket o el ID del usuario al que se asigna el ticket
*                               no se encuentran
     */
    @Transactional
    public TicketDTO putTicketById(Long id, ActualizarTicketDTO dto) {

        // obtiene el ticket por ID
        Ticket ticket = ticketsRepository.findByIdAndEstadoNot(id, EstadoTicket.BORRADO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado"));

        // comprueba si los datos del ActualizarTicketDTO están presentes para
        // actualizarlos en el Ticket
        if (dto.titulo() != null) {
            ticket.setTitulo(dto.titulo());
        }

        if (dto.descripcion() != null) {
            ticket.setDescripcion(dto.descripcion());
        }

        if (dto.idAsignado() != null) {
            ticket.setAsignado(this.usuariosRepository.findById(dto.idAsignado())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El Usuario a asignar no existe")));
        }

        if (dto.estado() != null) {
            ticket.setEstado(EstadoTicket.valueOf(dto.estado()));
        }

        if (dto.prioridad() != null) {
            ticket.setPrioridad(PrioridadTicket.valueOf(dto.prioridad()));
        }

        if (dto.tipo() != null) {
            ticket.setTipo(TipoTicket.valueOf(dto.tipo()));
        }

        return this.ticketMapper.toTicketDTO(ticket);
    }

    public void deleteTicketById(Long id) {
        Ticket ticket = this.ticketsRepository.findByIdAndEstadoNot(id, EstadoTicket.BORRADO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado"));

        ticket.setEstado(EstadoTicket.BORRADO);

        this.ticketsRepository.save(ticket);
    }
}
