package com.newjirasystem.app.tickets;

import com.newjirasystem.app.dataFactory.TestDataFactory;
import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.proyectos.ProyectosRepository;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketsRepositoryTest {

    @Autowired
    TicketsRepository ticketsRepository;

    @Autowired
    ProyectosRepository proyectosRepository;

    @Autowired
    UsuariosRepository usuariosRepository;

    @Test
    void findAllByActivoTrue_ShouldReturnListOfAllActiveTrueTickets() {
        // given
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        usuariosRepository.save(usuario);
        proyectosRepository.save(proyecto);

        for (int i = 0; i < 5; i++) {
            ticketsRepository.save(TestDataFactory.crearTicketActivo(usuario, proyecto));
        }

        // when
        List<Ticket> listaTickets = ticketsRepository.findAllByActivoTrue().stream()
                .filter(t -> t.getCreador().equals(usuario))
                .toList();

        // then
        assertTrue(listaTickets.stream().allMatch(Ticket::isActivo));
        assertFalse(listaTickets.stream().anyMatch(t -> !t.isActivo()));
        assertEquals(3, listaTickets.size());
    }

    @Test
    void findByIdAndActivoTrue_WhenIdExistsAndActivoIsTrue_ShouldReturnTicket() {
        // given
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        usuariosRepository.save(usuario);
        proyectosRepository.save(proyecto);

        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);
        ticketsRepository.save(ticket);

        // when
        assertTrue(ticketsRepository.findByIdAndActivoTrue(ticket.getId()).isPresent());
    }

    @Test
    void findByIdAndActivoTrue_WhenIdExistsAndActivoIsFalse_ShouldReturnEmptyOptionalTicket() {
        // given
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        usuariosRepository.save(usuario);
        proyectosRepository.save(proyecto);

        Ticket ticket = TestDataFactory.crearTicketInactivo(usuario, proyecto);
        ticketsRepository.save(ticket);

        // when
        Optional<Ticket> resultado = ticketsRepository.findByIdAndActivoTrue(ticket.getId());

        // then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByIdAndActivoTrue_WhenIdDoesNotExist_ShouldReturnEmptyOptionalTicket() {
        assertTrue(ticketsRepository.findByIdAndActivoTrue(99999999L).isEmpty());
    }

    @Test
    void testRelationshipForUsuarioAndProyectoToNewTicket() {
        // given
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);
        usuariosRepository.save(usuario);
        proyectosRepository.save(proyecto);

        // when
        Ticket resultado = ticketsRepository.save(ticket);

        // then
        assertEquals(usuario, resultado.getCreador());
        assertEquals(proyecto, resultado.getProyecto());
    }

    @Test
    void testSoftDeleteSuccess() {
        // given
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);
        usuariosRepository.save(usuario);
        proyectosRepository.save(proyecto);
        ticketsRepository.save(ticket);

        // when
        ticket.borrarTicket();
        ticketsRepository.save(ticket);

        // then
        assertFalse(ticket.isActivo());
        assertTrue(ticketsRepository.findById(ticket.getId()).isPresent());
        assertTrue(ticketsRepository.findByIdAndActivoTrue(ticket.getId()).isEmpty());
    }
}