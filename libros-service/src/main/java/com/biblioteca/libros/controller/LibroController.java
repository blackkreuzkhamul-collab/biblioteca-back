package com.biblioteca.libros.controller;

import com.biblioteca.libros.model.Libro;
import com.biblioteca.libros.service.LibroService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    /*@GetMapping
    public List<Libro> obtenerCatalogo() throws Exception{
        return libroService.obtenerCatalogo();
    }*/

    @GetMapping("/{id}")
    public Libro obtenerLibro(@PathVariable String id) throws Exception{
        return libroService.obtenerLibroPorId(id);
    }

    @GetMapping("/search")
    public List<Libro> buscar(@RequestParam String q) throws Exception {
       return libroService.buscarLibros(q);
    }

    @GetMapping("/suggest")
    public List<Libro> sugerir(@RequestParam String q) throws Exception {
       return libroService.sugerirLibros(q);
    }

    @GetMapping("/categorias")
    public List<String> obtenerCategorias() throws Exception {
        return libroService.obtenerCategorias();
    }

    @GetMapping
    public List<Libro> obtenerLibros(@RequestParam(required = false) String categoria) throws Exception {
        if (categoria != null) {
            return libroService.librosPorCategoria(categoria);
        }
        return libroService.obtenerCatalogo();
    }

}