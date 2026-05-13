package com.example.JailQ.GUI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {

    @BeforeEach
    public void setUp() {
        // Nos aseguramos de que cada test empieza con la sesión limpia
        SessionManager.getInstance().cerrarSesion();
    }

    @Test
    public void testSingletonGarantizaUnicaInstancia() {
        SessionManager instancia1 = SessionManager.getInstance();
        SessionManager instancia2 = SessionManager.getInstance();

        assertSame(instancia1, instancia2, "El patrón Singleton debe devolver exactamente el mismo objeto en memoria.");
        assertFalse(instancia1.haySesion(), "Al arrancar la aplicación no debería haber ninguna sesión activa.");
        assertNull(instancia1.getUsername(), "El username inicial debe ser null.");
        assertNull(instancia1.getTipoCuenta(), "El tipo de cuenta inicial debe ser null.");
    }

    @Test
    public void testIniciarYCerrarSesion() {
        SessionManager sesion = SessionManager.getInstance();

        // 1. Iniciamos sesión
        sesion.iniciarSesion("Inspector_Gadget", "POLICIA");

        // 2. Verificamos que los datos se guardan
        assertTrue(sesion.haySesion(), "Debe detectar que la sesión está activa.");
        assertEquals("Inspector_Gadget", sesion.getUsername(), "El username debe coincidir.");
        assertEquals("POLICIA", sesion.getTipoCuenta(), "El tipo de cuenta debe coincidir.");

        // 3. Cerramos sesión
        sesion.cerrarSesion();

        // 4. Verificamos el borrado de datos
        assertFalse(sesion.haySesion(), "Tras cerrar, la sesión no debe figurar como activa.");
        assertNull(sesion.getUsername(), "El username debe purgarse.");
        assertNull(sesion.getTipoCuenta(), "El tipo de cuenta debe purgarse.");
    }
}