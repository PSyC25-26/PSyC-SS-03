package com.example.JailQ.Facade;

import com.example.JailQ.Entidades.Preso;
import com.example.JailQ.Service.PresoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de gestionar las operaciones relacionadas con los presos.
 * 
 * Expone endpoints HTTP para:
 * <ul>
 *   <li>Crear presos</li>
 *   <li>Obtener todos los presos</li>
 *   <li>Obtener un preso por ID</li>
 *   <li>Eliminar un preso</li>
 * </ul>
 * 
 * Base URL: /preso
 */
@RestController
@RequestMapping("/preso")
public class PresoController {

    /** Servicio que contiene la lógica de negocio de presos */
    @Autowired
    private PresoService presoService;

    /**
     * Crea un nuevo preso en el sistema.
     *
     * Endpoint: POST /preso/crear
     *
     * @param preso objeto Preso recibido en formato JSON
     * @return ResponseEntity con:
     * <ul>
     *   <li>201 (CREATED) si se crea correctamente</li>
     *   <li>400 (BAD REQUEST) si los datos son inválidos</li>
     *   <li>500 (INTERNAL SERVER ERROR) si ocurre un error inesperado</li>
     * </ul>
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearPreso(@RequestBody Preso preso) {
        try {
            Preso guardado = presoService.anadirPreso(preso);
            return new ResponseEntity<>(guardado, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

        /**
     * Obtiene todos los presos registrados.
     *
     * Endpoint: GET /preso
     *
     * @return lista de presos con código 200 (OK)
     */
    @GetMapping
    public ResponseEntity<?> obtenerPresos() {
        try {
            return new ResponseEntity<>(presoService.obtenerTodos(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene un preso por su ID.
     *
     * Endpoint: GET /preso/{id}
     *
     * @param id identificador del preso
     * @return el preso encontrado o error correspondiente
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPresoPorId(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(presoService.obtenerPorId(id), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina un preso por su ID.
     *
     * Endpoint: DELETE /preso/eliminar/{id}
     *
     * @param id identificador del preso
     * @return mensaje de éxito o error
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarPreso(@PathVariable Integer id) {
        try {
            boolean eliminado = presoService.eliminar(id);

            if (eliminado) {
                return new ResponseEntity<>("Preso eliminado correctamente.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No se encontró el preso con ID: " + id, HttpStatus.NOT_FOUND);
            }

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}