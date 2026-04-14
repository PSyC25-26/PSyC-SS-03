package com.example.JailQ.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.JailQ.Dao.CuentaDAO;
import com.example.JailQ.Entidades.Cuenta;
import com.example.JailQ.Entidades.TipoCuenta;

@ExtendWith(MockitoExtension.class)
class CuentaServiceLoginTest {

    @Mock
    private CuentaDAO cuentaDAO;

    @InjectMocks
    private CuentaService cuentaService;

    private Cuenta cuentaPolicia;

    @BeforeEach
    void setUp() {
        cuentaPolicia = new Cuenta();
        cuentaPolicia.setUsername("policia1");
        cuentaPolicia.setPassword("1234");
        cuentaPolicia.setTipoCuenta(TipoCuenta.POLICIA);
    }

    /**
     * Test que verifica un login correcto de un policía.
     */
    @Test
    void testLoginPolicia_Correcto() {
        when(cuentaDAO.findAll()).thenReturn(Collections.singletonList(cuentaPolicia));

        Cuenta result = cuentaService.loginPolicia("policia1", "1234");

        assertNotNull(result);
        assertEquals("policia1", result.getUsername());
        assertEquals("1234", result.getPassword());
        assertEquals(TipoCuenta.POLICIA, result.getTipoCuenta());
        verify(cuentaDAO).findAll();
    }

    /**
     * Test que verifica que devuelve null si el username es incorrecto.
     */
    @Test
    void testLoginPolicia_UsernameIncorrecto() {
        when(cuentaDAO.findAll()).thenReturn(Collections.singletonList(cuentaPolicia));

        Cuenta result = cuentaService.loginPolicia("otroUsuario", "1234");

        assertNull(result);
        verify(cuentaDAO).findAll();
    }

    /**
     * Test que verifica que devuelve null si la contraseña es incorrecta.
     */
    @Test
    void testLoginPolicia_PasswordIncorrecta() {
        when(cuentaDAO.findAll()).thenReturn(Collections.singletonList(cuentaPolicia));

        Cuenta result = cuentaService.loginPolicia("policia1", "mal");

        assertNull(result);
        verify(cuentaDAO).findAll();
    }
}