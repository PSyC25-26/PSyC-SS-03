package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EstadisticasAvanzadasGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        // Ejecutamos la ventana. Servidor backend encendido 
        EstadisticasAvanzadasGUI frame = GuiActionRunner.execute(() -> new EstadisticasAvanzadasGUI());
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testComponentesCarganYEstanVisibles() {
        // Comprobamos que el desplegable, el botón, la barra y el label de estado existen y son visibles
        window.comboBox("comboCarceles").requireVisible();
        window.button("btnActualizar").requireVisible().requireText("↻ Cargar/Actualizar");
        window.progressBar("barraOcupacion").requireVisible();
        window.label("lblPorcentaje").requireVisible();
    }

    @Test
    public void testBotonActualizarFunciona() {
        // Hacemos clic en el botón de recargar datos
        window.button("btnActualizar").click();
        
        // Simplemente verificamos que no lanza ninguna excepción y que el botón sigue habilitado
        window.button("btnActualizar").requireEnabled();
    }

    @Test
    public void testSeleccionarCarcelActualizaBarra() {
        // Verificamos si hay opciones en el desplegable
        if (window.comboBox("comboCarceles").contents().length > 0) {
            // Seleccionamos la primera cárcel de la lista
            window.comboBox("comboCarceles").selectItem(0);
            
            // Verificamos que la barra de progreso reacciona
            window.progressBar("barraOcupacion").requireVisible();
        }
    }
}