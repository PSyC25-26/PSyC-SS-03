package com.example.JailQ.Entidades;

import org.jspecify.annotations.Nullable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

/**
 * Entidad que representa una cárcel dentro del sistema.
 * Contiene información básica como nombre, descripción,
 * localidad y capacidad máxima.
 */
@Entity
public class Carcel {
    /**
     * Identificador único de la cárcel.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private @Nullable Integer idCarcel;

    /**
     * Nombre de la cárcel.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Descripción o información adicional sobre la cárcel.
     */
    private String descripcion;

    /**
     * Localidad donde se encuentra la cárcel.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String localidad;

    /**
     * Capacidad máxima de reclusos que puede albergar.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private Integer capacidad;

    /**
     * Obtiene el identificador de la cárcel.
     * @return id de la cárcel
     */
    public Integer getIdCarcel() {
        return idCarcel;
    }
    
    /**
     * Establece el identificador de la cárcel.
     * @param idCarcel nuevo identificador
     */
    public void setIdCarcel(Integer idCarcel) {
        this.idCarcel = idCarcel;
    }

    /**
     * Obtiene el nombre de la cárcel.
     * @return nombre de la cárcel
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la cárcel.
     * @param nombre nombre de la cárcel
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción de la cárcel.
     * @return descripción
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la cárcel.
     * @param descripcion descripción de la cárcel
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la localidad de la cárcel.
     * @return localidad
     */
    public String getLocalidad() {
        return localidad;
    }

    /**
     * Establece la localidad de la cárcel.
     * @param localidad localidad de la cárcel
     */
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    /**
     * Obtiene la capacidad máxima de la cárcel.
     * @return capacidad máxima
     */
    public Integer getCapacidad() {
        return capacidad;
    }

    /**
     * Establece la capacidad máxima de la cárcel.
     * @param capacidad número máximo de reclusos
     */
    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

}
