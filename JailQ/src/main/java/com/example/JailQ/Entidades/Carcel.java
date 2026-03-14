package com.example.JailQ.Entidades;

import org.jspecify.annotations.Nullable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Carcel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private @Nullable Integer idCarcel;

    private String nombre;

    private String descripcion;

    private String localidad;

    private Integer capacidad;

    public Integer getIdCarcel() {
        return idCarcel;
    }

    public void setIdCarcel(Integer idCarcel) {
        this.idCarcel = idCarcel;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

}
