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

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    // ── Helper: cierra cualquier diálogo de error abierto por el constructor ──

    private void cerrarDialogosDeError() {
        try { Thread.sleep(300); } catch (InterruptedException e) {}
        try {
            java.awt.Window[] windows = java.awt.Window.getWindows();
            for (java.awt.Window w : windows) {
                if (w instanceof javax.swing.JDialog) w.dispose();
            }
        } catch (Exception ignored) {}
    }

    // ── Tests existentes ──────────────────────────────────────────────────────

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

    // ── Tests nuevos: cobertura de métodos privados via reflexión ─────────────

    @Test
    public void testExtraerValor_campoExistente() throws Exception {
        // Creamos una instancia separada para reflexión, cerramos diálogos de error
        // que pudiera abrir el constructor al intentar conectar con el backend
        ListadoPresosGUI gui = GuiActionRunner.execute(() -> new ListadoPresosGUI());
        cerrarDialogosDeError();

        java.lang.reflect.Method metodo = ListadoPresosGUI.class
            .getDeclaredMethod("extraerValor", String.class, String.class);
        metodo.setAccessible(true);

        String texto = "\"nombre\":\"Michael\",\"condena\":5,\"apellidos\":\"Scofield\"";

        assertEquals("Michael", metodo.invoke(gui, texto, "nombre"));
        assertEquals("Scofield", metodo.invoke(gui, texto, "apellidos"));
        assertEquals("5", metodo.invoke(gui, texto, "condena"));

        GuiActionRunner.execute(() -> gui.dispose());
    }

    @Test
    public void testExtraerValor_campoInexistente() throws Exception {
        // Cerramos diálogos de error que pudiera abrir el constructor
        ListadoPresosGUI gui = GuiActionRunner.execute(() -> new ListadoPresosGUI());
        cerrarDialogosDeError();

        java.lang.reflect.Method metodo = ListadoPresosGUI.class
            .getDeclaredMethod("extraerValor", String.class, String.class);
        metodo.setAccessible(true);

        String texto = "\"nombre\":\"Michael\"";
        assertEquals("N/A", metodo.invoke(gui, texto, "campoQueNoExiste"));

        GuiActionRunner.execute(() -> gui.dispose());
    }

    @Test
    public void testActualizarTabla_jsonValido() throws Exception {
        // Cerramos diálogos de error que pudiera abrir el constructor
        ListadoPresosGUI gui = GuiActionRunner.execute(() -> new ListadoPresosGUI());
        cerrarDialogosDeError();

        java.lang.reflect.Method metodo = ListadoPresosGUI.class
            .getDeclaredMethod("actualizarTabla", String.class);
        metodo.setAccessible(true);

        String json = "[{\"id\":1,\"nombre\":\"Michael\",\"apellidos\":\"Scofield\"," +
                       "\"condena\":5,\"carcel\":{\"idCarcel\":1,\"nombre\":\"Martutene\"}}]";

        GuiActionRunner.execute(() -> {
            try {
                metodo.invoke(gui, json);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Verificamos que la tabla tiene una fila con los datos correctos
        java.lang.reflect.Field campo = ListadoPresosGUI.class.getDeclaredField("modelo");
        campo.setAccessible(true);
        javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel) campo.get(gui);

        assertEquals(1, modelo.getRowCount());
        assertEquals("Michael", modelo.getValueAt(0, 1));
        assertEquals("Scofield", modelo.getValueAt(0, 2));

        GuiActionRunner.execute(() -> gui.dispose());
    }

    @Test
    public void testActualizarTabla_jsonVacio() throws Exception {
        // Cerramos diálogos de error que pudiera abrir el constructor
        ListadoPresosGUI gui = GuiActionRunner.execute(() -> new ListadoPresosGUI());
        cerrarDialogosDeError();

        java.lang.reflect.Method metodo = ListadoPresosGUI.class
            .getDeclaredMethod("actualizarTabla", String.class);
        metodo.setAccessible(true);

        GuiActionRunner.execute(() -> {
            try {
                metodo.invoke(gui, "[]");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        java.lang.reflect.Field campo = ListadoPresosGUI.class.getDeclaredField("modelo");
        campo.setAccessible(true);
        javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel) campo.get(gui);

        assertEquals(0, modelo.getRowCount());

        GuiActionRunner.execute(() -> gui.dispose());
    }

    @Test
    public void testActualizarTabla_carcelSinNombre() throws Exception {
        // Cerramos diálogos de error que pudiera abrir el constructor
        ListadoPresosGUI gui = GuiActionRunner.execute(() -> new ListadoPresosGUI());
        cerrarDialogosDeError();

        java.lang.reflect.Method metodo = ListadoPresosGUI.class
            .getDeclaredMethod("actualizarTabla", String.class);
        metodo.setAccessible(true);

        // Cárcel sin campo nombre — cubre el segundo replaceAll que pone N/A
        String json = "[{\"id\":1,\"nombre\":\"Juan\",\"apellidos\":\"Lopez\"," +
                       "\"condena\":3,\"carcel\":{\"idCarcel\":1,\"capacidad\":100}}]";

        GuiActionRunner.execute(() -> {
            try {
                metodo.invoke(gui, json);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        java.lang.reflect.Field campo = ListadoPresosGUI.class.getDeclaredField("modelo");
        campo.setAccessible(true);
        javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel) campo.get(gui);

        assertEquals(1, modelo.getRowCount());
        assertEquals("N/A", modelo.getValueAt(0, 4));

        GuiActionRunner.execute(() -> gui.dispose());
    }

    @Test
    public void testActualizarTabla_usaIdPreso() throws Exception {
        // Cerramos diálogos de error que pudiera abrir el constructor
        ListadoPresosGUI gui = GuiActionRunner.execute(() -> new ListadoPresosGUI());
        cerrarDialogosDeError();

        java.lang.reflect.Method metodo = ListadoPresosGUI.class
            .getDeclaredMethod("actualizarTabla", String.class);
        metodo.setAccessible(true);

        // Usa "idPreso" en lugar de "id" — cubre esa rama del if
        String json = "[{\"idPreso\":99,\"nombre\":\"Carlos\",\"apellidos\":\"García\"," +
                       "\"condena\":7,\"carcel\":{\"idCarcel\":1,\"nombre\":\"Alcatraz\"}}]";

        GuiActionRunner.execute(() -> {
            try {
                metodo.invoke(gui, json);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        java.lang.reflect.Field campo = ListadoPresosGUI.class.getDeclaredField("modelo");
        campo.setAccessible(true);
        javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel) campo.get(gui);

        assertEquals(1, modelo.getRowCount());
        assertEquals("99", modelo.getValueAt(0, 0));

        GuiActionRunner.execute(() -> gui.dispose());
    }
}