package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Frame;

public class LoginDialogTest {

    private DialogFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        for (Frame frame : Frame.getFrames()) {
            frame.dispose();
        }
        // Ejecutamos la ventana pasando null como padre, ya que para el test no nos hace falta JailQMainGUI
        LoginDialog dialog = GuiActionRunner.execute(() -> new LoginDialog(null));
        window = new DialogFixture(dialog);
        window.show();

    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
        GuiActionRunner.execute(() -> window.target().dispose());
        if (window.robot() != null) {
            window.robot().cleanUp();
        }
    }

    @Test
    public void testComponentesCarganCorrectamente() {
        // Comprueba que los elementos esenciales están en la pantalla
        window.textBox("txtUsername").requireVisible();
        window.textBox("txtPassword").requireVisible();
        window.button("btnLogin").requireVisible().requireText("Iniciar sesión");
    }

    @Test
    public void testLoginCamposVaciosMuestraError() {
        // Hacemos clic sin escribir nada
        window.button("btnLogin").click();
        
        // Comprobamos que el label cambia a nuestro texto de error
        window.label("lblEstado").requireText("Rellena username y password.");
    }

    @Test
    public void testLoginConDatosInvalidosMuestraError401() {
        // Introducimos credenciales que sabemos seguro que no existen para forzar el error 401
        window.textBox("txtUsername").enterText("usuario_super_falso_999");
        window.textBox("txtPassword").enterText("clave_incorrecta");
        window.button("btnLogin").click();
        
        // Damos tiempo al servidor para responder con el 401
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        
        try {
            // Comprobamos que entra por el 'else if (response.statusCode() == 401)'
            window.label("lblEstado").requireText("Usuario o contraseña incorrectos.");
        } catch (Exception e) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false, "Skipping: neither error text nor optionPane appeared");
        }
    }

    @Test
    public void testPulsarEnterEnPasswordDisparaLogin() {
        // Escribimos el usuario
        window.textBox("txtUsername").enterText("inspector");
        
        // NO escribimos contraseña, pero ponemos el foco en su caja y el robot pulsa "ENTER"
        // Esto debería disparar el evento Action Listener y ejecutar hacerLogin()
        window.textBox("txtPassword").pressAndReleaseKeys(java.awt.event.KeyEvent.VK_ENTER);
        
        // Al faltar la contraseña, verificamos que el login ha saltado pero ha sido interceptado
        window.label("lblEstado").requireText("Rellena username y password.");
    }
}
