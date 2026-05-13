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

    @Test
    public void testSeleccionNulaNoLanzaError() {
        // Limpiamos la selección. Esto hace que 'seleccion' sea null
        // y nos asegura de que el "if (seleccion != null)" funciona correctamente sin crashear.
        window.comboBox("comboCarceles").clearSelection();
        window.label("lblPorcentaje").requireVisible();
    }

    @Test
    public void testColoresBarraSegunOcupacion() {
        // Obtenemos el JComboBox real de Java Swing
        javax.swing.JComboBox combo = window.comboBox("comboCarceles").target();

        GuiActionRunner.execute(() -> {
            // Limpiamos los datos reales que hayan venido del servidor
            combo.removeAllItems();

            // 1. Caso Verde (< 75%) -> 50%
            EstadisticasAvanzadasGUI.CarcelDatos verde = new EstadisticasAvanzadasGUI.CarcelDatos();
            verde.nombre = "Prision Verde";
            verde.capacidad = 100;
            verde.ocupacion = 50;
            combo.addItem(verde);

            // 2. Caso Naranja (>= 75% y < 90%) -> 80%
            EstadisticasAvanzadasGUI.CarcelDatos naranja = new EstadisticasAvanzadasGUI.CarcelDatos();
            naranja.nombre = "Prision Naranja";
            naranja.capacidad = 100;
            naranja.ocupacion = 80;
            combo.addItem(naranja);

            // 3. Caso Rojo (>= 90%) -> 95%
            EstadisticasAvanzadasGUI.CarcelDatos rojo = new EstadisticasAvanzadasGUI.CarcelDatos();
            rojo.nombre = "Prision Roja";
            rojo.capacidad = 100;
            rojo.ocupacion = 95;
            combo.addItem(rojo);
        });

        // Hacemos que el robot seleccione cada una para que ejecute todos los 'if' de colores
        window.comboBox("comboCarceles").selectItem("Prision Verde");
        window.label("lblPorcentaje").requireText(java.util.regex.Pattern.compile(".*50.*"));

        window.comboBox("comboCarceles").selectItem("Prision Naranja");
        window.label("lblPorcentaje").requireText(java.util.regex.Pattern.compile(".*80.*"));

        window.comboBox("comboCarceles").selectItem("Prision Roja");
        window.label("lblPorcentaje").requireText(java.util.regex.Pattern.compile(".*95.*"));
    }
}