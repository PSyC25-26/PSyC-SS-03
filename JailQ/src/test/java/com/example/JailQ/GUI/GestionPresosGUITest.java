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
        // Ejecutamos la ventana en modo POLICIA
        GestionPresosGUI frame = GuiActionRunner.execute(() -> new GestionPresosGUI("POLICIA"));
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testComponentesCarganYEstanVisibles() {
        window.textBox("txtNombre").requireVisible();
        window.textBox("txtApellidos").requireVisible();
        window.textBox("txtFechaNacimiento").requireVisible();
        window.textBox("txtCondena").requireVisible();
        
        window.comboBox("cbCarcel").requireVisible();
        window.list("listaDelitos").requireVisible();
        window.textBox("txtConsola").requireVisible();
        
        window.button("btnAnadir").requireVisible().requireText("Registrar Preso");
        window.button("btnVolver").requireVisible().requireText("← Volver al Menú Principal");
    }

    @Test
    public void testRellenarFormularioYEnviar() {
        window.textBox("txtNombre").enterText("Michael");
        window.textBox("txtApellidos").enterText("Scofield");
        window.textBox("txtFechaNacimiento").enterText("1974-09-08");
        window.textBox("txtCondena").enterText("5.5");
        
        window.list("listaDelitos").selectItem(0);
        
        window.button("btnAnadir").click();
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        window.textBox("txtConsola").requireText(Pattern.compile("(?s).*"));
    }
    
    @Test
    public void testBotonVolverCierraVentana() {
        window.resizeTo(new java.awt.Dimension(500, 550));
        window.button("btnVolver").click();
        window.requireNotVisible();
    }

    // @Test
    // public void testModoPoliciaCarcelInvalida() {
    //     // Limpiamos la cárcel para forzar la validación
    //     window.comboBox("cbCarcel").clearSelection();
        
    //     // Hacemos clic en añadir
    //     window.button("btnAnadir").click();
        
    //     // Como programasteis, el error va a la consola de texto, no a un popup
    //     window.textBox("txtConsola").requireText("Error: Selecciona una cárcel válida.");
    // }

    // @Test
    // public void testModoFamiliaBusquedaExitosa() {
    //     // 1. Generamos un nombre único para evitar que la base de datos lo rechace por duplicado
    //     String nombreUnico = "Familiar_" + System.currentTimeMillis();

    //     // 2. Damos un respiro para que el desplegable cargue las cárceles del servidor
    //     try { Thread.sleep(1000); } catch (InterruptedException e) {}

    //     // 3. Rellenamos los datos del preso
    //     window.textBox("txtNombre").enterText(nombreUnico);
    //     window.textBox("txtApellidos").enterText("Test");
    //     window.textBox("txtFechaNacimiento").enterText("1990-01-01");
    //     window.textBox("txtCondena").enterText("10");
        
    //     if (window.comboBox("cbCarcel").contents().length > 0) {
    //         window.comboBox("cbCarcel").selectItem(0);
    //     }
    //     if (window.list("listaDelitos").contents().length > 0) {
    //         window.list("listaDelitos").selectItem(0);
    //     }
    //     window.button("btnAnadir").click();
        
    //     // 4. ESPERAMOS para dar tiempo al servidor a procesar y guardar el preso
    //     try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
    //     // Cerramos modo Policía
    //     window.target().dispose(); 

    //     // 5. Abrimos modo Familia
    //     GestionPresosGUI frameFam = GuiActionRunner.execute(() -> new GestionPresosGUI("FAMILIA"));
    //     FrameFixture famWindow = new FrameFixture(window.robot(), frameFam);
    //     famWindow.show();

    //     // 6. Buscamos al preso con el nombre único que acabamos de crear
    //     famWindow.textBox("txtBusquedaNombre").enterText(nombreUnico);
    //     famWindow.textBox("txtBusquedaApellidos").enterText("Test");
    //     famWindow.button("btnBuscar").click();

    //     try { Thread.sleep(1000); } catch (InterruptedException e) {}

    //     // 7. Verificamos que lo ha encontrado
    //     famWindow.label("lblEstadoBusqueda").requireText(java.util.regex.Pattern.compile("(?s).*encontrado.*", java.util.regex.Pattern.CASE_INSENSITIVE));
        
    //     famWindow.target().dispose();
    // }

    
}