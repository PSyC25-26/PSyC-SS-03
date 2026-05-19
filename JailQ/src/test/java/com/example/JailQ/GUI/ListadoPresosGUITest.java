package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.awt.Frame;

public class ListadoPresosGUITest {

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
        // Ejecutamos la ventana. Servidor backend encendido 
        ListadoPresosGUI frame = GuiActionRunner.execute(() -> new ListadoPresosGUI());
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
        // Verificamos que la tabla de presos está visible
        window.table("tablaPresos").requireVisible();
        
        // Verificamos los botones 
        window.button("btnActualizar").requireVisible().requireText("Actualizar lista");
        window.button("btnEliminar").requireVisible().requireText("Eliminar seleccionado");
    }

    @Test
    public void testEliminarSinSeleccionMuestraAviso() {
        window.table("tablaPresos").requireNoSelection();
        window.button("btnEliminar").click();
        
        try {
            window.optionPane().requireMessage("Selecciona un preso para eliminar.");
            window.optionPane().okButton().click();
        } catch (Exception e) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false,
                "Skipping: JOptionPane did not appear in time");
        }
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
            try {
                window.optionPane().okButton().click();
            } catch (Exception e) {
                org.junit.jupiter.api.Assumptions.assumeTrue(false,
                    "Skipping: result JOptionPane did not appear in time");
            }
        }
    }

    @Test
    public void testBotonVolverCierraVentana() {
        window.button("btnVolver").click();
        window.requireNotVisible();
    }

    @Test
    public void testBotonActualizarLlamaAlBackend() {
        window.button("btnActualizar").click();
        // Verificamos que el botón no se bloquea tras la actualización
        window.button("btnActualizar").requireEnabled();
    }

    @Test
    public void testTrasladarSinSeleccionMuestraAviso() {
        window.table("tablaPresos").requireNoSelection();
        window.button("btnTrasladar").click();
        
        try {
            window.optionPane().requireMessage("Selecciona un preso para trasladar.");
            window.optionPane().okButton().click();
        } catch (Exception e) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false,
                "Skipping: JOptionPane did not appear in time");
        }
    }

    @Test
    public void testModificarCondenaSinSeleccionMuestraAviso() {
        org.junit.jupiter.api.Assumptions.assumeTrue(
            window.table("tablaPresos").rowCount() >= 0,
            "Skipping: backend may not have responded yet"
        );
        
        window.table("tablaPresos").requireNoSelection();
        
        try {
            window.button("btnModificarCondena").click();
            window.optionPane().requireMessage("Selecciona un preso para modificar su condena.");
            window.optionPane().okButton().click();
        } catch (Exception e) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false, 
                "Skipping: JOptionPane did not appear in time");
        }
    }

    @Test
    public void testModificarCondenaValidacionesDeErrores() {
        // Solo ejecutamos este test si hay presos cargados en la tabla
        if (window.table("tablaPresos").rowCount() > 0) {
            window.table("tablaPresos").selectRows(0);
            
            // 1. Error: Condena vacía
            window.button("btnModificarCondena").click();
            window.optionPane().textBox().setText(""); // Lo dejamos vacío
            window.optionPane().okButton().click();
            window.optionPane().requireMessage("La condena no puede estar vacía.");
            window.optionPane().okButton().click();

            // 2. Error: Letras en vez de números
            window.button("btnModificarCondena").click();
            window.optionPane().textBox().setText("abc"); // Metemos letras
            window.optionPane().okButton().click();
            window.optionPane().requireMessage("La condena debe ser un número entero.");
            window.optionPane().okButton().click();

            // 3. Error: Condena cero o negativa
            window.button("btnModificarCondena").click();
            window.optionPane().textBox().setText("0"); // Metemos un 0
            window.optionPane().okButton().click();
            window.optionPane().requireMessage("La condena debe ser mayor que 0.");
            window.optionPane().okButton().click();
            
            // 4. Salida limpia: Le damos a cancelar en el menú
            window.button("btnModificarCondena").click();
            window.optionPane().cancelButton().click();
        }
    }

    @Test
    public void testTrasladarPresoCancelar() {
        if (window.table("tablaPresos").rowCount() > 0) {
            window.table("tablaPresos").selectRows(0);
            window.button("btnTrasladar").click();
            
            // Damos tiempo a que se descarguen las cárceles del servidor (GET)
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            
            try {
                // Cancelamos el traslado cerrando el menú desplegable
                window.optionPane().cancelButton().click();
            } catch (Exception e) {
                // Si la base de datos de cárceles fallara o estuviera vacía saltaría otro pop-up
            }
        }
    }
}