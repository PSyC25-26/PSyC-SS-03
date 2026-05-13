package com.example.JailQ.Dao;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.JailQ.Entidades.Carcel;

/**
 * Interfaz DAO para la entidad {@link Carcel}.
 */
public interface CarcelDAO extends CrudRepository<Carcel, Integer> {
/**
     * Busca una cárcel por su nombre exacto.
     * Es vital para el proceso de traslado desde la GUI.
     */
    Optional<Carcel> findByNombre(String nombre);
}