package com.example.JailQ.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

@Service // indica que esta funcion es un servicio
public class PresoService {

    /**
     * DAO utilizado para acceder a la base de datos de presos.
     * <p>
     * Se declara como {@code final} para indicar que no puede ser reasignado
     * tras la inicialización. Todas las operaciones del servicio usarán esta
     * instancia.
     * </p>
     */
    private final PresoDAO presoDAO;

    /**
     * DAO utilizado para acceder a la base de datos de presos.
     * <p>
     * Se declara como {@code final} para indicar que no puede ser reasignado
     * tras la inicialización. Todas las operaciones del servicio usarán esta
     * instancia.
     * </p>
     */
    @Autowired
    public PresoService(PresoDAO presoDAO) {
        this.presoDAO = presoDAO;
    }

    /**
     * Añade un nuevo {@link Preso} a la base de datos tras realizar validaciones.
     * 
     * Validaciones realizadas:
     * <ul>
     * <li>Compronar que existe el objeto</li>
     * <li>Validación de Nombre y Apellidos (con trim para detectar "" también como
     * vacío)</li>
     * <li>Validacion de edad</li>
     * <li>Comprobacion de condena.</li>
     * <li>Validacion de fecha de ingreso -> no puede ser futura y tampoco puede
     * entrar antes de haber nacido</li>
     * </ul>
     * 
     * Mensajes de error se imprimen en consola en caso de datos inválidos.
     * Si todas las validaciones pasan, el preso se guarda mediante
     * {@link PresoDAO#save(Object)}.
     * 
     * @param nuevoPreso El objeto {@link Preso} a añadir. No puede ser
     *                   {@code null}.
     */
    public Preso anadirPreso(Preso nuevoPreso) {

        if (nuevoPreso == null) {
            throw new IllegalArgumentException("No se ha recibido ningún dato del preso.");
        }

        LocalDate fechaActual = LocalDate.now();

        // Validación de nombre
        if (nuevoPreso.getNombre() == null || nuevoPreso.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        // Validación de apellidos
        if (nuevoPreso.getApellidos() == null || nuevoPreso.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios.");
        }

        // Validación de fecha de nacimiento
        if (nuevoPreso.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }

        if (nuevoPreso.getFechaNacimiento().plusYears(18).isAfter(fechaActual)) {
            throw new IllegalArgumentException("El preso debe ser mayor de edad.");
        }

        // Validación de condena
        if (nuevoPreso.getCondena() == null || nuevoPreso.getCondena() <= 0) {
            throw new IllegalArgumentException("La condena debe ser mayor a 0 años.");
        }

        // Validación de fecha de ingreso
        if (nuevoPreso.getFechaIngreso() == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria.");
        }

        if (nuevoPreso.getFechaIngreso().isAfter(fechaActual)) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser futura.");
        }

        if (nuevoPreso.getFechaIngreso().isBefore(nuevoPreso.getFechaNacimiento())) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser anterior al nacimiento.");
        }

        return presoDAO.save(nuevoPreso);
    }

    /**
     * Obtiene todos los presos almacenados en la base de datos.
     *
     * @return lista de presos
     */
    public List<Preso> obtenerTodos() {
        List<Preso> lista = new ArrayList<>();
        presoDAO.findAll().forEach(lista::add);
        return lista;
    }

    /**
     * Obtiene un preso según su ID.
     *
     * @param id identificador del preso
     * @return el preso encontrado
     * @throws IllegalArgumentException si no existe
     */
    public Preso obtenerPorId(Integer id) {
        return presoDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún preso con ese ID."));
    }

    /**
     * Elimina un preso según su ID.
     *
     * @param id identificador del preso
     * @return true si se eliminó correctamente, false si no existe
     */
    public boolean eliminar(Integer id) {
        if (!presoDAO.existsById(id)) {
            return false;
        }
        presoDAO.deleteById(id);
        return true;
    }

}