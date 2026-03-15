package com.example.JailQ.Dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import com.example.JailQ.Entidades.Preso;

/**
 * Interfaz DAO para la entidad {@link Preso}.
 */
public interface PresoDAO extends CrudRepository<Preso, Integer> {


}