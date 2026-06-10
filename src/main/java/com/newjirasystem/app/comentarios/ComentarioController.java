package com.newjirasystem.app.comentarios;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {
    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {this.comentarioService = comentarioService;}

    @Tag(name = "Comentarios")
    @PatchMapping("/{id}")
    public ResponseEntity<ComentarioDTO> patchComentario(@PathVariable Long id, @Valid @RequestBody ActualizarComentarioDTO dto) {
        ComentarioDTO comentarioActualizado = comentarioService.patchComentario(id, dto);

        return new ResponseEntity<>(comentarioActualizado, HttpStatus.OK);
    }

    @Tag(name = "Comentarios")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComentarioById(@PathVariable Long id) {
        comentarioService.deleteComentario(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
