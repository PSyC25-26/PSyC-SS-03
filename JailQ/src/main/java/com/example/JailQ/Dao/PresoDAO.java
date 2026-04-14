package com.example.JailQ.Dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import com.example.JailQ.Entidades.Carcel;
import com.example.JailQ.Entidades.Preso;

/**
 * Interfaz DAO para la entidad {@link Preso}.
 */
public interface PresoDAO extends CrudRepository<Preso, Integer> {
    /**
     * Cuenta automáticamente la cantidad de presos asociados a una cárcel específica.
     */
    long countByCarcel(Carcel carcel);

}