package com.example.JailQ.Service;

import com.example.JailQ.Dao.CuentaDAO;
import com.example.JailQ.Entidades.Cuenta;
import com.example.JailQ.Entidades.TipoCuenta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de la lógica de negocio relacionada con las cuentas.
 * 
 * Proporciona métodos para:
 * <ul>
 * <li>Crear nuevas cuentas</li>
 * <li>Eliminar cuentas</li>
 * <li>Eliminar cuentas de tipo POLICIA</li>
 * <li>Obtener todas las cuentas de tipo POLICIA</li>
 * <li>Iniciar sesión como POLICIA</li>
 * </ul>
 * 
 * Este servicio actúa como intermediario entre el controlador (Controller)
 * y el acceso a datos (DAO).
 */
@Service
public class CuentaService {

    /**
     * DAO utilizado para acceder a la base de datos de cuentas.
     * 
     * <p>
     * Este campo se declara como <code>private final</code> y se inicializa
     * mediante inyección de dependencias a través del constructor.
     * No debe ser modificado directamente fuera de esta clase.
     * </p>
     */
    private final CuentaDAO cuentaDAO;

    /**
     * Constructor de {@link CuentaService} que permite inyectar el DAO de cuentas.
     *
     * <p>
     * Se utiliza inyección de dependencias mediante {@link Autowired}, lo que
     * permite que Spring proporcione automáticamente la implementación de
     * {@link CuentaDAO}.
     * </p>
     *
     * @param cuentaDAO DAO utilizado para persistir y consultar entidades
     *                  {@link Cuenta}
     */
    @Autowired
    public CuentaService(CuentaDAO cuentaDAO) {
        this.cuentaDAO = cuentaDAO;
    }

    /**
     * Añade una nueva cuenta tras validar los campos obligatorios y comprobar
     * que no exista ya otra cuenta con el mismo username.
     *
     * @param nuevaCuenta Objeto Cuenta a guardar
     * @return La cuenta guardada con su ID generado
     * @throws IllegalArgumentException si los datos son inválidos o si el username
     *                                  ya está en uso
     */
    public Cuenta anadirCuenta(Cuenta nuevaCuenta) {
        if (nuevaCuenta == null) {
            throw new IllegalArgumentException("No se ha recibido ningún dato de la cuenta.");
        }

        if (nuevaCuenta.getUsername() == null || nuevaCuenta.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario (username) es obligatorio.");
        }

        if (nuevaCuenta.getPassword() == null || nuevaCuenta.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        if (nuevaCuenta.getTipoCuenta() == null) {
            throw new IllegalArgumentException("El tipo de cuenta es obligatorio.");
        }

        String usernameLimpio = nuevaCuenta.getUsername().trim();
        nuevaCuenta.setUsername(usernameLimpio);

        if (cuentaDAO.existsByUsername(usernameLimpio)) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese nombre de usuario.");
        }

        System.out.println("Cuenta validada correctamente. Procediendo a guardar...");
        return cuentaDAO.save(nuevaCuenta);
    }

    /**
     * Elimina una cuenta de la base de datos según su ID.
     *
     * @param id El identificador de la cuenta a eliminar
     * @return true si se eliminó correctamente, false si no se encontró
     * @throws IllegalArgumentException si el ID es nulo
     */
    public boolean eliminarCuenta(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la cuenta no puede ser nulo.");
        }

        if (cuentaDAO.existsById(id)) {
            cuentaDAO.deleteById(id);
            System.out.println("Cuenta con ID " + id + " eliminada correctamente.");
            return true;
        } else {
            System.err.println("No se encontró ninguna cuenta con el ID: " + id);
            return false;
        }
    }

    /**
     * Elimina una cuenta únicamente si pertenece al tipo POLICIA.
     *
     * @param id Identificador de la cuenta
     * @return true si la cuenta fue eliminada correctamente
     * @throws IllegalArgumentException si:
     *                                  <ul>
     *                                  <li>El ID es nulo</li>
     *                                  <li>La cuenta no existe</li>
     *                                  <li>La cuenta no es de tipo POLICIA</li>
     *                                  </ul>
     */
    public boolean eliminarCuentaPolicia(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la cuenta no puede ser nulo.");
        }

        Cuenta cuenta = cuentaDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ninguna cuenta con ese ID."));

        if (cuenta.getTipoCuenta() != TipoCuenta.POLICIA) {
            throw new IllegalArgumentException("La cuenta indicada no pertenece a un policía.");
        }

        cuentaDAO.deleteById(id);
        System.out.println("Cuenta de policía con ID " + id + " eliminada correctamente.");
        return true;
    }

    /**
     * Obtiene todas las cuentas de tipo POLICIA almacenadas en la base de datos.
     *
     * @return Lista de cuentas de tipo POLICIA
     */
    public List<Cuenta> obtenerCuentasPolicia() {
        List<Cuenta> policias = new ArrayList<>();

        for (Cuenta cuenta : cuentaDAO.findAll()) {
            if (cuenta.getTipoCuenta() == TipoCuenta.POLICIA) {
                policias.add(cuenta);
            }
        }

        return policias;
    }

    /**
     * Verifica si existe una cuenta de tipo POLICIA con username y password
     * correctos.
     *
     * @param username nombre de usuario
     * @param password contraseña
     * @return la cuenta si es válida, null si no
     */
    public Cuenta loginPolicia(String username, String password) {

        for (Cuenta cuenta : cuentaDAO.findAll()) {
            if (cuenta.getUsername().equals(username)
                    && cuenta.getPassword().equals(password)
                    && cuenta.getTipoCuenta() == TipoCuenta.POLICIA) {

                return cuenta;
            }
        }

        return null;
    }
}