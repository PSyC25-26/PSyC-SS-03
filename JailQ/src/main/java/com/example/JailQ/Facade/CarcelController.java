package com.example.JailQ.Facade;

import com.example.JailQ.Entidades.Carcel;
import com.example.JailQ.Service.CarcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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


    /**
     * Endpoint para obtener el recuento de presos por cada cárcel.
     * Método: GET
     * URL: /carcel/ocupacion
     */
    @GetMapping("/ocupacion")
    public ResponseEntity<?> obtenerOcupacion() {
        try {
            Map<String, Long> datosOcupacion = carcelService.obtenerOcupacionPorCarcel();
            return new ResponseEntity<>(datosOcupacion, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al calcular la ocupación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Endpoint para obtener la ocupación de una cárcel específica.
     * URL: GET /carcel/{id}/ocupacion
     * * @param id El ID de la cárcel a consultar
     * @return Cantidad de presos o error si no existe
     */
    @GetMapping("/{id}/ocupacion")
    public ResponseEntity<?> obtenerOcupacionEspecifica(@PathVariable Integer id) {
        try {
            long ocupacion = carcelService.obtenerOcupacionDeCarcel(id);
            return new ResponseEntity<>(ocupacion, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Si el ID no existe, devolvemos un 404 Not Found con el mensaje del service
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar la ocupación: " + e.getMessage(), 
            HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para obtener todas las estadísticas de las cárceles en una sola llamada.
     * URL: GET /carcel/estadisticas-completas
     */
    @GetMapping("/estadisticas-completas")
    public ResponseEntity<?> obtenerEstadisticasCompletas() {
        try {
            List<Map<String, Object>> listaEstadisticas = new ArrayList<>();
            // Obtenemos todas las cárceles usando el método que ya tenéis en el servicio
            List<Carcel> todasLasCarceles = carcelService.obtenerCarceles();

            for (Carcel carcel : todasLasCarceles) {
                long ocupacion = carcelService.obtenerOcupacionDeCarcel(carcel.getIdCarcel());
                
                Map<String, Object> datos = new HashMap<>();
                datos.put("id", carcel.getIdCarcel());
                datos.put("nombre", carcel.getNombre());
                datos.put("capacidad", carcel.getCapacidad());
                datos.put("ocupacion", ocupacion);
                
                listaEstadisticas.add(datos);
            }
            return new ResponseEntity<>(listaEstadisticas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
