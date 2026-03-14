package com.biblioteca.libros.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.biblioteca.libros.model.Libro;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LibroService {

    private final ElasticsearchClient client;

    public LibroService(ElasticsearchClient client) {
        this.client = client;
    }

    public List<Libro> obtenerCatalogo() throws IOException {

        SearchResponse<Libro> response = client.search(s -> s
                        .index("books")
                        .size(100)
                        .query(q -> q.matchAll(m -> m)),
                Libro.class
        );

        List<Libro> libros = new ArrayList<>();

        response.hits().hits().forEach(hit -> libros.add(hit.source()));

        return libros;
    }

    public Libro obtenerLibroPorId(String id) throws IOException {

        SearchResponse<Libro> response = client.search(s -> s
                        .index("books")
                        .query(q -> q.term(t -> t.field("id").value(id))),
                Libro.class
        );

        if (response.hits().hits().isEmpty()) {
            return null;
        }

        return response.hits().hits().get(0).source();
    }

    public List<Libro> buscarLibros(String texto) throws IOException {

        SearchResponse<Libro> response = client.search(s -> s
                        .index("books")
                        .query(q -> q
                                .multiMatch(m -> m
                                        .fields("titulo", "autor")
                                        .query(texto)
                                )
                        ),
                Libro.class
        );

        List<Libro> libros = new ArrayList<>();

        response.hits().hits().forEach(hit -> libros.add(hit.source()));

        return libros;
    }

    public List<Libro> sugerirLibros(String texto) throws IOException {

        SearchResponse<Libro> response = client.search(s -> s
                        .index("books")
                        .query(q -> q
                                .multiMatch(m -> m
                                        .query(texto)
                                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BoolPrefix)
                                        .fields(
                                                "titulo",
                                                "titulo._2gram",
                                                "titulo._3gram"
                                        )
                                )
                        ),
                Libro.class
        );

        List<Libro> libros = new ArrayList<>();

        response.hits().hits().forEach(hit -> libros.add(hit.source()));

        return libros;
    }

public List<Libro> librosPorCategoria(String categoria) throws IOException {

    SearchResponse<Libro> response = client.search(s -> s
                    .index("books")
                    .query(q -> q
                            .term(t -> t
                                    .field("categoria")
                                    .value(categoria)
                            )
                    ),
            Libro.class
    );

    List<Libro> libros = new ArrayList<>();

    response.hits().hits().forEach(hit -> libros.add(hit.source()));

    return libros;
}

public List<String> obtenerCategorias() throws IOException {

    var response = client.search(s -> s
                    .index("books")
                    .size(0)
                    .aggregations("categorias", a -> a
                            .terms(t -> t
                                    .field("categoria")
                            )
                    ),
            Object.class
    );

    var buckets = response.aggregations()
            .get("categorias")
            .sterms()
            .buckets()
            .array();

    List<String> categorias = new ArrayList<>();

    buckets.forEach(bucket -> categorias.add(bucket.key().stringValue()));

    return categorias;
}
}