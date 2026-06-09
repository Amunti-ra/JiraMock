package com.newjirasystem.app.tickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketsRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByActivoTrue();

    List<Ticket> findAllByProyectoIdAndActivoTrue(Long id);

    List<Ticket> findAllByAsignadoIdAndActivoTrue(Long id);

    List<Ticket> findByPrioridadAndActivoTrue(PrioridadTicket prioridad);

    Optional<Ticket> findByIdAndActivoTrue(Long id);
}
