package com.newjirasystem.app.tickets;

import org.springframework.data.jpa.domain.Specification;


public class TicketSpecifications {

    public static Specification<Ticket> isActivo() {
        return (root, query, criteriBuilder) ->
                criteriBuilder.isTrue(root.get("activo"));
    }

    public static Specification<Ticket> porTicketIdSpec(Long idTicket) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), idTicket);
    }

    public static Specification<Ticket> getTicketByIdEmpleadoSpec(Long idEmpleado) {
        return (root, query, criteriaBuilder) ->
                idEmpleado == null ? null :
                        criteriaBuilder.equal(root.get("asignado").get("id"), idEmpleado);
    }

    public static Specification<Ticket> getTicketByIdProyectoSpec(Long idProyecto) {
        return (root, query, criteriaBuilder) ->
                idProyecto == null ? null :
                        criteriaBuilder.equal(root.get("proyecto").get("id"), idProyecto);
    }

    public static Specification<Ticket> getTicketByTipoSpec(TipoTicket tipo) {
        return (root, query, criteriaBuilder) ->
                tipo == null ? null :
                        criteriaBuilder.equal(root.get("tipo"), tipo);
    }

    public static Specification<Ticket> getTicketByPrioridadSpec(PrioridadTicket prioridad) {
        return (root, query, criteriaBuilder) ->
                prioridad == null ? null :
                        criteriaBuilder.equal(root.get("prioridad"), prioridad);
    }

    public static Specification<Ticket> getTicketByEstadoSpec(EstadoTicket estado) {
        return (root, query, criteriaBuilder) ->
                estado == null ? null :
                        criteriaBuilder.equal(root.get("estado"), estado);
    }

}
