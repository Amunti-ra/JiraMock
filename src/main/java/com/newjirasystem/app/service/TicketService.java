package com.newjirasystem.app.service;

import com.newjirasystem.app.dto.CrearTicketDTO;
import com.newjirasystem.app.dto.TicketCreadoDTO;
import com.newjirasystem.app.entity.*;
import com.newjirasystem.app.repository.ProyectosRepository;
import com.newjirasystem.app.repository.TicketsRepository;
import com.newjirasystem.app.repository.UsuariosRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TicketService {
    private final UsuariosRepository usuariosRepository;
    private final TicketsRepository ticketsRepository;
    private final ProyectosRepository proyectosRepository;

    public TicketService(UsuariosRepository usuariosRepository, TicketsRepository ticketsRepository, ProyectosRepository proyectosRepository) {
        this.usuariosRepository = usuariosRepository;
        this.ticketsRepository = ticketsRepository;
        this.proyectosRepository = proyectosRepository;
    }

    public TicketCreadoDTO crearTicket(CrearTicketDTO crearTicketDTO) {
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
            default -> null;
        };


        // asigna de número a prioridad
        PrioridadTicket prioridadTicket = switch (prioridad) {
            case 1 -> PrioridadTicket.LOW;
            case 2 -> PrioridadTicket.MEDIUM;
            case 3 -> PrioridadTicket.HIGH;
            case 4 -> PrioridadTicket.BLOCKER;
            default -> null;
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


        // sacando los datos para construir el TicketCreadoDTO
        // EDIT : se podía hacer directo al crear el DTO

        /*Long idTicketCreado = ticketGuardado.getId();
        String claveTicketCreado = ticketGuardado.getClave();
        Long tipoTicketCreado = (long) ticketGuardado.getTipo().getId();
        Long estadoTicketCreado = (long) ticketGuardado.getEstado().getId();
        Long prioridadTicketCreado = (long) ticketGuardado.getPrioridad().getId();
        String proyectoTicketCreado = ticketGuardado.getProyecto().getNombre();
        String creadorTicketCreado = ticketGuardado.getCreador().getNombre();
        String tituloTicketCreado = ticketGuardado.getTitulo();
        String descripcionTicketCreado = ticketGuardado.getDescripcion();
        LocalDateTime fechaCreacionTicketCreado = ticketGuardado.getFechaCreacion();*/


        return new TicketCreadoDTO(
                ticketGuardado.getId(),
                ticketGuardado.getTitulo(),
                ticketGuardado.getDescripcion(),
                ticketGuardado.getClave(),
                ticketGuardado.getProyecto().getNombre(),
                (long) ticketGuardado.getTipo().getId(),
                (long) ticketGuardado.getEstado().getId(),
                (long) ticketGuardado.getPrioridad().getId(),
                ticketGuardado.getCreador().getNombre(),
                ticketGuardado.getFechaCreacion()
        );
    }
}
