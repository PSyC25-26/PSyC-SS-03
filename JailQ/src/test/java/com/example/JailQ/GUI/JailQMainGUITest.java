package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JailQMainGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        JailQMainGUI frame = GuiActionRunner.execute(() -> new JailQMainGUI());
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testComponentesCarganYEstanVisibles() {
        // Verificamos que los botones de los módulos están visibles
        window.button("btnCuentas").requireVisible();
        window.button("btnCarceles").requireVisible();
        window.button("btnPresos").requireVisible();
        
        // Verificamos el estado y los botones inferiores
        window.label("lblEstado").requireVisible();
        window.button("btnRefrescar").requireVisible().requireText("Reconectar");
        window.button("btnSesion").requireVisible().requireText("Iniciar sesión");
    }

    @Test
    public void testAccesoCuentasHabilitadoSinSesion() {
        // Al arrancar, el botón de cuentas siempre debe estar habilitado
        window.button("btnCuentas").requireEnabled();
    }

    @Test
    public void testBotonIniciarSesionAbreDialogo() {
        // Hacemos clic en Iniciar sesión
        window.button("btnSesion").click();
        
        // Verificamos que el diálogo modal de LoginDialog se ha abierto
        window.dialog().requireVisible();
        
        // Como no queremos hacer login completo en este test (eso ya lo prueba LoginDialogTest), 
        // simplemente cerramos el diálogo para dejar el estado limpio.
        window.dialog().close();
    }

    @Test
    public void testCerrarSesionHackeandoEstado() throws Exception {
        // 1. Accedemos a la ventana real de Java Swing
        JailQMainGUI mainFrame = (JailQMainGUI) window.target();
        
        // 2. Usamos reflexión para inyectar una sesión activa saltándonos el login
        java.lang.reflect.Field usernameField = JailQMainGUI.class.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(mainFrame, "Inspector_Gadget");
        
        java.lang.reflect.Field tipoCuentaField = JailQMainGUI.class.getDeclaredField("tipoCuenta");
        tipoCuentaField.setAccessible(true);
        tipoCuentaField.set(mainFrame, "POLICIA");
        
        // 3. Le pedimos al hilo de Swing que actualice los colores y textos de la ventana
        GuiActionRunner.execute(() -> {
            try {
                java.lang.reflect.Method aplicarCambio = JailQMainGUI.class.getDeclaredMethod("aplicarCambioSesion");
                aplicarCambio.setAccessible(true);
                aplicarCambio.invoke(mainFrame);
            } catch (Exception e) {}
        });
        
        // 4. El botón ahora debe decir "Cerrar sesión". ¡Hacemos clic!
        window.button("btnSesion").requireText("Cerrar sesión").click();
        
        // 5. El robot le da a "Sí" en el panel de confirmación
        window.optionPane().yesButton().click();
        
        // 6. Comprobamos que la sesión se borra y volvemos al estado original
        window.button("btnSesion").requireText("Iniciar sesión");
    }
}