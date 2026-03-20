package com.example.JailQ.Facade;

import com.example.JailQ.Entidades.Cuenta;
import com.example.JailQ.Service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    /**
     * Endpoint para crear una nueva cuenta.
     * Se accede enviando un POST a /cuentas/crear
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearCuenta(@RequestBody Cuenta cuenta) {
        try {
            Cuenta cuentaGuardada = cuentaService.anadirCuenta(cuenta);
            // Devuelve un código 201 (Created) si va bien
            return new ResponseEntity<>(cuentaGuardada, HttpStatus.CREATED); 
        } catch (IllegalArgumentException e) {
            // Devuelve un código 400 (Bad Request) si falla la validación
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Devuelve un código 500 (Internal Server Error) para otros fallos
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para eliminar una cuenta por su ID.
     * Se accede enviando un DELETE a /cuentas/eliminar/{id}
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable Integer id) {
        try {
            boolean eliminado = cuentaService.eliminarCuenta(id);
            
            if (eliminado) {
                // Devuelve un código 200 (OK) con un mensaje de éxito
                return new ResponseEntity<>("Cuenta eliminada correctamente.", HttpStatus.OK);
            } else {
                // Devuelve un código 404 (Not Found) si el ID no existe en la BBDD
                return new ResponseEntity<>("No se encontró la cuenta con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            // Devuelve un código 400 (Bad Request) si el ID no es válido
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Devuelve un código 500 para errores inesperados
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}