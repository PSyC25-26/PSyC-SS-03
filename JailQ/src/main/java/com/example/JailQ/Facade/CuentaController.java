package com.example.JailQ.Facade;

import com.example.JailQ.Entidades.Cuenta;
import com.example.JailQ.Service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador REST encargado de gestionar las operaciones relacionadas con las cuentas.
 * * Expone endpoints HTTP para:
 * <ul>
 * <li>Crear cuentas</li>
 * <li>Eliminar cuentas</li>
 * <li>Eliminar cuentas de tipo POLICIA</li>
 * <li>Obtener listado de cuentas POLICIA</li>
 * </ul>
 * * Actúa como capa intermedia entre el cliente (Postman / GUI)
 * y la lógica de negocio (CuentaService).
 * * Base URL: /cuentas
 * * */
@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    /** Logger para registrar eventos y errores en el controlador */
    private static final Logger logger = LoggerFactory.getLogger(CuentaController.class);

    /** Servicio que contiene la lógica de negocio de cuentas */
    @Autowired
    private CuentaService cuentaService;

    /**
     * Crea una nueva cuenta en el sistema.
     *
     * Endpoint: POST /cuentas/crear
     *
     * @param cuenta Objeto cuenta recibido en formato JSON
     * @return ResponseEntity con:
     * <ul>
     * <li>201 (CREATED) si la cuenta se crea correctamente</li>
     * <li>400 (BAD REQUEST) si los datos son inválidos</li>
     * <li>500 (INTERNAL SERVER ERROR) si ocurre un error inesperado</li>
     * </ul>
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearCuenta(@RequestBody Cuenta cuenta) {
        logger.info("Recibida petición POST en /cuentas/crear para la cuenta: {}", cuenta.getUsername());
        try {
            Cuenta cuentaGuardada = cuentaService.anadirCuenta(cuenta);
            return new ResponseEntity<>(cuentaGuardada, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Petición rechazada al crear cuenta (400 Bad Request): {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error interno (500) al crear la cuenta: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina una cuenta por su ID.
     *
     * Endpoint: DELETE /cuentas/eliminar/{id}
     *
     * @param id Identificador de la cuenta
     * @return ResponseEntity con:
     * <ul>
     * <li>200 (OK) si se elimina correctamente</li>
     * <li>404 (NOT FOUND) si no existe la cuenta</li>
     * <li>400 (BAD REQUEST) si el ID es inválido</li>
     * <li>500 (INTERNAL SERVER ERROR) si ocurre un error</li>
     * </ul>
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable Integer id) {
        logger.info("Recibida petición DELETE en /cuentas/eliminar/{}", id);
        try {
            boolean eliminado = cuentaService.eliminarCuenta(id);

            if (eliminado) {
                return new ResponseEntity<>("Cuenta eliminada correctamente.", HttpStatus.OK);
            } else {
                logger.warn("No se pudo eliminar, cuenta no encontrada con ID: {}", id);
                return new ResponseEntity<>("No se encontró la cuenta con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Petición rechazada al eliminar cuenta (400 Bad Request): {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error interno (500) al eliminar la cuenta con ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina una cuenta únicamente si es de tipo POLICIA.
     *
     * Endpoint: DELETE /cuentas/eliminar/policia/{id}
     *
     * @param id Identificador de la cuenta
     * @return ResponseEntity con:
     * <ul>
     * <li>200 (OK) si se elimina correctamente</li>
     * <li>404 (NOT FOUND) si no existe</li>
     * <li>400 (BAD REQUEST) si no es policía o ID inválido</li>
     * </ul>
     */
    @DeleteMapping("/eliminar/policia/{id}")
    public ResponseEntity<?> eliminarCuentaPolicia(@PathVariable Integer id) {
        logger.info("Recibida petición DELETE en /cuentas/eliminar/policia/{}", id);
        try {
            boolean eliminado = cuentaService.eliminarCuentaPolicia(id);

            if (eliminado) {
                return new ResponseEntity<>("Cuenta de policía eliminada correctamente.", HttpStatus.OK);
            } else {
                logger.warn("No se pudo eliminar, cuenta de policía no encontrada con ID: {}", id);
                return new ResponseEntity<>("No se encontró ninguna cuenta de policía con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Petición rechazada al eliminar cuenta de policía (400 Bad Request): {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error interno (500) al eliminar la cuenta de policía con ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene todas las cuentas de tipo POLICIA.
     *
     * Endpoint: GET /cuentas/policias
     *
     * @return Lista de cuentas de tipo POLICIA
     * con código 200 (OK)
     */
    @GetMapping("/policias")
    public ResponseEntity<?> obtenerCuentasPolicia() {
        logger.info("Recibida petición GET en /cuentas/policias");
        try {
            return new ResponseEntity<>(cuentaService.obtenerCuentasPolicia(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error interno (500) al obtener la lista de policías: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Autenticación de policía mediante username y password.
     *
     * Endpoint: POST /cuentas/login/policia
     */
    @PostMapping("/login/policia")
    public ResponseEntity<?> loginPolicia(@RequestBody Cuenta cuenta) {
        logger.info("Recibida petición POST en /cuentas/login/policia para el usuario: {}", cuenta.getUsername());
        try {
            Cuenta resultado = cuentaService.loginPolicia(
                    cuenta.getUsername(),
                    cuenta.getPassword()
            );

            if (resultado != null) {
                return new ResponseEntity<>(resultado, HttpStatus.OK);
            } else {
                logger.warn("Intento de login fallido (401 Unauthorized) para el usuario: {}", cuenta.getUsername());
                return new ResponseEntity<>("Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            logger.error("Error interno (500) durante el login del usuario {}: {}", cuenta.getUsername(), e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}