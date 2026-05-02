package com.example.JailQ.Facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JailQ.Entidades.Preso;
import com.example.JailQ.Service.PresoService;

/**
 * Controlador REST encargado de gestionar las operaciones relacionadas con los presos.
 * * Expone endpoints HTTP para:
 * <ul>
 * <li>Crear presos</li>
 * <li>Obtener todos los presos</li>
 * <li>Obtener un preso por ID</li>
 * <li>Eliminar un preso</li>
 * </ul>
 * * Base URL: /preso
 */
@RestController
@RequestMapping("/preso")
public class PresoController {

    /** Logger para registrar eventos y errores en el controlador */
    private static final Logger logger = LoggerFactory.getLogger(PresoController.class);

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
     * <li>201 (CREATED) si se crea correctamente</li>
     * <li>400 (BAD REQUEST) si los datos son inválidos</li>
     * <li>500 (INTERNAL SERVER ERROR) si ocurre un error inesperado</li>
     * </ul>
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearPreso(@RequestBody Preso preso) {
        logger.info("Recibida petición POST en /preso/crear para el preso: {}", preso.getNombre());
        try {
            Preso guardado = presoService.anadirPreso(preso);
            return new ResponseEntity<>(guardado, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            logger.warn("Petición rechazada al crear preso (400 Bad Request): {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            logger.error("Error interno (500) al crear el preso: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene todos los presos registrados.
     *
     * Endpoint: GET /preso/todos
     *
     * @return lista de presos con código 200 (OK)
     */
    @GetMapping("/todos")
    public ResponseEntity<?> obtenerPresos() {
        logger.info("Recibida petición GET en /preso/todos");
        try {
            return new ResponseEntity<>(presoService.obtenerTodos(), HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error interno (500) al obtener la lista de presos: {}", e.getMessage(), e);
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
        logger.info("Recibida petición GET en /preso/{} para obtener preso por ID", id);
        try {
            return new ResponseEntity<>(presoService.obtenerPorId(id), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.warn("Preso no encontrado (404 Not Found) para el ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            logger.error("Error interno (500) al obtener el preso con ID {}: {}", id, e.getMessage(), e);
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
        logger.info("Recibida petición DELETE en /preso/eliminar/{}", id);
        try {
            boolean eliminado = presoService.eliminar(id);

            if (eliminado) {
                return new ResponseEntity<>("Preso eliminado correctamente.", HttpStatus.OK);
            } else {
                logger.warn("No se pudo eliminar, preso no encontrado con ID: {}", id);
                return new ResponseEntity<>("No se encontró el preso con ID: " + id, HttpStatus.NOT_FOUND);
            }

        } catch (IllegalArgumentException e) {
            logger.warn("Petición rechazada al eliminar preso (400 Bad Request): {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            logger.error("Error interno (500) al eliminar el preso con ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Traslada a un preso a una cárcel diferente.
     *
     * Endpoint: POST /preso/trasladar/{id}/{nombreCarcel}
     *
     * @param id identificador del preso
     * @param nombreCarcel nombre de la cárcel de destino
     * @return mensaje de éxito o error correspondiente
     */
    @PostMapping("/trasladar/{id}/{nombreCarcel}")
    public ResponseEntity<?> trasladarPreso(@PathVariable Integer id, @PathVariable String nombreCarcel) {
        logger.info("Recibida petición POST en /preso/trasladar/{}/{}", id, nombreCarcel);
        try {
            // Llamada al servicio para ejecutar la lógica de negocio del traslado
            presoService.trasladarPreso(id, nombreCarcel);
            
            logger.info("Traslado exitoso: Preso ID {} enviado a {}", id, nombreCarcel);
            return new ResponseEntity<>("Traslado realizado correctamente a " + nombreCarcel, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // Se lanza si el preso no existe o la cárcel es inválida
            logger.warn("Petición de traslado rechazada (400 Bad Request): {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            logger.error("Error interno (500) al trasladar el preso con ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor al procesar el traslado", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
