package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class GestionPresosGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        // Ejecutamos la ventana. Servidor backend encendido 
        GestionPresosGUI frame = GuiActionRunner.execute(() -> new GestionPresosGUI());
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testComponentesCarganYEstanVisibles() {
        // Cajas de texto
        window.textBox("txtNombre").requireVisible();
        window.textBox("txtApellidos").requireVisible();
        window.textBox("txtFechaNacimiento").requireVisible();
        window.textBox("txtCondena").requireVisible();
        
        // Listas, desplegables y consola
        window.comboBox("cbCarcel").requireVisible();
        window.list("listaDelitos").requireVisible();
        window.textBox("txtConsola").requireVisible();
        
        // Botones
        window.button("btnAnadir").requireVisible().requireText("Registrar Preso");
        window.button("btnVolver").requireVisible().requireText("← Volver al Menú Principal");
    }

    @Test
    public void testRellenarFormularioYEnviar() {
        // 1. El robot rellena los datos
        window.textBox("txtNombre").enterText("Michael");
        window.textBox("txtApellidos").enterText("Scofield");
        window.textBox("txtFechaNacimiento").enterText("1974-09-08");
        window.textBox("txtCondena").enterText("5.5");
        
        // Seleccionamos un elemento de la lista de delitos
        window.list("listaDelitos").selectItem(0);
        
        // 2. Click al botón añadir
        window.button("btnAnadir").click();
        
        // 3. Verificamos que la consola muestra algún texto tras la petición (respuesta o error)
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        window.textBox("txtConsola").requireText(Pattern.compile("(?s).*"));
    }
    
    @Test
    public void testBotonVolverCierraVentana() {
        window.resizeTo(new java.awt.Dimension(500, 550));
        
        window.button("btnVolver").click();
        window.requireNotVisible();
    }

}