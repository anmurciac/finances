package com.thim.finances.controllers;

import com.thim.finances.dtos.CategoriaDTO;
import com.thim.finances.dtos.CategoriaRequest;
import com.thim.finances.model.entities.Categoria;
import com.thim.finances.model.entities.TipoCategoria;
import com.thim.finances.model.services.GestorCategorias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO: Revisar y agregar operaciones de borrado en general
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final GestorCategorias gestorCategorias;

    @Autowired
    public CategoriaController(GestorCategorias gestorCategorias) {
        this.gestorCategorias = gestorCategorias;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerTodasLasCategorias(Authentication authentication) {
        List<Categoria> categorias = gestorCategorias.obtenerTodasLasCategorias();
        List<CategoriaDTO> dtos = categorias.stream()
                .map(this::toCategoriaDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    //   @GetMapping
//   public ResponseEntity<List<CategoriaDTO>> obtenerCategoriasPorTipo(
//           @RequestParam(value = "type") String tipoCategoria,
//           Authentication authentication
//   ) {
//        return null;
//   }
    @PostMapping("/crear")
    public ResponseEntity<CategoriaDTO> crearCategoria(
            @RequestBody CategoriaRequest request,
            Authentication authentication
    ) {
        String usuarioId = authentication.getName();
        System.out.println("Creando categoria para usuario: " + usuarioId + ", nombre: " + request.getNombre());
        try {
            Categoria nuevaCategoria = gestorCategorias.agregarCategoriaParaUsuario(usuarioId, request.getNombre(), TipoCategoria.valueOf(request.getTipo()));
            return ResponseEntity.status(201).body(toCategoriaDTO(nuevaCategoria));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> editarCategoria(
            @PathVariable String id,
            @RequestBody CategoriaRequest request,
            Authentication authentication
    ) {
        ;
        try {
            Categoria categoria1 = gestorCategorias.editarCategoria(id, request.getNombre());
            return ResponseEntity.ok(toCategoriaDTO(categoria1));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(
            @PathVariable String id,
            Authentication authentication
    ) {
        String usuarioId = authentication.getName();
        try {
            gestorCategorias.eliminarCategoria(usuarioId, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    private CategoriaDTO toCategoriaDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNombre(), categoria.getTipo());
    }

}
