package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class FiltrarPresosPorDelitoGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        FiltrarPresosPorDelitoGUI frame = GuiActionRunner.execute(() -> new FiltrarPresosPorDelitoGUI());
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testComponentesVisiblesYCargados() {
        window.comboBox("comboDelitos").requireVisible();
        window.button("btnFiltrar").requireVisible();
        window.button("btnVolver").requireVisible();
        window.textBox("areaResultados").requireVisible();
        window.label("lblEstado").requireVisible();
    }

    @Test
    public void testBotonVolverCierraVentana() {
        window.button("btnVolver").click();
        window.requireNotVisible();
    }

    @Test
    public void testFiltrarPorDelitoSolicitaAlBackend() {
        if (window.comboBox("comboDelitos").contents().length > 0) {
            // Seleccionamos el primer delito de la lista
            window.comboBox("comboDelitos").selectItem(0);
            
            // Pulsamos filtrar
            window.button("btnFiltrar").click();

            // Esperamos un poco para la respuesta HTTP (ya sea un JSON vacío o con datos)
            try { Thread.sleep(800); } catch (InterruptedException e) {}

            // Verificamos que el label de estado ha reaccionado (se actualiza la UI)
            window.label("lblEstado").requireText(Pattern.compile("(?s).+"));
        }
    }
}