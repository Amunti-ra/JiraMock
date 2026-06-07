package com.newjirasystem.app.tickets;

import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.proyectos.ProyectosRepository;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        int prioridad = crearTicketDTO.prioridad();
        int tipo = crearTicketDTO.tipo();


        // asigna de número a tipo de ticket
        TipoTicket tipoTicket = switch (tipo) {
            case 1 -> TipoTicket.SUBTAREA;
            case 2 -> TipoTicket.BUG;
            case 3 -> TipoTicket.TAREA;
            case 4 -> TipoTicket.EPICA;
            default -> throw new IllegalArgumentException("Tipo inválido");
        };


        // asigna de número a prioridad
        PrioridadTicket prioridadTicket = switch (prioridad) {
            case 1 -> PrioridadTicket.LOW;
            case 2 -> PrioridadTicket.MEDIUM;
            case 3 -> PrioridadTicket.HIGH;
            case 4 -> PrioridadTicket.BLOCKER;
            default -> throw new IllegalArgumentException("Tipo inválido");
        };


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
