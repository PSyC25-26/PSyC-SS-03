package com.example.JailQ.Service;

import com.example.JailQ.Dao.CuentaDAO;
import com.example.JailQ.Entidades.Cuenta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentaService {

    @Autowired
    private CuentaDAO cuentaDAO;

    /**
     * Añade una nueva cuenta tras validar los campos obligatorios.
     * * @param nuevaCuenta Objeto Cuenta a guardar
     * @return La cuenta guardada con su ID generado
     */
    public Cuenta anadirCuenta(Cuenta nuevaCuenta) {
        if (nuevaCuenta == null) {
            throw new IllegalArgumentException("No se ha recibido ningún dato de la cuenta.");
        }

        // Validaciones básicas
        if (nuevaCuenta.getUsername() == null || nuevaCuenta.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario (username) es obligatorio.");
        }
        if (nuevaCuenta.getPassword() == null || nuevaCuenta.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (nuevaCuenta.getTipoCuenta() == null) {
            throw new IllegalArgumentException("El tipo de cuenta es obligatorio.");
        }

        System.out.println("Cuenta validada correctamente. Procediendo a guardar...");
        return cuentaDAO.save(nuevaCuenta);
    }


    /**
     * Elimina una cuenta de la base de datos según su ID.
     * @param id El identificador de la cuenta a eliminar.
     * @return true si se eliminó correctamente, false si no se encontró.
     */
    public boolean eliminarCuenta(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la cuenta no puede ser nulo.");
        }

        // Comprobamos si la cuenta existe en la base de datos antes de intentar borrarla
        if (cuentaDAO.existsById(id)) {
            cuentaDAO.deleteById(id);
            System.out.println("Cuenta con ID " + id + " eliminada correctamente.");
            return true;
        } else {
            System.err.println("No se encontró ninguna cuenta con el ID: " + id);
            return false;
        }
    }
}