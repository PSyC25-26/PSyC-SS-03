package com.example.JailQ.Facade;

import com.example.JailQ.Entidades.Carcel;
import com.example.JailQ.Service.CarcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de gestionar las operaciones relacionadas con las cárceles.
 * 
 * Expone endpoints HTTP para:
 * <ul>
 *   <li>Crear cárceles</li>
 *   <li>Eliminar cárceles</li>
 *   <li>Obtener todas las cárceles</li>
 *   <li>Obtener una cárcel por ID</li>
 * </ul>
 * 
 * Actúa como capa intermedia entre el cliente (Postman / GUI)
 * y la lógica de negocio (CarcelService).
 * 
 * Base URL: /carcel
 */
@RestController
@RequestMapping("/carcel")
public class CarcelController {

    /** Servicio que contiene la lógica de negocio de cárceles */
    @Autowired
    private CarcelService carcelService;

    /**
     * Crea una nueva cárcel en el sistema.
     *
     * Endpoint: POST /carcel/crear
     *
     * @param carcel Objeto cárcel recibido en formato JSON
     * @return ResponseEntity con:
     * <ul>
     *   <li>201 (CREATED) si se crea correctamente</li>
     *   <li>400 (BAD REQUEST) si los datos son inválidos</li>
     *   <li>500 (INTERNAL SERVER ERROR) si ocurre un error inesperado</li>
     * </ul>
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearCarcel(@RequestBody Carcel carcel) {
        try {
            Carcel guardada = carcelService.anadirCarcel(carcel);
            return new ResponseEntity<>(guardada, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina una cárcel por su ID.
     *
     * Endpoint: DELETE /carcel/eliminar/{id}
     *
     * @param id Identificador de la cárcel
     * @return ResponseEntity con:
     * <ul>
     *   <li>200 (OK) si se elimina correctamente</li>
     *   <li>404 (NOT FOUND) si no existe</li>
     *   <li>400 (BAD REQUEST) si el ID es inválido</li>
     * </ul>
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarCarcel(@PathVariable Integer id) {
        try {
            boolean eliminado = carcelService.eliminarCarcel(id);

            if (eliminado) {
                return new ResponseEntity<>("Cárcel eliminada correctamente.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No se encontró la cárcel con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene todas las cárceles registradas.
     *
     * Endpoint: GET /carcel
     *
     * @return Lista de cárceles con código 200 (OK)
     */
    @GetMapping
    public ResponseEntity<?> obtenerCarceles() {
        try {
            return new ResponseEntity<>(carcelService.obtenerCarceles(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene una cárcel por su ID.
     *
     * Endpoint: GET /carcel/{id}
     *
     * @param id identificador de la cárcel
     * @return la cárcel encontrada o error correspondiente
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCarcelPorId(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(carcelService.obtenerCarcelPorId(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
