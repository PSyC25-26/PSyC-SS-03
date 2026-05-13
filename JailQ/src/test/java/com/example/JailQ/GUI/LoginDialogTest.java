package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginDialogTest {

    private DialogFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        // Ejecutamos la ventana pasando null como padre, ya que para el test no nos hace falta JailQMainGUI
        LoginDialog dialog = GuiActionRunner.execute(() -> new LoginDialog(null));
        window = new DialogFixture(dialog);
        window.show(); 
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
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

}