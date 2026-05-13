package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EliminarPoliciaGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        // Ejecutamos la ventana. Servidor backend encendido 
        EliminarPoliciaGUI frame = GuiActionRunner.execute(() -> new EliminarPoliciaGUI());
        window = new FrameFixture(frame);
        window.show(); 
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testComponentesCarganCorrectamente() {
        // Comprueba que los elementos esenciales (botones, lista y estado) están visibles
        window.list("listaPolicias").requireVisible();
        window.button("btnRecargar").requireVisible().requireText("Recargar");
        window.button("btnEliminar").requireVisible().requireText("Eliminar seleccionado");
        window.label("lblEstado").requireVisible();
    }

    @Test
    public void testEliminarSinSeleccionarMuestraAviso() {
        // Nos aseguramos de que no hay nada seleccionado en la lista
        window.list("listaPolicias").clearSelection();
        
        // Hacemos clic en el botón de eliminar
        window.button("btnEliminar").click();
        
        // Comprobamos que el robot detecta el JOptionPane de advertencia
        window.optionPane().requireWarningMessage().requireMessage("Selecciona una cuenta de policía.");
        
        // El robot le da a "OK" para cerrar el mensaje y que el test termine limpiamente
        window.optionPane().okButton().click();
    }

    @Test
    public void testEliminarPoliciaYDarleACancelar() {
        // Comprobamos si la base de datos ha cargado algún policía en la lista
        if (window.list("listaPolicias").contents().length > 0) {
            // Seleccionamos el primero
            window.list("listaPolicias").selectItem(0);
            window.button("btnEliminar").click();
            
            // Verificamos que sale la pregunta
            window.optionPane().requireQuestionMessage();
            
            // El robot hace clic en "NO"
            window.optionPane().noButton().click();
            
            // La lista debe seguir intacta y visible
            window.list("listaPolicias").requireVisible();
        }
    }

    @Test
    public void testEliminarPoliciaYConfirmarConSi() {
        if (window.list("listaPolicias").contents().length > 0) {
            window.list("listaPolicias").selectItem(0);
            window.button("btnEliminar").click();
            
            // Le damos al botón "Sí"
            window.optionPane().yesButton().click(); 
            
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            window.optionPane().okButton().click();
        }
    }
}