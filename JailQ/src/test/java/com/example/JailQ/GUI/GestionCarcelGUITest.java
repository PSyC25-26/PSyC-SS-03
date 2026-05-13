package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class GestionCarcelGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        GestionCarcelGUI frame = GuiActionRunner.execute(() -> new GestionCarcelGUI());
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
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
}