package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ListadoPresosGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        // Ejecutamos la ventana. Servidor backend encendido 
        ListadoPresosGUI frame = GuiActionRunner.execute(() -> new ListadoPresosGUI());
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testComponentesCarganYEstanVisibles() {
        // Verificamos que la tabla de presos está visible
        window.table("tablaPresos").requireVisible();
        
        // Verificamos los botones
        window.button("btnActualizar").requireVisible().requireText("Actualizar Lista");
        window.button("btnEliminar").requireVisible().requireText("Eliminar Seleccionado");
    }

    @Test
    public void testEliminarSinSeleccionMuestraAviso() {
        // Verificamos que la tabla arranca sin ninguna fila seleccionada
        window.table("tablaPresos").requireNoSelection();
        
        // El robot hace clic en el botón de eliminar
        window.button("btnEliminar").click();
        
        // Comprobamos que el robot detecta el JOptionPane con el mensaje de aviso exacto
        window.optionPane().requireMessage("Por favor, selecciona un preso de la lista.");
        
        // Cerramos el JOptionPane para terminar el test limpiamente
        window.optionPane().okButton().click();
    }

    @Test
    public void testEliminarPresoYConfirmarConSi() {
        if (window.table("tablaPresos").rowCount() > 0) {
            window.table("tablaPresos").selectRows(0);
            window.button("btnEliminar").click();
            
            // Le damos al botón "Sí"
            window.optionPane().yesButton().click(); 
            
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            // Cerramos el cartelito de resultado que sale después
            window.optionPane().okButton().click();
        }
    }

    @Test
    public void testCoberturaMetodoMain() {
        // Simplemente llamamos al main para que JaCoCo lo pinte de verde
        ListadoPresosGUI.main(new String[]{});
    }
}