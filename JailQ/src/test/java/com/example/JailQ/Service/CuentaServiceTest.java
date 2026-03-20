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


@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class CuentaServiceTest {

    @Autowired
    private CuentaDAO cuentaDAO;

    @Autowired
    private CuentaService cuentaService;

    @BeforeEach
    public void setUp() {
        // Limpiamos la base de datos antes de cada test para que sean independientes
        cuentaDAO.deleteAll();
    }

    // 1. Test para comprobar que se añade bien una cuenta con todos los datos correctos
    @Test
    void anadirBienCuenta() {
        Cuenta cuentaTest = new Cuenta();
        cuentaTest.setNombre("Paco");
        cuentaTest.setApellidos("Gómez");
        cuentaTest.setUsername("pgomez_policia");
        cuentaTest.setPassword("1234");
        cuentaTest.setTipoCuenta(TipoCuenta.POLICIA);

        Cuenta guardada = cuentaService.anadirCuenta(cuentaTest);

        // Verificamos que se haya generado un ID
        assertNotNull(guardada.getIdCuentas(), "La cuenta guardada debería tener un ID asignado");
        // Verificamos que esté en la BDD
        assertEquals(1, cuentaDAO.count(), "Debería haber una cuenta guardada en la BD");
    }

    // 2. Test para comprobar que salta nuestra validación si falta el username
    @Test
    void anadirCuentaSinUsername() {
        Cuenta cuentaMala = new Cuenta();
        cuentaMala.setNombre("Paco");
        cuentaMala.setPassword("1234");
        cuentaMala.setTipoCuenta(TipoCuenta.POLICIA);
        // Dejamos el username intencionadamente nulo

        // assertThrows comprueba que al ejecutar esa función, salte esa excepción concreta
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cuentaService.anadirCuenta(cuentaMala);
        });

        // Comprobamos que el mensaje de error es exactamente el que programamos
        assertEquals("El nombre de usuario (username) es obligatorio.", exception.getMessage());
        // Verificamos que la BDD sigue vacía
        assertEquals(0, cuentaDAO.count(), "No se debería haber guardado la cuenta sin username");
    }

    // 3. Test para eliminar una cuenta que existe
    @Test
    void eliminarCuentaExistente() {
        // Primero preparamos el escenario añadiendo una cuenta
        Cuenta cuentaTest = new Cuenta();
        cuentaTest.setUsername("borrar_user");
        cuentaTest.setPassword("1234");
        cuentaTest.setTipoCuenta(TipoCuenta.POLICIA);
        Cuenta guardada = cuentaService.anadirCuenta(cuentaTest);

        // Ahora intentamos borrarla usando el ID que se le acaba de asignar
        boolean resultado = cuentaService.eliminarCuenta(guardada.getIdCuentas());

        assertTrue(resultado, "Debería devolver true al eliminar una cuenta que existe");
        assertEquals(0, cuentaDAO.count(), "La BD debería quedar vacía tras eliminar la cuenta");
    }

    // 4. Test para intentar eliminar una cuenta que NO existe
    @Test
    void eliminarCuentaNoExistente() {
        // Intentamos borrar el ID 999 (como la BD se limpia en el setUp, sabemos que no existe)
        boolean resultado = cuentaService.eliminarCuenta(999);

        assertFalse(resultado, "Debería devolver false al intentar eliminar una cuenta que no existe");
    }
}