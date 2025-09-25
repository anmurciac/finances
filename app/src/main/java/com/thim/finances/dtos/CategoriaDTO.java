package com.thim.finances.dtos;

import com.thim.finances.model.entities.TipoCategoria;

public class CategoriaDTO {
    private String id;
    private String nombre;
    private TipoCategoria tipo;

    public CategoriaDTO(String id, String nombre, TipoCategoria tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoCategoria getTipo() {
        return tipo;
    }

    public void setTipo(TipoCategoria tipo) {
        this.tipo = tipo;
    }
}
