package com.example.JailQ.Service;

import com.example.JailQ.Dao.CarcelDAO;
import com.example.JailQ.Entidades.Carcel;
import com.example.JailQ.Dao.PresoDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio encargado de la lógica de negocio relacionada con las cárceles.
 * 
 * Proporciona métodos para:
 * <ul>
 * <li>Crear nuevas cárceles</li>
 * <li>Eliminar cárceles</li>
 * <li>Obtener todas las cárceles</li>
 * <li>Obtener una cárcel por su ID</li>
 * </ul>
 * 
 * Este servicio actúa como intermediario entre el controlador (Controller)
 * y el acceso a datos (DAO).
 */
@Service
public class CarcelService {

    /** DAO utilizado para acceder a la base de datos de cárceles */
    private final CarcelDAO carcelDAO;



    private final PresoDAO presoDAO;

    /**
     * Obtiene un recuento de cuántos presos hay registrados en cada cárcel.
     * * @return Un mapa donde la clave es el nombre de la cárcel y el valor es la cantidad de presos.
     */
    public Map<String, Long> obtenerOcupacionPorCarcel() {
        Map<String, Long> estadisticas = new HashMap<>();
        Iterable<Carcel> todasLasCarceles = carcelDAO.findAll();
        
        for (Carcel carcel : todasLasCarceles) {
            long totalPresos = presoDAO.countByCarcel(carcel);
            estadisticas.put(carcel.getNombre(), totalPresos);
        }
        
        return estadisticas;
    }


    /**
     * Constructor del servicio que inyecta el DAO de cárceles.
     *
     * @param carcelDAO DAO utilizado para persistir y consultar entidades Carcel
     */
    @Autowired
    public CarcelService(CarcelDAO carcelDAO, PresoDAO presoDAO) {
        this.carcelDAO = carcelDAO;
        this.presoDAO = presoDAO;
    }

    /**
     * Añade una nueva cárcel tras validar los campos obligatorios.
     *
     * @param nuevaCarcel Objeto Carcel a guardar
     * @return La cárcel guardada con su ID generado
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public Carcel anadirCarcel(Carcel nuevaCarcel) {
        if (nuevaCarcel == null) {
            throw new IllegalArgumentException("No se ha recibido ningún dato de la cárcel.");
        }

        if (nuevaCarcel.getNombre() == null || nuevaCarcel.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la cárcel es obligatorio.");
        }

        if (nuevaCarcel.getLocalidad() == null || nuevaCarcel.getLocalidad().trim().isEmpty()) {
            throw new IllegalArgumentException("La localidad es obligatoria.");
        }

        if (nuevaCarcel.getCapacidad() == null || nuevaCarcel.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser un número mayor que cero.");
        }

        System.out.println("Cárcel validada correctamente. Procediendo a guardar...");
        return carcelDAO.save(nuevaCarcel);
    }

    /**
     * Elimina una cárcel de la base de datos según su ID.
     *
     * @param id El identificador de la cárcel a eliminar
     * @return true si se eliminó correctamente, false si no se encontró
     * @throws IllegalArgumentException si el ID es nulo
     */
    public boolean eliminarCarcel(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la cárcel no puede ser nulo.");
        }

        if (carcelDAO.existsById(id)) {
            carcelDAO.deleteById(id);
            System.out.println("Cárcel con ID " + id + " eliminada correctamente.");
            return true;
        } else {
            System.err.println("No se encontró ninguna cárcel con el ID: " + id);
            return false;
        }
    }

    /**
     * Obtiene todas las cárceles almacenadas en la base de datos.
     *
     * @return Lista de cárceles
     */
    public List<Carcel> obtenerCarceles() {
        List<Carcel> lista = new ArrayList<>();
        carcelDAO.findAll().forEach(lista::add);
        return lista;
    }

    /**
     * Obtiene una cárcel según su ID.
     *
     * @param id identificador de la cárcel
     * @return la cárcel encontrada
     * @throws IllegalArgumentException si no existe
     */
    public Carcel obtenerCarcelPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return carcelDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ninguna cárcel con ese ID."));
    }

    /**
     * Obtiene la cantidad de presos de una cárcel específica mediante su ID.
     *
     * @param id Identificador de la cárcel
     * @return Número de presos en dicha cárcel
     * @throws IllegalArgumentException si la cárcel no existe
     */
    public long obtenerOcupacionDeCarcel(Integer id) {
        // Reutilizamos la lógica de búsqueda que ya existe en el servicio
        Carcel carcel = obtenerCarcelPorId(id);
        
        // Usamos el método de conteo que definimos en el DAO anteriormente
        return presoDAO.countByCarcel(carcel);
    }
}