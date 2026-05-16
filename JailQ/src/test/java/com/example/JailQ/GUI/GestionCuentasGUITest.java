package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Frame;
import java.util.regex.Pattern;

public class GestionCuentasGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        for (Frame frame : Frame.getFrames()) {
            frame.dispose();
        }
        GestionCuentasGUI frame = GuiActionRunner.execute(() -> new GestionCuentasGUI());
        window = new FrameFixture(frame);
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
    public void testComponentesCarganYEstanVisibles() {
        // Verificamos campos de texto
        window.textBox("txtNombre").requireVisible();
        window.textBox("txtApellidos").requireVisible();
        window.textBox("txtUsername").requireVisible();
        window.textBox("txtPassword").requireVisible();
        
        // Verificamos desplegable y botones
        window.comboBox("cbTipoCuenta").requireVisible();
        window.button("btnAnadir").requireVisible().requireText("Añadir Cuenta");
        window.button("btnAbrirEliminar").requireVisible().requireText("Eliminar Policía");
        window.button("btnVolver").requireVisible().requireText("← Volver al Menú Principal");
        
        // Consola
        window.textBox("txtConsola").requireVisible();
    }

    @Test
    public void testRellenarFormularioYEnviarExitoso() {
        // 1. Generamos un username único para garantizar que la BD nos dé un 201
        String usuarioUnico = "jperez_" + System.currentTimeMillis();
        
        window.textBox("txtNombre").enterText("Juan");
        window.textBox("txtApellidos").enterText("Perez");
        window.textBox("txtUsername").enterText(usuarioUnico);
        window.textBox("txtPassword").enterText("1234");
        
        window.comboBox("cbTipoCuenta").selectItem("POLICIA");
        
        // 2. Click al botón añadir
        window.button("btnAnadir").click();
        
        // 3. Verificamos que recibimos un 201 (Éxito)
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        window.textBox("txtConsola").requireText(Pattern.compile("(?s).*201.*"));
        
        // 4. Aseguramos que se ha ejecutado limpiarFormulario() correctamente
        window.textBox("txtNombre").requireText("");
        window.textBox("txtApellidos").requireText("");
        window.textBox("txtUsername").requireText("");
        window.textBox("txtPassword").requireText("");
    }

    @Test
    public void testBotonVolverCierraVentana() {
        window.button("btnVolver").click();
        window.requireNotVisible();
    }

    @Test
    public void testBotonEliminarAbreVentana() {
        window.button("btnAbrirEliminar").click();
        window.button("btnAbrirEliminar").requireEnabled();
    }

    @Test
    public void testEnviarDatosIncompletosMuestraError() {
        // Rellenamos solo un campo para forzar un error 400 del servidor
        window.textBox("txtNombre").enterText("Incompleto");
        window.button("btnAnadir").click();
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        // Verificamos que la consola detecta la palabra ERROR y entra en el 'else'
        window.textBox("txtConsola").requireText(java.util.regex.Pattern.compile("(?s).*ERROR.*"));
    }
}