package com.example.JailQ.Service;
import java.time.LocalDate;

import com.example.JailQ.Entidades.Preso;
import com.example.JailQ.Dao.PresoDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar operaciones sobre {@link Preso}.
 * 
 * Esta clase se encarga de validar y guardar objetos Preso en la base de datos
 * utilizando {@link PresoDAO}. Incluye validaciones de nombre, apellidos, edad,
 * condena y fechas de ingreso.
 * 
 * Anotada con {@link Service} para ser detectada como componente de Spring.
 */

@Service //indica que esta funcion es un servicio
public class PresoService {

    /**
     * DAO para la entidad {@link Preso}.
     * Crear una instancia de PresoDAO para usar sus funcionalidades. 
     * 
     * Se inyecta automáticamente mediante Spring con {@link Autowired}.
     */
    @Autowired
    private PresoDAO presoDAO;
    /**
     * Añade un nuevo {@link Preso} a la base de datos tras realizar validaciones.
     * 
     * Validaciones realizadas:
     * <ul>
     *   <li>Compronar que existe el objeto</li>
     *   <li>Validación de Nombre y Apellidos (con trim para detectar "" también como vacío)</li>
     *   <li>Validacion de edad</li>
     *   <li>Comprobacion de condena.</li>
     *   <li>Validacion de fecha de ingreso -> no puede ser futura y tampoco puede entrar antes de haber nacido</li>
     * </ul>
     * 
     * Mensajes de error se imprimen en consola en caso de datos inválidos.
     * Si todas las validaciones pasan, el preso se guarda mediante {@link PresoDAO#save(Object)}.
     * 
     * @param nuevoPreso El objeto {@link Preso} a añadir. No puede ser {@code null}.
     */
    public void anadirPreso(Preso nuevoPreso){
        //Compronar que existe el objeto
        if (nuevoPreso == null) {
            System.err.println("No se ha recibido ningún dato del preso");
            return;
        }

        LocalDate fechaActual = LocalDate.now();

        //validación de Nombre y Apellidos (con trim para detectar "" también como vacío)
        if (nuevoPreso.getNombre() == null || nuevoPreso.getNombre().trim().isEmpty()){
            System.err.println("El campo nombre es obligatorio");
        } 
        else if (nuevoPreso.getApellidos() == null || nuevoPreso.getApellidos().trim().isEmpty()){
            System.err.println("El campo apellidos es obligatorio");
        }
        //validacion de edad
        else if (nuevoPreso.getFechaNacimiento() == null || 
                 nuevoPreso.getFechaNacimiento().plusYears(18).isAfter(fechaActual)) {
            System.err.println("El preso debe ser mayor de edad o la fecha de nacimiento es nula");
        }
        //Comprobacion de condena
        else if (nuevoPreso.getCondena() == null || nuevoPreso.getCondena() <= 0){
            System.err.println("La condena debe ser mayor a 0 años");
        }
        // 4.Validacion de fecha de ingreso -> no puede ser futura y tampoco puede entrar antes de haber nacido
        else if (nuevoPreso.getFechaIngreso() == null || 
                 nuevoPreso.getFechaIngreso().isAfter(fechaActual)) {
            System.err.println("La fecha de ingreso no puede ser futura");
        }
        else if (nuevoPreso.getFechaIngreso().isBefore(nuevoPreso.getFechaNacimiento())) {
            System.err.println("La fecha de ingreso no puede ser anterior al nacimiento");
        }
        else {
            //Si todos los datos son válidos: 
            System.out.println("Preso validado correctamente. Procediendo a guardar...");
            presoDAO.save(nuevoPreso);
        }
    }
}
