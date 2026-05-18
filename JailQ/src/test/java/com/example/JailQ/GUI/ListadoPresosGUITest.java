package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.assertj.swing.timing.Timeout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        // Ejecutamos la ventana de Swing de forma segura en el Event Dispatch Thread (EDT)
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
        // Verificamos de forma estática que la tabla de presos está en la interfaz
        window.table("tablaPresos").requireVisible();
        
        // Verificamos los botones sin interactuar con la red
        window.button("btnActualizar").requireVisible().requireText("Actualizar lista");
        window.button("btnEliminar").requireVisible().requireText("Eliminar seleccionado");
    }

    @Test
    public void testEliminarSinSeleccionMuestraAviso() {
        window.table("tablaPresos").requireNoSelection();
        window.button("btnEliminar").click();
        
        // Espera asíncrona de hasta 5 segundos a que aparezca el diálogo modal
        JOptionPaneFixture optionPane = window.optionPane(Timeout.timeout(10000));
        optionPane.requireMessage("Selecciona un preso para eliminar.");
        optionPane.okButton().click();
    }

    @Test
    public void testEliminarPresoYConfirmarConSi() {
        // Ejecutar el bloque interactivo solo si hay datos reales del backend
        if (window.table("tablaPresos").rowCount() > 0) {
            window.table("tablaPresos").selectRows(0);
            window.button("btnEliminar").click();
            
            // Espera dinámica al cuadro de diálogo de confirmación (Sí/No)
            JOptionPaneFixture confirmPane = window.optionPane(Timeout.timeout(10000));
            confirmPane.yesButton().click(); 
            
            // Pausa de seguridad para que el HttpClient complete el envío síncrono al backend
            window.robot().waitForIdle();
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            
            // Cerramos el cartel secundario con el resultado del estado de red
            JOptionPaneFixture resultPane = window.optionPane(Timeout.timeout(10000));
            resultPane.okButton().click();
        }
    }

    @Test
    public void testBotonVolverCierraVentana() {
        window.button("btnVolver").click();
        window.robot().waitForIdle();
        window.requireNotVisible();
    }

    @Test
    public void testBotonActualizarLlamaAlBackend() {
        window.button("btnActualizar").click();
        
        // Tiempo de cortesía suficiente para que el HttpClient libere el hilo del EDT gráfica
        try { Thread.sleep(800); } catch (InterruptedException e) {}
        window.robot().waitForIdle();
        
        // Verificamos que el hilo de Swing sigue respondiendo y el botón no se queda bloqueado
        window.button("btnActualizar").requireEnabled();
    }

    @Test
    public void testTrasladarSinSeleccionMuestraAviso() {
        window.table("tablaPresos").requireNoSelection();
        window.button("btnTrasladar").click();
        
        JOptionPaneFixture optionPane = window.optionPane(Timeout.timeout(10000));
        optionPane.requireMessage("Selecciona un preso para trasladar.");
        optionPane.okButton().click();
    }

    @Test
    public void testModificarCondenaSinSeleccionMuestraAviso() {
        window.table("tablaPresos").requireNoSelection();
        window.button("btnModificarCondena").click();
        
        JOptionPaneFixture optionPane = window.optionPane(Timeout.timeout(10000));
        optionPane.requireMessage("Selecciona un preso para modificar su condena.");
        optionPane.okButton().click();
    }

    @Test
    public void testModificarCondenaValidacionesDeErrores() {
        if (window.table("tablaPresos").rowCount() > 0) {
            window.table("tablaPresos").selectRows(0);
            
            // 1. Error: Condena vacía
            window.button("btnModificarCondena").click();
            JOptionPaneFixture inputPane1 = window.optionPane(Timeout.timeout(10000));
            inputPane1.textBox().setText(""); 
            inputPane1.okButton().click();
            
            JOptionPaneFixture alertPane1 = window.optionPane(Timeout.timeout(10000));
            alertPane1.requireMessage("La condena no puede estar vacía.");
            alertPane1.okButton().click();

            // 2. Error: Letras en vez de números
            window.button("btnModificarCondena").click();
            JOptionPaneFixture inputPane2 = window.optionPane(Timeout.timeout(10000));
            inputPane2.textBox().setText("abc"); 
            inputPane2.okButton().click();
            
            JOptionPaneFixture alertPane2 = window.optionPane(Timeout.timeout(10000));
            alertPane2.requireMessage("La condena debe ser un número entero.");
            alertPane2.okButton().click();

            // 3. Error: Condena cero o negativa
            window.button("btnModificarCondena").click();
            JOptionPaneFixture inputPane3 = window.optionPane(Timeout.timeout(10000));
            inputPane3.textBox().setText("0"); 
            inputPane3.okButton().click();
            
            JOptionPaneFixture alertPane3 = window.optionPane(Timeout.timeout(10000));
            alertPane3.requireMessage("La condena debe ser mayor que 0.");
            alertPane3.okButton().click();
            
            // 4. Salida limpia: Cancelar flujo
            window.button("btnModificarCondena").click();
            JOptionPaneFixture inputPane4 = window.optionPane(Timeout.timeout(10000));
            inputPane4.cancelButton().click();
        }
    }

    @Test
    public void testTrasladarPresoCancelar() {
        if (window.table("tablaPresos").rowCount() > 0) {
            window.table("tablaPresos").selectRows(0);
            window.button("btnTrasladar").click();
            
            // Margen de maniobra amplio para simular la descarga HTTP interna del Combobox
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            window.robot().waitForIdle();
            
            // Capturamos el panel de selección de cárceles y cancelamos
            JOptionPaneFixture inputPane = window.optionPane(Timeout.timeout(10000));
            inputPane.cancelButton().click();
        }
    }
}