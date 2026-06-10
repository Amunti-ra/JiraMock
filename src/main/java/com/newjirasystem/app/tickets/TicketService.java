package com.newjirasystem.app.tickets;

import com.newjirasystem.app.exception.ProyectoNoEncontradoException;
import com.newjirasystem.app.exception.TicketNoEncontradoException;
import com.newjirasystem.app.exception.UsuarioNoEncontradoException;
import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.proyectos.ProyectosRepository;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.List;

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
                .orElseThrow(() -> new UsuarioNoEncontradoException(crearTicketDTO.idCreador()));

        Proyecto proyecto = this.proyectosRepository.findById(idProyecto)
                .orElseThrow(() -> new ProyectoNoEncontradoException(crearTicketDTO.idPoryecto()));


        // clave del proyecto y suma uno al número de tickets actual (ex:JIRA-1)
        String clave = proyecto.getCodigoProyecto() + "-" + (proyecto.getContadorTickets() + 1);

        proyecto.aumentarContador();

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
        return ticketsRepository.findAllByActivoTrue().stream()
                .map(this.ticketMapper::toTicketDTO)
                .toList();
    }

    public List<TicketDTO> getTicketsByProyectoId(Long id) {
        // comprueba que el proyecto exista
        if (!this.proyectosRepository.existsById(id)) {
            throw new ProyectoNoEncontradoException(id);
        }

        return this.ticketsRepository.findAllByProyectoIdAndActivoTrue(id).stream()
                .map(this.ticketMapper::toTicketDTO)
                .toList();
    }

    public List<TicketDTO> getTicketsByPrioridad(PrioridadTicket prioridad) {
        return this.ticketsRepository.findAllByPrioridadAndActivoTrue(prioridad).stream()
                .map(this.ticketMapper::toTicketDTO)
                .toList();
    }

    public List<TicketDTO> getTicketsByAsignadoId(Long id) {
        // comprueba que el usuario exista para no devolver una lista vacía
        if (!this.usuariosRepository.existsById(id)) {
            throw new UsuarioNoEncontradoException(id);
        }

        return this.ticketsRepository.findAllByAsignadoIdAndActivoTrue(id).stream()
                .map(this.ticketMapper::toTicketDTO)
                .toList();
    }

    public List<TicketDTO> getTicketsByEstado(EstadoTicket estado) {
        return this.ticketsRepository.findAllByEstadoAndActivoTrue(estado).stream()
                .map(this.ticketMapper::toTicketDTO)
                .toList();
    }

    public TicketDTO getTicketById(Long id) {
        return ticketsRepository.findByIdAndActivoTrue(id)
                .map(this.ticketMapper::toTicketDTO)
                .orElseThrow(() -> new TicketNoEncontradoException(id));
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
        Ticket ticket = ticketsRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new TicketNoEncontradoException(id));

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
                    .orElseThrow(() -> new UsuarioNoEncontradoException(dto.idAsignado())));
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

    @Transactional
    public void deleteTicketById(Long id) {
        Ticket ticket = this.ticketsRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new TicketNoEncontradoException(id));

        ticket.borrarTicket();
    }
}
