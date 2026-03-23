package com.example.JailQ.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.JailQ.Dao.CuentaDAO;
import com.example.JailQ.Entidades.Cuenta;
import com.example.JailQ.Entidades.TipoCuenta;
import com.example.JailQ.Service.CuentaService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import com.example.JailQ.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Clase de pruebas de integración para el servicio {@link CuentaService}.
 * <p>
 * Utiliza Testcontainers para levantar una base de datos MySQL efímera
 * y garantizar el aislamiento de cada prueba.
 */

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class CuentaServiceTest {

    @Autowired
    private CuentaDAO cuentaDAO;

    @Autowired
    private CuentaService cuentaService;

    /**
     * Configuración inicial que se ejecuta antes de cada test.
     * Limpia la base de datos para asegurar que los tests sean independientes
     * y no compartan estado.
     */
    @BeforeEach
    public void setUp() {
        cuentaDAO.deleteAll();
    }

    /**
     * Verifica que el sistema permite añadir una cuenta correctamente
     * cuando todos los datos obligatorios son proporcionados.
     */
    @Test
    void anadirBienCuenta() {
        Cuenta cuentaTest = new Cuenta();
        cuentaTest.setNombre("Paco");
        cuentaTest.setApellidos("Gómez");
        cuentaTest.setUsername("pgomez_policia");
        cuentaTest.setPassword("1234");
        cuentaTest.setTipoCuenta(TipoCuenta.POLICIA);

        Cuenta guardada = cuentaService.anadirCuenta(cuentaTest);

        assertNotNull(guardada.getIdCuentas(), "La cuenta guardada debería tener un ID asignado");
        assertEquals(1, cuentaDAO.count(), "Debería haber una cuenta guardada en la BD");
    }

    /**
     * Verifica que el sistema lanza una {@link IllegalArgumentException}
     * al intentar añadir una cuenta en la que falta el nombre de usuario.
     */
    @Test
    void anadirCuentaSinUsername() {
        Cuenta cuentaMala = new Cuenta();
        cuentaMala.setNombre("Paco");
        cuentaMala.setPassword("1234");
        cuentaMala.setTipoCuenta(TipoCuenta.POLICIA);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cuentaService.anadirCuenta(cuentaMala);
        });

        assertEquals("El nombre de usuario (username) es obligatorio.", exception.getMessage());
        assertEquals(0, cuentaDAO.count(), "No se debería haber guardado la cuenta sin username");
    }

    /**
     * Verifica que el sistema puede eliminar exitosamente una cuenta
     * existente utilizando su identificador único.
     */
    @Test
    void eliminarCuentaExistente() {
        Cuenta cuentaTest = new Cuenta();
        cuentaTest.setUsername("borrar_user");
        cuentaTest.setPassword("1234");
        cuentaTest.setTipoCuenta(TipoCuenta.POLICIA);
        Cuenta guardada = cuentaService.anadirCuenta(cuentaTest);

        boolean resultado = cuentaService.eliminarCuenta(guardada.getIdCuentas());

        assertTrue(resultado, "Debería devolver true al eliminar una cuenta que existe");
        assertEquals(0, cuentaDAO.count(), "La BD debería quedar vacía tras eliminar la cuenta");
    }

    /**
     * Verifica el comportamiento del sistema al intentar eliminar
     * una cuenta que no existe en la base de datos.
     */
    @Test
    void eliminarCuentaNoExistente() {
        boolean resultado = cuentaService.eliminarCuenta(999);

        assertFalse(resultado, "Debería devolver false al intentar eliminar una cuenta que no existe");
    }
}