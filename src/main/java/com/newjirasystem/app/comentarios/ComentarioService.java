package com.newjirasystem.app.comentarios;

import com.newjirasystem.app.exception.ComentarioNoEncontradoException;
import com.newjirasystem.app.exception.TicketNoEncontradoException;
import com.newjirasystem.app.exception.UsuarioNoEncontradoException;
import com.newjirasystem.app.tickets.Ticket;
import com.newjirasystem.app.tickets.TicketsRepository;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new UsuarioNoEncontradoException(dto.idAutor()));

        Ticket ticketComentario = this.ticketsRepository.findByIdAndActivoTrue(idTicket)
                .orElseThrow(() -> new TicketNoEncontradoException(idTicket));

        Comentario comentario = new Comentario(texto, ticketComentario, usuarioAutor);

        this.comentarioRepository.save(comentario);

        return this.comentarioMapper.toComentarioDTO(comentario);
    }

    public List<ComentarioDTO> getComentariosByTicketId(Long id) {

        if (!this.ticketsRepository.existsById(id)) {
            throw new TicketNoEncontradoException(id);
        }

        List<Comentario> listaComentarios = this.comentarioRepository.findAllByTicketIdAndActivoTrue(id);

        return listaComentarios.stream()
                .map(this.comentarioMapper::toComentarioDTO)
                .toList();
    }

    @Transactional
    public ComentarioDTO patchComentario(Long id, ActualizarComentarioDTO dto) {
        Comentario comentario = this.comentarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ComentarioNoEncontradoException(id));

        if (comentario.getTexto().equals(dto.texto())) {
            return this.comentarioMapper.toComentarioDTO(comentario);
        }

        String nuevoTexto = dto.texto();

        comentario.editarTexto(nuevoTexto);

        return this.comentarioMapper.toComentarioDTO(comentario);
    }

    @Transactional
    public void deleteComentario(Long id) {
        Comentario comentario = this.comentarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ComentarioNoEncontradoException(id));

        comentario.borrarComentario();
    }
}
