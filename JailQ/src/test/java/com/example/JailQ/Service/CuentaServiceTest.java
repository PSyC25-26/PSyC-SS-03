package com.example.JailQ.Service;

import com.example.JailQ.Dao.CuentaDAO;
import com.example.JailQ.Entidades.Cuenta;
import com.example.JailQ.Entidades.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario para {@link CuentaService}.
 *
 * <p>
 * Se prueba toda la lógica de negocio del servicio, usando Mockito
 * para simular la base de datos a través de {@link CuentaDAO}.
 * </p>
 *
 * <p>
 * Se verifica la funcionalidad de:
 * </p>
 * <ul>
 * <li>Añadir cuentas (validaciones y éxito)</li>
 * <li>Eliminar cuentas (existentes, no existentes y nulas)</li>
 * <li>Eliminar cuentas de tipo POLICIA</li>
 * <li>Obtener todas las cuentas de tipo POLICIA</li>
 * <li>Login de cuentas de tipo POLICIA</li>
 * </ul>
 */
class CuentaServiceTest {

    /** Mock del DAO de cuentas */
    private CuentaDAO cuentaDAO;

    /** Servicio bajo prueba */
    private CuentaService cuentaService;

    /**
     * Configuración antes de cada test.
     * <p>
     * Se crea un mock de {@link CuentaDAO} y se inyecta en {@link CuentaService}.
     * </p>
     */
    @BeforeEach
    void setUp() {
        // Creamos el mock del DAO
        cuentaDAO = mock(CuentaDAO.class);

        // Inyectamos el mock a través del constructor
        cuentaService = new CuentaService(cuentaDAO);
    }

    // ==================== TESTS anadirCuenta ====================

    /**
     * Verifica que se pueda añadir una cuenta válida.
     * Comprueba que {@link CuentaDAO#save} sea llamado una vez.
     */
    @Test
    void testAnadirCuentaValida() {
        Cuenta c = new Cuenta();
        c.setUsername("policia1");
        c.setPassword("1234");
        c.setTipoCuenta(TipoCuenta.POLICIA);

        when(cuentaDAO.save(c)).thenReturn(c);

        Cuenta result = cuentaService.anadirCuenta(c);

        assertEquals("policia1", result.getUsername());
        verify(cuentaDAO, times(1)).save(c);
    }

    /**
     * Verifica que se lance {@link IllegalArgumentException} al intentar
     * añadir null como cuenta.
     */
    @Test
    void testAnadirCuentaNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cuentaService.anadirCuenta(null));
        assertEquals("No se ha recibido ningún dato de la cuenta.", ex.getMessage());
        verify(cuentaDAO, never()).save(any());
    }

    /**
     * Verifica que se lance {@link IllegalArgumentException} si el username es
     * nulo.
     */
    @Test
    void testAnadirCuentaUsernameNulo() {
        Cuenta c = new Cuenta();
        c.setPassword("1234");
        c.setTipoCuenta(TipoCuenta.POLICIA);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cuentaService.anadirCuenta(c));
        assertEquals("El nombre de usuario (username) es obligatorio.", ex.getMessage());
        verify(cuentaDAO, never()).save(any());
    }

    /**
     * Verifica que se lance {@link IllegalArgumentException} si la contraseña es
     * nula.
     */
    @Test
    void testAnadirCuentaPasswordNulo() {
        Cuenta c = new Cuenta();
        c.setUsername("policia1");
        c.setTipoCuenta(TipoCuenta.POLICIA);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cuentaService.anadirCuenta(c));
        assertEquals("La contraseña es obligatoria.", ex.getMessage());
        verify(cuentaDAO, never()).save(any());
    }

    /**
     * Verifica que se lance {@link IllegalArgumentException} si el tipo de cuenta
     * es nulo.
     */
    @Test
    void testAnadirCuentaTipoNulo() {
        Cuenta c = new Cuenta();
        c.setUsername("policia1");
        c.setPassword("1234");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cuentaService.anadirCuenta(c));
        assertEquals("El tipo de cuenta es obligatorio.", ex.getMessage());
        verify(cuentaDAO, never()).save(any());
    }

    // ==================== TESTS eliminarCuenta ====================

    /**
     * Verifica que se pueda eliminar una cuenta existente.
     * Comprueba que {@link CuentaDAO#deleteById} sea llamado.
     */
    @Test
    void testEliminarCuentaExistente() {
        when(cuentaDAO.existsById(1)).thenReturn(true);

        boolean res = cuentaService.eliminarCuenta(1);
        assertTrue(res);
        verify(cuentaDAO).deleteById(1);
    }

    /**
     * Verifica que eliminar una cuenta inexistente devuelva false
     * y {@link CuentaDAO#deleteById} nunca sea llamado.
     */
    @Test
    void testEliminarCuentaNoExistente() {
        when(cuentaDAO.existsById(2)).thenReturn(false);

        boolean res = cuentaService.eliminarCuenta(2);
        assertFalse(res);
        verify(cuentaDAO, never()).deleteById(2);
    }

    /**
     * Verifica que eliminar una cuenta con ID nulo lance
     * {@link IllegalArgumentException}.
     */
    @Test
    void testEliminarCuentaIdNulo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cuentaService.eliminarCuenta(null));
        assertEquals("El ID de la cuenta no puede ser nulo.", ex.getMessage());
    }

    // ==================== TESTS eliminarCuentaPolicia ====================

    /**
     * Verifica que se pueda eliminar una cuenta de tipo POLICIA correctamente.
     */
    @Test
    void testEliminarCuentaPoliciaValida() {
        Cuenta c = new Cuenta();
        c.setTipoCuenta(TipoCuenta.POLICIA);

        when(cuentaDAO.findById(1)).thenReturn(Optional.of(c));

        boolean res = cuentaService.eliminarCuentaPolicia(1);
        assertTrue(res);
        verify(cuentaDAO).deleteById(1);
    }

    /**
     * Verifica que eliminarCuentaPolicia con ID nulo lance
     * {@link IllegalArgumentException}.
     */
    @Test
    void testEliminarCuentaPoliciaIdNulo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cuentaService.eliminarCuentaPolicia(null));
        assertEquals("El ID de la cuenta no puede ser nulo.", ex.getMessage());
    }

    /**
     * Verifica que eliminarCuentaPolicia lance excepción si la cuenta no existe.
     */
    @Test
    void testEliminarCuentaPoliciaNoExiste() {
        when(cuentaDAO.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cuentaService.eliminarCuentaPolicia(99));
        assertEquals("No se encontró ninguna cuenta con ese ID.", ex.getMessage());
    }

    /**
     * Verifica que eliminarCuentaPolicia lance excepción si la cuenta no es de tipo
     * POLICIA.
     */
    @Test
    void testEliminarCuentaPoliciaNoEsPolicia() {
        Cuenta c = new Cuenta();
        c.setTipoCuenta(TipoCuenta.FAMILIA);
        when(cuentaDAO.findById(5)).thenReturn(Optional.of(c));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cuentaService.eliminarCuentaPolicia(5));
        assertEquals("La cuenta indicada no pertenece a un policía.", ex.getMessage());
    }

    // ==================== TESTS obtenerCuentasPolicia ====================

    /**
     * Verifica que obtenerCuentasPolicia devuelva solo las cuentas de tipo POLICIA.
     */
    @Test
    void testObtenerCuentasPolicia() {
        Cuenta c1 = new Cuenta();
        c1.setTipoCuenta(TipoCuenta.POLICIA);
        Cuenta c2 = new Cuenta();
        c2.setTipoCuenta(TipoCuenta.FAMILIA);
        Cuenta c3 = new Cuenta();
        c3.setTipoCuenta(TipoCuenta.POLICIA);

        when(cuentaDAO.findAll()).thenReturn(Arrays.asList(c1, c2, c3));

        List<Cuenta> result = cuentaService.obtenerCuentasPolicia();
        assertEquals(2, result.size());
        assertTrue(result.contains(c1));
        assertTrue(result.contains(c3));
        verify(cuentaDAO).findAll();
    }

    /**
     * Verifica que obtenerCuentasPolicia devuelva lista vacía si no hay cuentas de
     * tipo POLICIA.
     */
    @Test
    void testObtenerCuentasPoliciaVacia() {
        when(cuentaDAO.findAll()).thenReturn(List.of());
        List<Cuenta> result = cuentaService.obtenerCuentasPolicia();
        assertTrue(result.isEmpty());
        verify(cuentaDAO).findAll();
    }

    // ==================== TESTS loginPolicia ====================

    /**
     * Verifica loginPolicia con credenciales correctas de tipo POLICIA.
     */
    @Test
    void testLoginPoliciaCorrecto() {
        Cuenta c = new Cuenta();
        c.setUsername("policia1");
        c.setPassword("1234");
        c.setTipoCuenta(TipoCuenta.POLICIA);

        when(cuentaDAO.findAll()).thenReturn(List.of(c));

        Cuenta result = cuentaService.loginPolicia("policia1", "1234");
        assertNotNull(result);
        assertEquals(c, result);
    }

    /**
     * Verifica loginPolicia con credenciales incorrectas (usuario o contraseña).
     */
    @Test
    void testLoginPoliciaIncorrecto() {
        Cuenta c = new Cuenta();
        c.setUsername("policia1");
        c.setPassword("1234");
        c.setTipoCuenta(TipoCuenta.POLICIA);

        when(cuentaDAO.findAll()).thenReturn(List.of(c));

        assertNull(cuentaService.loginPolicia("policia1", "wrongpass"));
        assertNull(cuentaService.loginPolicia("wronguser", "1234"));
    }

    /**
     * Verifica loginPolicia con credenciales correctas pero tipo de cuenta
     * incorrecto.
     */
    @Test
    void testLoginPoliciaTipoIncorrecto() {
        Cuenta c = new Cuenta();
        c.setUsername("policia1");
        c.setPassword("1234");
        c.setTipoCuenta(TipoCuenta.FAMILIA);

        when(cuentaDAO.findAll()).thenReturn(List.of(c));

        assertNull(cuentaService.loginPolicia("policia1", "1234"));
    }
}