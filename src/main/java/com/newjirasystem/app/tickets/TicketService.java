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
}
