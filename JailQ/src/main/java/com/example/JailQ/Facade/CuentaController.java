package com.example.JailQ.Facade;

import com.example.JailQ.Entidades.Cuenta;
import com.example.JailQ.Service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de gestionar las operaciones relacionadas con las cuentas.
 * 
 * Expone endpoints HTTP para:
 * <ul>
 *   <li>Crear cuentas</li>
 *   <li>Eliminar cuentas</li>
 *   <li>Eliminar cuentas de tipo POLICIA</li>
 *   <li>Obtener listado de cuentas POLICIA</li>
 * </ul>
 * 
 * Actúa como capa intermedia entre el cliente (Postman / GUI)
 * y la lógica de negocio (CuentaService).
 * 
 * Base URL: /cuentas
 * 
 * 
 */
@RestController
@RequestMapping("/cuentas")
public class CuentaController {

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
     *   <li>201 (CREATED) si la cuenta se crea correctamente</li>
     *   <li>400 (BAD REQUEST) si los datos son inválidos</li>
     *   <li>500 (INTERNAL SERVER ERROR) si ocurre un error inesperado</li>
     * </ul>
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearCuenta(@RequestBody Cuenta cuenta) {
        try {
            Cuenta cuentaGuardada = cuentaService.anadirCuenta(cuenta);
            return new ResponseEntity<>(cuentaGuardada, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
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
     *   <li>200 (OK) si se elimina correctamente</li>
     *   <li>404 (NOT FOUND) si no existe la cuenta</li>
     *   <li>400 (BAD REQUEST) si el ID es inválido</li>
     *   <li>500 (INTERNAL SERVER ERROR) si ocurre un error</li>
     * </ul>
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable Integer id) {
        try {
            boolean eliminado = cuentaService.eliminarCuenta(id);

            if (eliminado) {
                return new ResponseEntity<>("Cuenta eliminada correctamente.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No se encontró la cuenta con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
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
     *   <li>200 (OK) si se elimina correctamente</li>
     *   <li>404 (NOT FOUND) si no existe</li>
     *   <li>400 (BAD REQUEST) si no es policía o ID inválido</li>
     * </ul>
     */
    @DeleteMapping("/eliminar/policia/{id}")
    public ResponseEntity<?> eliminarCuentaPolicia(@PathVariable Integer id) {
        try {
            boolean eliminado = cuentaService.eliminarCuentaPolicia(id);

            if (eliminado) {
                return new ResponseEntity<>("Cuenta de policía eliminada correctamente.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No se encontró ninguna cuenta de policía con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
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
        try {
            return new ResponseEntity<>(cuentaService.obtenerCuentasPolicia(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}