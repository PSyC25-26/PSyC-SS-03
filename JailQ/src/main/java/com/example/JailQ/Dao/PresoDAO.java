package com.example.JailQ.Dao;

import org.springframework.data.repository.CrudRepository;
import com.example.JailQ.Entidades.Preso;

/**
 * Interfaz DAO para la entidad {@link Preso}.
 */
public interface PresoDAO extends CrudRepository<Preso, Integer> {

}
