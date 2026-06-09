package com.newjirasystem.app.comentarios;

import com.newjirasystem.app.tickets.Ticket;
import com.newjirasystem.app.tickets.TicketsRepository;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final TicketsRepository ticketsRepository;
    private final UsuariosRepository usuariosRepository;
    private final ComentarioMapper comentarioMapper;


    public ComentarioService(ComentarioRepository comentarioRepository, TicketsRepository ticketsRepository, UsuariosRepository usuariosRepository, ComentarioMapper comentarioMapper) {
        this.comentarioRepository = comentarioRepository;
        this.ticketsRepository = ticketsRepository;
        this.usuariosRepository = usuariosRepository;
        this.comentarioMapper = comentarioMapper;
    }

    @Transactional
    public ComentarioDTO postComentario(Long idTicket, CrearComentarioDTO dto) {

        String texto = dto.texto();

        Usuario usuarioAutor = this.usuariosRepository.findById(dto.idAutor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no existe"));

        Ticket ticketComentario = this.ticketsRepository.findById(idTicket)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ticket no existe"));

        Comentario comentario = new Comentario(texto, ticketComentario, usuarioAutor);

        this.comentarioRepository.save(comentario);

        return this.comentarioMapper.toComentarioDTO(comentario);
    }

    public List<ComentarioDTO> getComenatariosByTicketId(Long id) {
        List<Comentario> listaComentarios = this.comentarioRepository.findByTicketIdAndActivoTrue(id);

        return listaComentarios.stream()
                .map(this.comentarioMapper::toComentarioDTO)
                .toList();
    }

    @Transactional
    public ComentarioDTO patchComentario(Long id, ActualizarComentarioDTO dto) {
        Comentario comentario = this.comentarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado"));

        String nuevoTexto = dto.texto();

        comentario.editarTexto(nuevoTexto);

        return this.comentarioMapper.toComentarioDTO(comentario);
    }

    @Transactional
    public void deleteComentario(Long id) {
        Comentario comentario = this.comentarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El comentario no existe"));

        comentario.eliminarLogicamente();
    }
}
