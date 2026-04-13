package com.example.JailQ.Dao;

import org.springframework.data.repository.CrudRepository;

import com.example.JailQ.Entidades.Cuenta;

/**
 * Interfaz DAO para la entidad {@link Cuenta}.
 */
public interface CuentaDAO extends CrudRepository<Cuenta, Integer> {


    boolean existsByUsername(String username);

}