package com.newjirasystem.app.tickets;

import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.proyectos.ProyectosRepository;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.springframework.stereotype.Service;

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

    public TicketDTO crearTicket(CrearTicketDTO crearTicketDTO) {
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
        Usuario creador = this.usuariosRepository.findById(idCreador).orElseThrow();
        Proyecto proyecto = this.proyectosRepository.findById(idProyecto).orElseThrow();


        // saca el número actual de tickets del proyecto, le suma 1, actualiza el número de tickets
        // y usa el nuevo número para generar la clave del ticket
        int siguienteNumero = proyecto.getContadorTickets() + 1;
        proyecto.setContadorTickets(siguienteNumero);
        String clave = proyecto.getCodigoProyecto() + "-" + siguienteNumero;


        // construye un ticket con los parámetros sacados antes
        Ticket ticket = new Ticket(clave, tipoTicket, prioridadTicket, proyecto, creador, titulo, descripcion);


        // guarda el ticket en el respositorio y recibe el ticket guardado para generar
        // el dto de ticket creado después
        Ticket ticketGuardado = this.ticketsRepository.save(ticket);


        // guarda el proyecto, ya que se aumentó el nº de tickets
        this.proyectosRepository.save(proyecto);

        // mapea el ticket a un ticketDTO para devolverlo al front
        return new TicketMapper().toTicketDTO(ticketGuardado);
    }

    public List<TicketDTO> getTickets() {

        List<Ticket> listaTicket = ticketsRepository.findAll();

        return listaTicket.stream()
                .map(this.ticketMapper::toTicketDTO)
                .toList();
    }

    public Optional<TicketDTO> getTicketById(Long id) {
        return ticketsRepository.findById(id)
                .map(this.ticketMapper::toTicketDTO);
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
    public TicketDTO putTicketById(Long id, ActualizarTicketDTO dto) {

        // obtiene el ticket por ID
        Ticket ticket = ticketsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con el id: " + id));

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
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
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

        // guarda el ticket actualizado y lo almacena para crear un TicketDTO de respuesta
        Ticket ticketActualizado = this.ticketsRepository.save(ticket);

        return new TicketMapper().toTicketDTO(ticketActualizado);
    }
}
