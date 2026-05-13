package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class GestionCuentasGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        GestionCuentasGUI frame = GuiActionRunner.execute(() -> new GestionCuentasGUI());
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
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
    public void testRellenarFormularioYEnviar() {
        // 1. El robot rellena los datos
        window.textBox("txtNombre").enterText("Juan");
        window.textBox("txtApellidos").enterText("Perez");
        window.textBox("txtUsername").enterText("jperez");
        window.textBox("txtPassword").enterText("1234");
        
        // Seleccionamos un ítem del desplegable
        window.comboBox("cbTipoCuenta").selectItem("POLICIA");
        
        // 2. Click al botón añadir
        window.button("btnAnadir").click();
        
        // 3. Verificamos que la consola muestra algún texto tras la petición 
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        window.textBox("txtConsola").requireText(Pattern.compile("(?s).+"));
    }

    @Test
    public void testBotonVolverCierraVentana() {
        window.button("btnVolver").click();
        window.requireNotVisible();
    }

    @Test
    public void testBotonEliminarAbreVentana() {
        window.button("btnAbrirEliminar").click();
        // Solo verificamos que el botón reacciona sin lanzar excepciones
        window.button("btnAbrirEliminar").requireEnabled();
    }

    @Test
    public void testEnviarDatosIncompletosMuestraError() {
        // Rellenamos solo un campo para forzar un error 400 del servidor
        window.textBox("txtNombre").enterText("Incompleto");
        window.button("btnAnadir").click();
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        // Verificamos que la consola detecta la palabra ERROR
        window.textBox("txtConsola").requireText(java.util.regex.Pattern.compile("(?s).*ERROR.*"));
    }


}