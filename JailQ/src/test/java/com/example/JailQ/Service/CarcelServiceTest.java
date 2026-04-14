package com.example.JailQ.Service;

import com.example.JailQ.Dao.CarcelDAO;
import com.example.JailQ.Entidades.Carcel;
import com.example.JailQ.Entidades.Preso;
import com.example.JailQ.Dao.PresoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.test.util.ReflectionTestUtils;



/**
 * Test unitario para {@link CarcelService}.
 * 
 * <p>
 * Utiliza Mockito para simular el comportamiento de {@link CarcelDAO} y probar
 * la lógica de negocio del servicio sin necesidad de una base de datos real.
 * </p>
 * 
 * <p>
 * Se comprueba:
 * <ul>
 * <li>Adición de cárceles válidas</li>
 * <li>Validaciones de nombre y capacidad</li>
 * <li>Eliminación de cárceles existentes y no existentes</li>
 * <li>Obtención de cárceles por ID (existentes y no existentes)</li>
 * </ul>
 * </p>
 * commit
 */

class CarcelServiceTest {

    /** Mock del DAO de cárceles */
    private CarcelDAO carcelDAO;

    /** Mock del DAO de presos */
    private PresoDAO presoDAO;

    /** Servicio bajo prueba */
    private CarcelService carcelService;

    /**
     * Configuración antes de cada test.
     * 
     * <p>
     * Se crea un mock de {@link CarcelDAO} y se inyecta en {@link CarcelService}.
     * </p>
     */
    @BeforeEach
    void setUp() {
        // Creamos el mock del DAO
        carcelDAO = mock(CarcelDAO.class);
        presoDAO = mock(PresoDAO.class);
        // Inyectamos el mock en el servicio mediante el constructor
        carcelService = new CarcelService(carcelDAO, presoDAO);
    }

    /**
     * Test que verifica la adición de una cárcel válida.
     * Se comprueba que {@link CarcelDAO#save} se llame una vez.
     */
    @Test
    void testAnadirCarcel_Valida() {
        Carcel c = new Carcel();
        c.setNombre("Alcatraz");
        c.setLocalidad("California");
        c.setCapacidad(100);

        when(carcelDAO.save(c)).thenReturn(c);

        Carcel result = carcelService.anadirCarcel(c);

        assertEquals("Alcatraz", result.getNombre());
        verify(carcelDAO, times(1)).save(c);
    }

    /**
     * Test que verifica que se lanza {@link IllegalArgumentException}
     * al intentar añadir una cárcel sin nombre.
     * No debe llamar a {@link CarcelDAO#save}.
     */
    @Test
    void testAnadirCarcel_NombreNulo() {
        Carcel c = new Carcel();
        c.setLocalidad("California");
        c.setCapacidad(100);

        assertThrows(IllegalArgumentException.class, () -> carcelService.anadirCarcel(c));
        verify(carcelDAO, never()).save(any());
    }

    /**
     * Test que lanza excepción al añadir cárcel con localidad nula.
     */
    @Test
    void testAnadirCarcel_LocalidadNula() {
        Carcel c = new Carcel();
        c.setNombre("Cárcel Central");
        c.setCapacidad(50);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carcelService.anadirCarcel(c));

        assertEquals("La localidad es obligatoria.", ex.getMessage());
        verify(carcelDAO, never()).save(any());
    }

    /**
     * Test que verifica la validación de capacidad inválida (<=0).
     * Se comprueba que se lance la excepción correcta y que
     * {@link CarcelDAO#save} nunca sea llamado.
     */
    @Test
    void testAnadirCarcelCapacidadInvalida() {
        Carcel carcelMala = new Carcel();
        carcelMala.setNombre("Cárcel Sur");
        carcelMala.setDescripcion("Centro penitenciario");
        carcelMala.setLocalidad("Sevilla");
        carcelMala.setCapacidad(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            carcelService.anadirCarcel(carcelMala);
        });

        assertEquals("La capacidad debe ser un número mayor que cero.", ex.getMessage());
        verify(carcelDAO, never()).save(any());
    }

    /**
     * Test que lanza excepción al añadir null como cárcel.
     */
    @Test
    void testAnadirCarcel_Null() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carcelService.anadirCarcel(null));

        assertEquals("No se ha recibido ningún dato de la cárcel.", ex.getMessage());
        verify(carcelDAO, never()).save(any());
    }

    /**
     * Test que verifica la eliminación de una cárcel existente.
     * {@link CarcelDAO#deleteById} debe ser llamado exactamente una vez.
     */
    @Test
    void testEliminarCarcel_Existe() {
        when(carcelDAO.existsById(1)).thenReturn(true);

        boolean res = carcelService.eliminarCarcel(1);

        assertTrue(res);
        verify(carcelDAO, times(1)).deleteById(1);
    }

    /**
     * Test que verifica la eliminación de una cárcel inexistente.
     * {@link CarcelDAO#deleteById} nunca debe ser llamado.
     */
    @Test
    void testEliminarCarcel_NoExiste() {
        when(carcelDAO.existsById(2)).thenReturn(false);

        boolean res = carcelService.eliminarCarcel(2);

        assertFalse(res);
        verify(carcelDAO, never()).deleteById(2);
    }

    /**
     * Verifica que al intentar eliminar una cárcel con ID nulo
     * se lance {@link IllegalArgumentException}.
     */
    @Test
    void testEliminarCarcel_IdNulo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            carcelService.eliminarCarcel(null);
        });
        assertEquals("El ID de la cárcel no puede ser nulo.", ex.getMessage());
    }

    /**
     * Test que verifica que se lance {@link IllegalArgumentException} al
     * intentar obtener una cárcel por ID inexistente.
     */
    @Test
    void testObtenerCarcelPorId_NoExiste() {
        when(carcelDAO.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> carcelService.obtenerCarcelPorId(99));
    }

    /**
     * Test que verifica la obtención correcta de una cárcel existente por ID.
     * Se comprueba que {@link CarcelDAO#findById} sea llamado.
     */
    @Test
    void testObtenerCarcelPorId_Existe() {
        Carcel c = new Carcel();
        c.setNombre("Sing Sing");
        c.setLocalidad("New York");
        c.setCapacidad(200);

        when(carcelDAO.findById(5)).thenReturn(Optional.of(c));

        Carcel result = carcelService.obtenerCarcelPorId(5);

        assertEquals("Sing Sing", result.getNombre());
        verify(carcelDAO).findById(5);
    }

    /**
     * Verifica que al intentar obtener una cárcel con ID nulo
     * se lance {@link IllegalArgumentException}.
     */
    @Test
    void testObtenerCarcelPorId_IdNulo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            carcelService.obtenerCarcelPorId(null);
        });
        assertEquals("El ID no puede ser nulo.", ex.getMessage());
    }

    /**
     * Test que verifica la obtención de todas las cárceles.
     */
    @Test
    void testObtenerCarceles() {
        Carcel c1 = new Carcel();
        c1.setNombre("Alcatraz");
        Carcel c2 = new Carcel();
        c2.setNombre("Sing Sing");

        when(carcelDAO.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<Carcel> result = carcelService.obtenerCarceles();

        assertEquals(2, result.size());
        assertTrue(result.contains(c1));
        assertTrue(result.contains(c2));
        verify(carcelDAO).findAll();
    }


    /**
     * Test que verifica la obtención de la ocupación de una cárcel específica.
     */
    @Test
    void testObtenerOcupacionDeCarcelEspecifica() {
        // 1. Preparamos una cárcel simulada
        Carcel carcelSimulada = new Carcel();
        carcelSimulada.setIdCarcel(1);
        carcelSimulada.setNombre("Cárcel Mock");

        // 2. Le decimos a Mockito cómo comportarse cuando se le pregunte
        when(carcelDAO.findById(1)).thenReturn(Optional.of(carcelSimulada));
        when(presoDAO.countByCarcel(carcelSimulada)).thenReturn(3L);

        // 3. Ejecutamos la función a testear
        long ocupacion = carcelService.obtenerOcupacionDeCarcel(1);

        // 4. Verificamos que el resultado es correcto y que se llamaron a las funciones
        assertEquals(3L, ocupacion, "La cárcel debería tener exactamente 3 presos");
        verify(carcelDAO).findById(1);
        verify(presoDAO).countByCarcel(carcelSimulada);
    }

    /**
    * Test que verifica que se lance {@link IllegalArgumentException} al
    * intentar obtener la ocupación de una cárcel inexistente.
    */
    @Test
    void testObtenerOcupacionDeCarcelNoExistente() {
        // Le decimos a Mockito que simule que no encuentra la cárcel 9999
        when(carcelDAO.findById(9999)).thenReturn(Optional.empty());

        // Verificamos que salte nuestra excepción
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            carcelService.obtenerOcupacionDeCarcel(9999);
        });

        assertEquals("No se encontró ninguna cárcel con ese ID.", excepcion.getMessage());
    }
}