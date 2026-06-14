package com.newjirasystem.app.comentarios;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findAllByTicketIdAndActivoTrue(Long id);

    List<Comentario> findAllByAutorIdAndActivoTrue(Long idAutor);

    Optional<Comentario> findByIdAndActivoTrue(Long id);

}
