package com.newjirasystem.app.comentarios;

import com.newjirasystem.app.dataFactory.TestDataFactory;
import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.proyectos.ProyectosRepository;
import com.newjirasystem.app.tickets.Ticket;
import com.newjirasystem.app.tickets.TicketsRepository;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ComentarioRepositoryTest {

    @Autowired
    ComentarioRepository comentarioRepository;

    @Autowired
    TicketsRepository ticketsRepository;

    @Autowired
    UsuariosRepository usuariosRepository;

    @Autowired
    ProyectosRepository proyectosRepository;

    @Test
    void findAllByTicketIdAndActivoTrue_ShouldReturnListOfOnlyActiveComentarios() {
        // given
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);

        proyectosRepository.save(proyecto);
        usuariosRepository.save(usuario);
        ticketsRepository.save(ticket);

        for (int i = 0; i < 3; i++) {
            comentarioRepository.save(TestDataFactory.crearComentario(ticket, usuario));
        }

        Comentario comentarioInactivo = TestDataFactory.crearComentario(ticket, usuario);
        comentarioInactivo.borrarComentario();
        comentarioRepository.save(comentarioInactivo);

        // when
        List<Comentario> comentarios = comentarioRepository.findAllByTicketIdAndActivoTrue(ticket.getId());

        // then
        assertFalse(comentarios.isEmpty());
        assertTrue(comentarios.stream().allMatch(Comentario::isActivo));
        assertEquals(3, comentarios.size());
    }

    @Test
    void findAllByTicketIdAndActivoTrue_WhenTickedIdDoesNotExist_ShouldReturnEmptyOptionalTicket() {
        assertTrue(comentarioRepository.findAllByTicketIdAndActivoTrue(-1L).isEmpty());
    }

    @Test
    void findAllByTicketIdAndActivoTrue_ShouldReturnAllActiveComentariosRegardlessOfAuthor() {
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Usuario usuario2 = TestDataFactory.crearUsuario();

        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);

        proyectosRepository.save(proyecto);
        usuariosRepository.save(usuario);
        usuariosRepository.save(usuario2);
        ticketsRepository.save(ticket);

        for (int i = 0; i < 3; i++) {
            comentarioRepository.save(TestDataFactory.crearComentario(ticket, usuario));
        }

        Comentario inactivo =  TestDataFactory.crearComentario(ticket, usuario);
        inactivo.borrarComentario();
        comentarioRepository.save(inactivo);

        for (int i = 0; i < 3; i++) {
            comentarioRepository.save(TestDataFactory.crearComentario(ticket, usuario2));
        }

        List<Comentario>  comentarios = comentarioRepository.findAllByTicketIdAndActivoTrue(ticket.getId());

        assertFalse(comentarios.isEmpty());
        assertTrue(comentarios.stream().allMatch(Comentario::isActivo));
        assertEquals(6, comentarios.size());
    }

    @Test
    void findAllByTicketIdAndActivoTrue_WhenTicketHasNoComments_ShouldReturnEmptyList() {
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);

        proyectosRepository.save(proyecto);
        usuariosRepository.save(usuario);
        ticketsRepository.save(ticket);

        List<Comentario> result = comentarioRepository.findAllByTicketIdAndActivoTrue(ticket.getId());

        assertTrue(result.isEmpty());
    }


    @Test
    void findAllByAutorIdAndActivoTrue_ShouldReturnListOfOnlyActiveComentarios() {
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);

        proyectosRepository.save(proyecto);
        usuariosRepository.save(usuario);
        ticketsRepository.save(ticket);

        Comentario activo = TestDataFactory.crearComentario(ticket, usuario);
        Comentario inactivo1 = TestDataFactory.crearComentario(ticket, usuario);
        inactivo1.borrarComentario();
        Comentario inactivo2 = TestDataFactory.crearComentario(ticket, usuario);
        inactivo2.borrarComentario();

        comentarioRepository.save(activo);
        comentarioRepository.save(inactivo1);
        comentarioRepository.save(inactivo2);

        // when
        List<Comentario> comentario = comentarioRepository.findAllByAutorIdAndActivoTrue(usuario.getId());

        // then
        assertFalse(comentario.isEmpty());
        assertTrue(comentario.stream().allMatch(Comentario::isActivo));
        assertEquals(1, comentario.size());
    }

    @Test
    void findAllByAutorIdAndActivoTrue_WhenAutorIdDoesNotExist_ShouldReturnEmptyOptionalTicket() {
        assertTrue(comentarioRepository.findAllByAutorIdAndActivoTrue(-1L).isEmpty());
    }

    @Test
    void findAllByAutorIdAndActivoTrue_ShouldReturnAllActiveComentariosRegardlessOfTicket() {
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);
        Ticket ticket2 = TestDataFactory.crearTicketActivo(usuario, proyecto);

        proyectosRepository.save(proyecto);
        usuariosRepository.save(usuario);
        ticketsRepository.save(ticket);
        ticketsRepository.save(ticket2);


        for (int i = 0; i < 3; i++) {
            comentarioRepository.save(TestDataFactory.crearComentario(ticket, usuario));
            comentarioRepository.save(TestDataFactory.crearComentario(ticket2, usuario));
        }

        Comentario inactivo = TestDataFactory.crearComentario(ticket, usuario);
        inactivo.borrarComentario();

        Comentario inactivo1 = TestDataFactory.crearComentario(ticket2, usuario);
        inactivo1.borrarComentario();

        comentarioRepository.save(inactivo);
        comentarioRepository.save(inactivo1);

        List<Comentario> lista = comentarioRepository.findAllByAutorIdAndActivoTrue(usuario.getId());

        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(Comentario::isActivo));
        assertEquals(6, lista.size());
    }

    @Test
    void findAllByAutorIdAndActivoTrue_WhenAuthorHasNoComments_ShouldReturnEmptyList() {
        Usuario usuario = TestDataFactory.crearUsuario();
        usuariosRepository.save(usuario);

        List<Comentario> result = comentarioRepository.findAllByAutorIdAndActivoTrue(usuario.getId());

        assertTrue(result.isEmpty());
    }



    @Test
    void findByIdAndActivoTrue_WhenIdExistsAndActivoIsTrue_ShouldReturnOptionalComentario() {
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);

        proyectosRepository.save(proyecto);
        usuariosRepository.save(usuario);
        ticketsRepository.save(ticket);

        Comentario comentario = TestDataFactory.crearComentario(ticket, usuario);
        comentarioRepository.save(comentario);

        assertTrue(comentarioRepository.findByIdAndActivoTrue(comentario.getId()).isPresent());
    }

    @Test
    void findByIdAndActivoTrue_WhenIdExistsAndActivoIsFalse_ShouldReturnEmptyOptionalComentario() {
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);

        proyectosRepository.save(proyecto);
        usuariosRepository.save(usuario);
        ticketsRepository.save(ticket);

        Comentario inactivo = TestDataFactory.crearComentario(ticket, usuario);
        inactivo.borrarComentario();
        comentarioRepository.save(inactivo);

        assertFalse(comentarioRepository.findByIdAndActivoTrue(inactivo.getId()).isPresent());
    }

    @Test
    void findByIdAndActivoTrue_WhenIdDoesNotExist_ShouldReturnEmptyOptionalComentario() {
        assertFalse(comentarioRepository.findByIdAndActivoTrue(-1L).isPresent());
    }



    @Test
    void testSoftDeleteSuccess_ShouldNotReturnSoftDeletedComentarioInActiveQuery() {
        Usuario usuario = TestDataFactory.crearUsuario();
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Ticket ticket = TestDataFactory.crearTicketActivo(usuario, proyecto);

        proyectosRepository.save(proyecto);
        usuariosRepository.save(usuario);
        ticketsRepository.save(ticket);

        Comentario activo = TestDataFactory.crearComentario(ticket, usuario);
        activo.borrarComentario();
        comentarioRepository.save(activo);

        assertFalse(activo.isActivo());
        assertTrue(comentarioRepository.findById(activo.getId()).isPresent());
        assertFalse(comentarioRepository.findByIdAndActivoTrue(activo.getId()).isPresent());
    }
}