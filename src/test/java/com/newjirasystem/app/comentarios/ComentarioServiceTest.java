package com.newjirasystem.app.comentarios;

import com.newjirasystem.app.dataFactory.TestDataFactory;
import com.newjirasystem.app.exception.ComentarioNoEncontradoException;
import com.newjirasystem.app.exception.TicketNoEncontradoException;
import com.newjirasystem.app.exception.UsuarioNoEncontradoException;
import com.newjirasystem.app.tickets.Ticket;
import com.newjirasystem.app.tickets.TicketsRepository;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {
    @InjectMocks
    private ComentarioService comentarioService;

    @Spy
    private ComentarioMapper comentarioMapper;

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private TicketsRepository ticketsRepository;

    @Mock
    private UsuariosRepository usuariosRepository;



    @Test
    void postComentarioWhenUsuarioAndTicketExist_ShouldReturnComentarioDTOWithMatchingFields() {
        // arrange
        Usuario usuario = TestDataFactory.crearUsuario();
        ReflectionTestUtils.setField(usuario, "id", 1L);
        Ticket ticket = TestDataFactory.crearTicket();
        ReflectionTestUtils.setField(ticket, "id", 1L);
        CrearComentarioDTO crearDto = new CrearComentarioDTO("comentario test", 1L);

        when(usuariosRepository.findById(crearDto.idAutor())).thenReturn(Optional.of(usuario));
        when(ticketsRepository.findByIdAndActivoTrue(ticket.getId())).thenReturn(Optional.of(ticket));

        // act
        ComentarioDTO resultado = comentarioService.postComentario(ticket.getId(), crearDto);

        // assert
        assertNotNull(resultado);
        assertEquals(crearDto.texto(), resultado.texto());
        assertEquals(crearDto.idAutor(), usuario.getId());
        assertEquals(ticket.getId(), resultado.ticket());
    }

    @Test
    void postComentarioWhenUserDoesNotExist_ShouldThrowUsuarioNoEncontradoException() {
        CrearComentarioDTO crearDto = new CrearComentarioDTO("comentario test", 1L);

        when(usuariosRepository.findById(crearDto.idAutor())).thenReturn(Optional.empty());


        assertThrows(UsuarioNoEncontradoException.class, () -> comentarioService.postComentario(1L, crearDto));
        verify(ticketsRepository, never()).findByIdAndActivoTrue(anyLong());
    }

    @Test
    void postComentarioWhenUserExistsButTicketDoesNotExist_ShouldThrowTicketNoEncontradoException() {
        CrearComentarioDTO crearDto = new CrearComentarioDTO("comentario test", 1L);
        Usuario usuario = TestDataFactory.crearUsuario();
        ReflectionTestUtils.setField(usuario, "id", 1L);
        Ticket ticket = TestDataFactory.crearTicket();
        ReflectionTestUtils.setField(ticket, "id", 1L);

        when(usuariosRepository.findById(crearDto.idAutor())).thenReturn(Optional.of(usuario));
        when(ticketsRepository.findByIdAndActivoTrue(ticket.getId())).thenReturn(Optional.empty());

        assertThrows(TicketNoEncontradoException.class, () -> comentarioService.postComentario(ticket.getId(), crearDto));
    }



    @Test
    void getComentariosByTicketIdWhenTicketExists_ShouldReturnMappedComentarios() {
        Usuario usuario = TestDataFactory.crearUsuario();
        Ticket ticket = TestDataFactory.crearTicket();
        ReflectionTestUtils.setField(ticket, "id", 1L);

        Comentario comentario1 = TestDataFactory.crearComentario(ticket, usuario);
        Comentario comentario2 = TestDataFactory.crearComentario(ticket, usuario);

        when(comentarioRepository.findAllByTicketIdAndActivoTrue(ticket.getId())).thenReturn(List.of(comentario1, comentario2));

        List<ComentarioDTO> resultado = comentarioService.getComentariosByTicketId(ticket.getId());

        assertEquals(2, resultado.size());
        assertEquals(comentario1.getTexto(), resultado.getFirst().texto());
        assertEquals(comentario2.getTicket().getId(), resultado.get(1).ticket());
        assertEquals(comentario1.getAutor().getNombre(), resultado.getFirst().nombreAutor());

    }

    @Test
    void getComentariosByTicketIdWhenTicketDoesExistButHasNoComentarios() {
        when(ticketsRepository.existsById(1L)).thenReturn(true);
        when(comentarioRepository.findAllByTicketIdAndActivoTrue(1L)).thenReturn(List.of());

        List<ComentarioDTO> resultado = comentarioService.getComentariosByTicketId(1L);

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    @Test
    void getComentariosByTicketIdWhenTicketDoesNotExist_ShouldThrowTicketNoEncontradoException() {
        when(ticketsRepository.existsById(1L)).thenReturn(false);

        assertThrows(TicketNoEncontradoException.class, () -> comentarioService.getComentariosByTicketId(1L));
    }



    @Test
    void patchComentarioSuccessfully_ShouldReturnComentarioDTOWithUpdatedTexto() {
        Usuario usuario = TestDataFactory.crearUsuario();
        Ticket ticket = TestDataFactory.crearTicket();
        Comentario comentario = TestDataFactory.crearComentario(ticket, usuario);
        ReflectionTestUtils.setField(comentario, "id", 1L);

        ActualizarComentarioDTO actuDto = new ActualizarComentarioDTO("nuevo texto");

        when(comentarioRepository.findByIdAndActivoTrue(comentario.getId())).thenReturn(Optional.of(comentario));

        ComentarioDTO resultado = comentarioService.patchComentario(comentario.getId(), actuDto);

        assertEquals(actuDto.texto(), resultado.texto());
    }

    @Test
    void patchComentarioWhenComentarioDoesNotExist_ShouldThrowComentarioNoEncontradoException() {
        ActualizarComentarioDTO actuDto = new ActualizarComentarioDTO("nuevo texto");

        when(comentarioRepository.findByIdAndActivoTrue(anyLong())).thenReturn(Optional.empty());

        assertThrows(ComentarioNoEncontradoException.class, () -> comentarioService.patchComentario(1L, actuDto));
    }



    @Test
    void deleteComentarioSuccessfully_ShouldChangeActivoToFalse() {
        Usuario usuario = TestDataFactory.crearUsuario();
        Ticket ticket = TestDataFactory.crearTicket();
        Comentario comentario = TestDataFactory.crearComentario(ticket, usuario);
        ReflectionTestUtils.setField(comentario, "id", 1L);

        when(comentarioRepository.findByIdAndActivoTrue(comentario.getId())).thenReturn(Optional.of(comentario));

        comentarioService.deleteComentario(comentario.getId());

        assertFalse(comentario.isActivo());

    }

    @Test
    void deleteComentarioWhenComentarioDoesNotExist_ShouldThrowComentarioNoEncontradoException() {
        when(comentarioRepository.findByIdAndActivoTrue(anyLong())).thenReturn(Optional.empty());

        assertThrows(ComentarioNoEncontradoException.class, () -> comentarioService.deleteComentario(1L));
    }
}