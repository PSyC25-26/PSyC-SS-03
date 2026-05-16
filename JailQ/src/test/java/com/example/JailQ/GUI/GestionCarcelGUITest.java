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

public class GestionCarcelGUITest {

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
        GestionCarcelGUI frame = GuiActionRunner.execute(() -> new GestionCarcelGUI());
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
        // Verificamos que todos los campos del formulario están visibles
        window.textBox("txtNombre").requireVisible();
        window.textBox("txtDescripcion").requireVisible();
        window.textBox("txtLocalidad").requireVisible();
        window.textBox("txtCapacidad").requireVisible();
        
        // Verificamos que los botones y la consola están visibles
        window.button("btnAnadir").requireVisible().requireText("Añadir Cárcel");
        window.button("btnEstadisticas").requireVisible().requireText("Ver Estadísticas");
        window.button("btnVolver").requireVisible().requireText("← Volver al Menú Principal");
        window.textBox("txtConsola").requireVisible();
    }

    @Test
    public void testRellenarFormularioYEnviar() {
        // 1. El robot rellena el formulario de la cárcel
        window.textBox("txtNombre").enterText("Prisión de Prueba");
        window.textBox("txtDescripcion").enterText("Centro penitenciario de prueba para el test de interfaz");
        window.textBox("txtLocalidad").enterText("Ciudad Test");
        window.textBox("txtCapacidad").enterText("500");
        
        // 2. El robot pulsa el botón de añadir
        window.button("btnAnadir").click();
        
        // 3. Verificamos que la consola se actualiza con algún mensaje 
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        window.textBox("txtConsola").requireText(Pattern.compile("(?s).+"));
    }

    @Test
    public void testBotonVolverCierraVentana() {
        // Hacemos clic en volver para cubrir su evento
        window.button("btnVolver").click();
        
        // Comprobamos que la ventana responde cerrándose (o al menos ocultándose)
        window.requireNotVisible();
    }

    @Test
    public void testBotonEstadisticasAbreVentana() {
        // Hacemos clic en el botón de estadísticas
        window.button("btnEstadisticas").click();
        
        // Verificamos que la interfaz no crashea y el botón sigue habilitado
        window.button("btnEstadisticas").requireEnabled();
    }

    @Test
    public void testEnviarDatosIncompletosMuestraError() {
        // 1. Forzamos un error mandando datos vacíos o incorrectos
        window.textBox("txtNombre").enterText(""); 
        // Capacidad debería ser un número. Mandamos letras para asegurar un Error 400 Bad Request
        window.textBox("txtCapacidad").enterText("invalido"); 
        
        // 2. El robot pulsa el botón de añadir
        window.button("btnAnadir").click();
        
        // 3. Damos tiempo al servidor y verificamos que entra por el 'else' y pinta ERROR
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        window.textBox("txtConsola").requireText(Pattern.compile("(?s).*ERROR.*"));
    }
}