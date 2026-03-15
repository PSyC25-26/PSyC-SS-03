package com.example.JailQ.Dao;

import org.springframework.data.repository.CrudRepository;

import com.example.JailQ.Entidades.Carcel;

/**
 * Interfaz DAO para la entidad {@link Carcel}.
 */
public interface CarcelDAO extends CrudRepository<Carcel, Integer> {

}