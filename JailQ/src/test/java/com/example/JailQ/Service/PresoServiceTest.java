package com.example.JailQ.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.JailQ.Dao.CarcelDAO;
import com.example.JailQ.Dao.PresoDAO;
import com.example.JailQ.Entidades.Carcel;
import com.example.JailQ.Entidades.Delito;
import com.example.JailQ.Entidades.Preso;

/**
 * Test unitario para {@link PresoService}.
 *
 * <p>
 * Verifica la lógica de negocio completa del servicio, incluyendo:
 * <ul>
 * <li>Validaciones de datos obligatorios al añadir un preso</li>
 * <li>Verificación de edad mínima de 18 años</li>
 * <li>Validaciones de fecha de ingreso</li>
 * <li>Obtención de todos los presos y por ID</li>
 * <li>Eliminación de presos existentes e inexistentes</li>
 * </ul>
 * </p>
 */
class PresoServiceTest {

    /**
     * Mock de {@link PresoDAO} utilizado para simular el acceso a la base de datos
     * de presos durante los tests.
     */
    private PresoDAO presoDAO;

    /**
     * Mock de {@link CarcelDAO} utilizado para simular el acceso a la base de datos
     * de cárceles durante los tests.
     */
    private CarcelDAO carcelDAO;

    /**
     * Servicio a testear.
     */
    private PresoService presoService;

    /**
     * Configuración previa a cada test de {@link PresoService}.
     *
     * <p>
     * Se crea un mock de {@link PresoDAO} usando Mockito y se inyecta en
     * {@link PresoService} mediante el constructor. Esto permite probar la
     * lógica del servicio sin depender de una base de datos real.
     * </p>
     */
    @BeforeEach
    void setUp() throws Exception {
        presoDAO = mock(PresoDAO.class);
        carcelDAO = mock(CarcelDAO.class);

        // Inyectamos el mock a través del constructor
        presoService = new PresoService(presoDAO);

        // Inyección manual del carcelDAO, porque el servicio no lo recibe por constructor
        Field field = PresoService.class.getDeclaredField("carcelDAO");
        field.setAccessible(true);
        field.set(presoService, carcelDAO);
    }

    /**
     * Test que verifica la adición de un preso válido con todos los campos
     * obligatorios.
     */
    @Test
    void testAnadirPreso_Valido() {
        Preso p = new Preso();
        p.setNombre("Juan");
        p.setApellidos("Pérez");
        p.setFechaNacimiento(LocalDate.now().minusYears(30));
        p.setCondena(5);
        p.setDelitos(Arrays.asList(Delito.ROBO, Delito.HOMICIDIO));
        p.setFechaIngreso(LocalDate.now().minusDays(10));

        Carcel carcel = new Carcel();
        carcel.setIdCarcel(1);
        carcel.setNombre("Martutene");
        p.setCarcel(carcel);

        when(carcelDAO.findById(1)).thenReturn(Optional.of(carcel));
        when(presoDAO.save(p)).thenReturn(p);

        Preso result = presoService.anadirPreso(p);

        assertEquals("Juan", result.getNombre());
        assertEquals(2, result.getDelitos().size());
        assertNotNull(result.getCarcel());
        verify(carcelDAO, times(1)).findById(1);
        verify(presoDAO, times(1)).save(p);
    }

    /**
     * Test que lanza excepción al añadir un preso null.
     */
    @Test
    void testAnadirPreso_Null() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> presoService.anadirPreso(null));
        assertEquals("No se ha recibido ningún dato del preso.", ex.getMessage());
        verify(presoDAO, never()).save(any());
    }

    /**
     * Test que lanza excepción al añadir un preso con nombre vacío.
     */
    @Test
    void testAnadirPreso_NombreNulo() {
        Preso p = new Preso();
        p.setApellidos("Pérez");
        p.setFechaNacimiento(LocalDate.now().minusYears(25));
        p.setCondena(3);
        p.setDelitos(Arrays.asList(Delito.ROBO));
        p.setFechaIngreso(LocalDate.now());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> presoService.anadirPreso(p));
        assertEquals("El nombre es obligatorio.", ex.getMessage());
        verify(presoDAO, never()).save(any());
    }

    /**
     * Test que lanza excepción al añadir un preso con apellidos vacíos.
     */
    @Test
    void testAnadirPreso_ApellidosNulos() {
        Preso p = new Preso();
        p.setNombre("Juan");
        p.setFechaNacimiento(LocalDate.now().minusYears(25));
        p.setCondena(3);
        p.setDelitos(Arrays.asList(Delito.ROBO));
        p.setFechaIngreso(LocalDate.now());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> presoService.anadirPreso(p));
        assertEquals("Los apellidos son obligatorios.", ex.getMessage());
        verify(presoDAO, never()).save(any());
    }

    /**
     * Test que lanza excepción si la fecha de nacimiento es null o menor de 18
     * años.
     */
    @Test
    void testAnadirPreso_FechaNacimientoInvalida() {
        Preso p = new Preso();
        p.setNombre("Juan");
        p.setApellidos("Pérez");
        p.setCondena(3);
        p.setDelitos(Arrays.asList(Delito.ROBO));
        p.setFechaIngreso(LocalDate.now());

        // Fecha de nacimiento null
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> presoService.anadirPreso(p));
        assertEquals("La fecha de nacimiento es obligatoria.", ex1.getMessage());

        // Fecha de nacimiento menor de 18 años
        p.setFechaNacimiento(LocalDate.now().minusYears(16));
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> presoService.anadirPreso(p));
        assertEquals("El preso debe ser mayor de edad.", ex2.getMessage());
    }

    /**
     * Test que lanza excepción si la condena es null o <=0.
     */
    @Test
    void testAnadirPreso_CondenaInvalida() {
        Preso p = new Preso();
        p.setNombre("Juan");
        p.setApellidos("Pérez");
        p.setFechaNacimiento(LocalDate.now().minusYears(25));
        p.setDelitos(Arrays.asList(Delito.ROBO));
        p.setFechaIngreso(LocalDate.now());

        // Condena null
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> presoService.anadirPreso(p));
        assertEquals("La condena debe ser mayor a 0 años.", ex1.getMessage());

        // Condena <=0
        p.setCondena(0);
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> presoService.anadirPreso(p));
        assertEquals("La condena debe ser mayor a 0 años.", ex2.getMessage());
    }

    /**
     * Test que comprueba que una lista de delitos vacía no falla si el resto de
     * datos son válidos.
     */
    @Test
    void testAnadirPreso_DelitosVacios() {
        Preso p = new Preso();
        p.setNombre("Juan");
        p.setApellidos("Pérez");
        p.setFechaNacimiento(LocalDate.now().minusYears(25));
        p.setCondena(5);
        p.setDelitos(Arrays.asList());
        p.setFechaIngreso(LocalDate.now());

        Carcel carcel = new Carcel();
        carcel.setIdCarcel(1);
        carcel.setNombre("Martutene");
        p.setCarcel(carcel);

        when(carcelDAO.findById(1)).thenReturn(Optional.of(carcel));
        when(presoDAO.save(p)).thenReturn(p);

        Preso result = presoService.anadirPreso(p);

        assertEquals(0, result.getDelitos().size());
        verify(carcelDAO, times(1)).findById(1);
        verify(presoDAO, times(1)).save(p);
    }

    /**
     * Test que lanza excepción si la fecha de ingreso es null, futura o anterior al
     * nacimiento.
     */
    @Test
    void testAnadirPreso_FechaIngresoInvalida() {
        Preso p = new Preso();
        p.setNombre("Juan");
        p.setApellidos("Pérez");
        p.setFechaNacimiento(LocalDate.now().minusYears(25));
        p.setCondena(5);
        p.setDelitos(Arrays.asList(Delito.ROBO));

        // Fecha de ingreso null
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> presoService.anadirPreso(p));
        assertEquals("La fecha de ingreso es obligatoria.", ex1.getMessage());

        // Fecha de ingreso futura
        p.setFechaIngreso(LocalDate.now().plusDays(1));
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> presoService.anadirPreso(p));
        assertEquals("La fecha de ingreso no puede ser futura.", ex2.getMessage());

        // Fecha de ingreso antes del nacimiento
        p.setFechaIngreso(LocalDate.now().minusYears(30));
        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class,
                () -> presoService.anadirPreso(p));
        assertEquals("La fecha de ingreso no puede ser anterior al nacimiento.", ex3.getMessage());
    }

    /**
     * Test que verifica la obtención de todos los presos.
     */
    @Test
    void testObtenerTodos() {
        Preso p1 = new Preso();
        p1.setNombre("Juan");
        Preso p2 = new Preso();
        p2.setNombre("Pedro");

        when(presoDAO.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Preso> result = presoService.obtenerTodos();
        assertEquals(2, result.size());
        verify(presoDAO).findAll();
    }

    /**
     * Test que verifica obtener un preso por ID.
     */
    @Test
    void testObtenerPorId() {
        Preso p = new Preso();
        p.setNombre("Juan");
        when(presoDAO.findById(1)).thenReturn(Optional.of(p));

        Preso result = presoService.obtenerPorId(1);
        assertEquals("Juan", result.getNombre());
        verify(presoDAO).findById(1);
    }

    /**
     * Test que lanza excepción al obtener un preso con ID inexistente.
     */
    @Test
    void testObtenerPorId_Inexistente() {
        when(presoDAO.findById(99)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> presoService.obtenerPorId(99));
    }

    /**
     * Test que verifica la eliminación de un preso existente e inexistente.
     */
    @Test
    void testEliminar() {
        when(presoDAO.existsById(1)).thenReturn(true);
        when(presoDAO.existsById(2)).thenReturn(false);

        assertTrue(presoService.eliminar(1));
        assertFalse(presoService.eliminar(2));

        verify(presoDAO).deleteById(1);
        verify(presoDAO, never()).deleteById(2);
    }

    /*
    *Test que verifica que el traslado de preso a otra cárcel se realiza exitosamente
     */

    @Test
    void testTrasladarPreso(){
        Preso p = new Preso();
        p.setNombre("Markel");
        
        Carcel carcel1 = new Carcel();
        carcel1.setIdCarcel(1);
        carcel1.setNombre("Martutene");
        p.setCarcelById(1);

        Carcel carcel2 = new Carcel();
        carcel2.setIdCarcel(2);
        carcel2.setNombre("Alcatraz");

        presoService.trasladarPreso(1, "Alcatraz");
        assertTrue(p.getCarcel().getIdCarcel()==2);
    }
}